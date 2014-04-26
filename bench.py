from board import Board, Move
import random
import time

ITERATIONS = int(1e5)

max_values = [0] * 12

start = time.time()

for i in xrange(0, ITERATIONS):
    board = Board()

    board.fill_random()

    while not board.check_game_over():
        move = random.randrange(0, 4)

        if move == 0:
            board.move(Move.UP)
        elif move == 1:
            board.move(Move.DOWN)
        elif move == 2:
            board.move(Move.LEFT)
        elif move == 3:
            board.move(Move.RIGHT)

    max_value =  board.max_value()

    max_values[max_value] += 1

end = time.time()

print "total time ", (end - start)

for i, value in enumerate(max_values):
    print (1 << i), " ", value
