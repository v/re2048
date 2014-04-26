from board import Board, Move
import random
import numpy as np

from sklearn.linear_model.stochastic_gradient import SGDRegressor
from sklearn.externals import joblib

LINEAR_REGULARIZATION = 0.01

EXPLORATION_RATE = 0.8
ITERATIONS = int(1e4)
Q_LEARNING_RATE = 0.1
Q_DISCOUNT_FACTOR = 0.9

qtable = SGDRegressor(loss="squared_loss", alpha=LINEAR_REGULARIZATION)
qtable.fit([[1]*18], np.array([1]))

def run_game():
    board = Board()
    board.fill_random()

    count = 0

    states = []

    while not board.check_game_over():
        choice = random.random()

        valid_moves = board.valid_moves()

        if choice > EXPLORATION_RATE:
            # explot
            move = get_best_move(board, valid_moves)
        else:
            move = random.choice(valid_moves)

        board.move(move)

        states.append(
                [Board.from_cells(board.cells), move]
                )
        count += 1

    x_states = []
    y_states = []

    i = 0

    while i < len(states):
        board, move = states[i]

        x_state = board.state()
        x_state.append(move.index)

        old_value = qtable.predict(x_state)[0]

        reward = 0

        new_value = old_value

        if i < len(states) - 1:
            reward = 1

            next_board = states[i + 1][0]

            new_value += Q_LEARNING_RATE * (reward + Q_DISCOUNT_FACTOR * get_optimal_value(next_board) - old_value)

        else:
            reward = -1000
            new_value += Q_LEARNING_RATE * (reward - old_value)

        x_states.append(x_state)
        y_states.append(new_value)

        count -= 1
        i += 1




    # give a high reward to the final state.
    # y_states[-1] = 1000
    return np.array(x_states), np.array(y_states)

def get_optimal_value(board):
    valid_moves = board.valid_moves()

    if not valid_moves:
        return 0

    best_val = 0
    best_move = random.choice(valid_moves)

    state = board.state()
    state.append(0)

    for move in valid_moves:
        state[-1] = move.index

        result = qtable.predict(state)

        if result[0] > best_val:
            best_val = result[0]
            best_move = move

    return best_val



def get_best_move(board, valid_moves):
    best_val = 0
    state = board.state()

    state.append(0)

    best_move = random.choice(valid_moves)

    for move in valid_moves:
        state[-1] = move.index
        result = qtable.predict(state)

        if result[0] > best_val:
            best_val = result[0]
            best_move = move

    return best_move

def main():
    for i in xrange(ITERATIONS):
        X, Y = run_game()

        qtable.fit(np.array(X), np.array(Y))

        if i % 200 == 0:
            print i, "iterations"

    joblib.dump(qtable, 'model.pk1')


if __name__ == '__main__':
    main()
