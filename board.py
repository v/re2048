from enum import Enum
import random
SIZE = 4

Move = Enum('RIGHT', 'LEFT', 'UP', 'DOWN')

class Board:

    def __init__(self):
        self.cells = [0] * SIZE * SIZE

    def __str__(self):
        result = ""

        for i in range(0, SIZE):
            for j in range(0, SIZE):
                index = i * SIZE + j

                if self.cells[i][j]:
                    result += "  %5d  " % (self.cells[index])
                else:
                    result += "  %5s  " % "."

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
        while blank < size:
            while blank < size:
                if self.cells[row][blank] == 0:
                    break
                blank += 1

            nonblank = blank + 1

            while nonblank < size:
                if self.cells[row][nonblank] != 0:
                    break
                nonblank += 1

            if  blank >= size or nonblank >= size:
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
            count += slide_right(row)

            # sums up things next to each other.
            for col in range(SIZE-1, 0, -1):
                if self.cells[row][col] != 0:
                    #we have a collision.
                    if self.cells[row][col - 1] == self.cells[row][col]:
                        self.cells[row][col] = self.cells[row][col - 1] + 1
                        self.cells[row][col - 1] = 0

                        count += 1

            count += slide_right(row)
        return count

    def move_left(self):
        count = 0

        for row in range(0, SIZE):
            count += slide_left(row)

            #sums up things next to each other.
            for col in range(0, SIZE):
                if self.cells[row][col] != 0:
                    #we have a collision.
                    if self.cells[row][col] == self.cells[row][col + 1]:
                        self.cells[row][col] = self.cells[row][col + 1] + 1
                        self.cells[row][col + 1] = 0

                        count += 1

            count += slide_left(row)
        return count

    def move_up(self):
        count = 0
        for col in range(0, SIZE):
            count += slide_up(col)

            #sums up things next to each other.
            for row in range(0, SIZE):
                if self.cells[row][col] != 0:
                    #we have a collision.
                    if self.cells[row][col] == self.cells[row + 1][col]:
                        self.cells[row + 1][col] = self.cells[row][col] + 1
                        self.cells[row][col] = 0

                        count += 1

            count += slideUp(col)
        return count

    def fill_random(self):
        blank_cells = 0
        for row in range(0, SIZE):
            for col in range(0, SIZE):
                if self.cells[row][col] == 0:
                    blank_cells += 1

        rand = random.nextint(0, blank_cells)

        for row in range(0, SIZE):
            for col in range(0, SIZE):
                if self.cells[row][col] == 0:
                    if rand == 0:
                        dice = r.nextInt(10)

                        if  dice == 9:
                            self.cells[row][col] = 2
                        else:
                            self.cells[row][col] = 1
                        return
                    rand -= 1

    def move_down(self):
        count = 0
        for col in range(0, SIZE):
            count += slide_down(col)

            #sums up things next to each other.
            for row in range(SIZE - 1, 0, -1):
                if self.cells[row][col] != 0:
                    #we have a collision.
                    if self.cells[row][col] == self.cells[row - 1][col]:
                        self.cells[row - 1][col] = self.cells[row][col] + 1
                        self.cells[row][col] = 0

                        count += 1

            count += slide_down(col)

        return count

    def move(move):
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

