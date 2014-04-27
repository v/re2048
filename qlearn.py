from board import Board, Move, SIZE
import random
import numpy as np
from pylab import plot, show
from copy import deepcopy

from sklearn.linear_model.stochastic_gradient import SGDRegressor
from sklearn.externals import joblib

LINEAR_REGULARIZATION = 0.0001

EXPLORATION_RATE = 0.2
ITERATIONS = int(1e3)
Q_LEARNING_RATE = 0.1
Q_DISCOUNT_FACTOR = 0.9


def run_game(qtable):
    board = Board()
    board.fill_random()

    count = 0

    states = []

    while not board.check_game_over():
        choice = random.random()

        valid_moves = board.valid_moves()

        if choice > EXPLORATION_RATE:
            # explot
            move = get_best_move(qtable, board, valid_moves)
        else:
            move = random.choice(valid_moves)

        board.move(move)

        states.append(Board.from_cells(board.cells))

        count += 1

    x_states = []
    y_states = []

    i = 0

    while i < len(states):
        board = states[i]

        x_state = board.state()

        old_value = qtable.predict(x_state)[0]
        reward = 0

        new_value = old_value

        if i < len(states) - 1:

            reward = 100
            reward += board.monotonicity() * 1
            reward += board.blank_cells() * 1

            next_board = states[i + 1]

            new_value += Q_LEARNING_RATE * (reward + Q_DISCOUNT_FACTOR * get_optimal_value(qtable, next_board) - old_value)

        else:
            reward = -10000
            new_value += Q_LEARNING_RATE * (reward - old_value)

        x_states.append(x_state)
        y_states.append(new_value)

        count -= 1
        i += 1




    # give a high reward to the final state.
    # y_states[-1] = 1000
    return np.array(x_states), np.array(y_states)

def get_optimal_value(qtable, board):
    valid_moves = board.valid_moves()

    if not valid_moves == 0:
        return 0

    return _compute_best_move_and_val(qtable, board, valid_moves)[1]

# returns the move that gives us the best worst case performance.
def get_best_move(qtable, board, valid_moves):
    return _compute_best_move_and_val(qtable, board, valid_moves)[0]


def _compute_best_move_and_val(qtable, board, valid_moves):
    best_val = 0

    best_move = random.choice(valid_moves)

    for move in valid_moves:
        copy = Board.from_cells(board.cells)

        # compute the expected qvalue of this move.
        if move == Move.RIGHT:
            copy.move_right()
        elif move == Move.LEFT:
            copy.move_left()
        elif move == Move.UP:
            copy.move_up()
        elif move == Move.DOWN:
            copy.move_down()


        # this is the worst outcome for making this move.
        worst_val = 1e7

        # fill in a random square one at a time.
        for i in range(SIZE):
            for j in range(SIZE):
                if copy.cells[i][j] == 0:
                    newcells = deepcopy(copy.cells)

                    for values in [2, 4]:
                        newcells[i][j] = 2
                        newboard = Board.from_cells(newcells)

                        value = qtable.predict(newboard.state())

                        if value[0] < worst_val:
                            worst_val = value[0]

        if worst_val > best_val:
            best_val = worst_val
            best_move = move

    return best_move, best_val

def main():
    qtable = SGDRegressor(loss="squared_loss", alpha=LINEAR_REGULARIZATION)
    qtable.fit([[1]*17], np.array([1]))

    errors = []

    for i in xrange(ITERATIONS):
        X, Y = run_game(qtable)

        qtable.fit(np.array(X), np.array(Y))

        error = qtable.score(np.array(X), np.array(Y))

        errors.append(error)

        if i % 200 == 0:
            print i, "iterations"

    joblib.dump(qtable, 'model.pk1')
    times = np.arange(0, len(errors))

    print errors
    plot(times, errors, 'o')
    show()


if __name__ == '__main__':
    main()
