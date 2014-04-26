from enum import Enum
import random
from copy import deepcopy

SIZE = 4

Move = Enum('RIGHT', 'LEFT', 'UP', 'DOWN')

class Board:

    def __init__(self):
        self.cells = []

        for i in range(0, SIZE):
            self.cells.append([0] * SIZE)

    @classmethod
    def from_cells(cls, cells):
        board = Board()
        board.cells = cells

        return board

    def __str__(self):
        result = ""

        for i in range(0, SIZE):
            for j in range(0, SIZE):
                if self.cells[i][j]:
                    result += "  %5d  " % (1 << self.cells[i][j])
                else:
                    result += "  %5s  " % "."
            result += "\n"

        return result

    def slide_right(self, row):
        count = 0

        blank = SIZE - 1
        while  blank >= 0:
            while blank >= 0:
                if self.cells[row][blank] == 0:
                    break
                blank -= 1

            nonblank = blank - 1

            while nonblank >= 0:
                if self.cells[row][nonblank] != 0:
                    break
                nonblank -= 1

            if blank < 0 or nonblank < 0:
                break

            self.cells[row][blank] = self.cells[row][nonblank]
            self.cells[row][nonblank] = 0
            count += 1

            blank -= 1
            nonblank -= 1

        return count

    def slide_left(self, row):
        count = 0
        blank = 0
        while blank < SIZE:
            while blank < SIZE:
                if self.cells[row][blank] == 0:
                    break
                blank += 1

            nonblank = blank + 1

            while nonblank < SIZE:
                if self.cells[row][nonblank] != 0:
                    break
                nonblank += 1

            if blank >= SIZE or nonblank >= SIZE:
                break

            self.cells[row][blank] = self.cells[row][nonblank]
            self.cells[row][nonblank] = 0

            count += 1

            blank += 1
            nonblank += 1

        return count

    def slide_up(self, col):
        count = 0
        blank = 0
        while blank < SIZE:
            while blank < SIZE:
                if self.cells[blank][col] == 0:
                    break
                blank += 1

            nonblank = blank + 1

            while nonblank < SIZE:
                if self.cells[nonblank][col] != 0:
                    break
                nonblank += 1

            if  blank >= SIZE or nonblank >= SIZE:
                break

            self.cells[blank][col] = self.cells[nonblank][col]
            self.cells[nonblank][col] = 0

            count += 1

            blank += 1
            nonblank += 1

        return count

    def slide_down(self, col):
        count = 0
        blank = SIZE - 1
        while blank >= 0:
            while blank >= 0:
                if self.cells[blank][col] == 0:
                    break
                blank -= 1

            nonblank = blank - 1

            while nonblank >= 0:
                if self.cells[nonblank][col] != 0:
                    break
                nonblank -= 1

            if blank < 0 or nonblank < 0:
                break

            self.cells[blank][col] = self.cells[nonblank][col]
            self.cells[nonblank][col] = 0

            count += 1

            blank -= 1
            nonblank -= 1
        return count

    def move_right(self):
        count = 0

        for row in range(0, SIZE):
            count += self.slide_right(row)

            # sums up things next to each other.
            for col in range(SIZE-1, 0, -1):
                if self.cells[row][col] != 0:
                    #we have a collision.
                    if self.cells[row][col - 1] == self.cells[row][col]:
                        self.cells[row][col] = self.cells[row][col - 1] + 1
                        self.cells[row][col - 1] = 0

                        count += 1

            count += self.slide_right(row)
        return count

    def move_left(self):
        count = 0

        for row in range(0, SIZE):
            count += self.slide_left(row)

            #sums up things next to each other.
            for col in range(0, SIZE - 1):
                if self.cells[row][col] != 0:
                    #we have a collision.
                    if self.cells[row][col] == self.cells[row][col + 1]:
                        self.cells[row][col] = self.cells[row][col + 1] + 1
                        self.cells[row][col + 1] = 0

                        count += 1

            count += self.slide_left(row)
        return count

    def move_up(self):
        count = 0
        for col in range(0, SIZE):
            count += self.slide_up(col)

            #sums up things next to each other.
            for row in range(0, SIZE - 1):
                if self.cells[row][col] != 0:
                    #we have a collision.
                    if self.cells[row][col] == self.cells[row + 1][col]:
                        self.cells[row + 1][col] = self.cells[row][col] + 1
                        self.cells[row][col] = 0

                        count += 1

            count += self.slide_up(col)
        return count

    def fill_random(self):
        blank_cells = 0
        for row in range(0, SIZE):
            for col in range(0, SIZE):
                if self.cells[row][col] == 0:
                    blank_cells += 1

        rand = random.randrange(0, blank_cells)

        for row in range(0, SIZE):
            for col in range(0, SIZE):
                if self.cells[row][col] == 0:
                    if rand == 0:
                        dice = random.randrange(0, 10)

                        if  dice == 9:
                            self.cells[row][col] = 2
                        else:
                            self.cells[row][col] = 1
                        return
                    rand -= 1

    def move_down(self):
        count = 0
        for col in range(0, SIZE):
            count += self.slide_down(col)

            #sums up things next to each other.
            for row in range(SIZE - 1, 0, -1):
                if self.cells[row][col] != 0:
                    #we have a collision.
                    if self.cells[row][col] == self.cells[row - 1][col]:
                        self.cells[row - 1][col] = self.cells[row][col] + 1
                        self.cells[row][col] = 0

                        count += 1

            count += self.slide_down(col)

        return count

    def move(self, move):
        count = 0
        if move == Move.RIGHT:
            count = self.move_right()
        elif move == Move.LEFT:
            count = self.move_left()
        elif move == Move.UP:
            count = self.move_up()
        elif move == Move.DOWN:
            count = self.move_down()

        if count > 0:
            self.fill_random()


    def check_game_over(self):
        return len(self.valid_moves()) == 0

    def max_value(self):
        result = 0

        for row in self.cells:
            result = max(result, max(row))

        return result

    def valid_moves(self):
        result = []

        moves = set()

        # check for horizontal moves.
        for row in range(SIZE):
            for col in range(SIZE - 1):
                # both are non blank.
                if self.cells[row][col] > 0 and self.cells[row][col] == self.cells[row][col + 1]:
                    moves.add(Move.RIGHT)
                    moves.add(Move.LEFT)
                # right guy is blank, left guy is not.
                elif self.cells[row][col] > 0 and self.cells[row][col + 1] == 0:
                    moves.add(Move.RIGHT)
                elif self.cells[row][col] == 0 and self.cells[row][col + 1] > 0:
                    moves.add(Move.LEFT)


        # check for vertical moves.
        for col in range(SIZE):
            for row in range(SIZE - 1):
                if self.cells[row][col] > 0 and self.cells[row][col] == self.cells[row + 1][col]:
                    moves.add(Move.UP)
                    moves.add(Move.DOWN)
                elif self.cells[row][col] > 0 and self.cells[row + 1][col] == 0:
                    moves.add(Move.DOWN)
                elif self.cells[row][col] == 0 and self.cells[row + 1][col] > 0:
                    moves.add(Move.UP)

        return list(moves)

    def state(self):
        result = [1]

        for row in self.cells:
            result += row

        return result
