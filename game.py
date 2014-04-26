from board import Board, Move
from getch import _Getch

getch = _Getch()

def main():
    board = Board()
    board.fill_random()

    while not board.check_game_over():
        print board
        move = getch()

        if move in ('w', 'k'):
            board.move(Move.UP)
        elif move in ('s', 'j'):
            board.move(Move.DOWN)
        elif move in ('a', 'h'):
            board.move(Move.LEFT)
        elif move in ('d', 'l'):
            board.move(Move.RIGHT)

    print board

if __name__ == '__main__':
    main()
