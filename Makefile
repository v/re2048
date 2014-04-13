CFLAGS=-g -Wall -Wextra -std=c99

CC = gcc

2048: board.o 2048.c
	$(CC) -o 2048 board.o 2048.c

board.o: board.c
	$(CC) -c $(CFLAGS) board.c

clean:
	rm -f board.o 2048
