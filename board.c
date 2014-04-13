#include "board.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>

board_t board_init()
{
    board_t board;
    memset(&board, 0, sizeof board);

    return board;
}

void board_print(board_t board)
{
    for(int i=0; i < SIZE; i++)
    {
        for(int j=0; j < SIZE; j++)
        {
            if (board.cells[i][j])
                printf("   %5d   ", 1 << board.cells[i][j]);
            else
                printf("   %5s   ", ".");
        }
        printf("\n");
    }
}

int slide_right(board_t *board, int row)
{
    int count = 0;

    int blank = SIZE - 1;
    while (blank >= 0)
    {
        while(blank >= 0)
        {
            if(!board->cells[row][blank])
                break;
            blank--;
        }

        int nonblank = blank - 1;

        while(nonblank >= 0)
        {
            if(board->cells[row][nonblank])
                break;
            nonblank--;
        }

        if (blank < 0 || nonblank < 0)
            break;

        board->cells[row][blank] = board->cells[row][nonblank];
        board->cells[row][nonblank] = 0;
        count++;

        blank--;
        nonblank--;
    }

    return count;
}

int slide_left(board_t *board, int row)
{
    int count = 0;
    int blank = 0;
    while (blank < SIZE)
    {
        while(blank < SIZE)
        {
            if(!board->cells[row][blank])
                break;
            blank++;
        }

        int nonblank = blank + 1;

        while(nonblank < SIZE)
        {
            if(board->cells[row][nonblank])
                break;
            nonblank++;
        }

        if (blank >= SIZE || nonblank >= SIZE)
            break;

        board->cells[row][blank] = board->cells[row][nonblank];
        board->cells[row][nonblank] = 0;

        count++;

        blank++;
        nonblank++;
    }

    return count;
}

int slide_up(board_t *board, int col)
{
    int count = 0;
    int blank = 0;
    while (blank < SIZE)
    {
        while(blank < SIZE)
        {
            if(!board->cells[blank][col])
                break;
            blank++;
        }

        int nonblank = blank + 1;

        while(nonblank < SIZE)
        {
            if(board->cells[nonblank][col])
                break;
            nonblank++;
        }

        if (blank >= SIZE || nonblank >= SIZE)
            break;

        board->cells[blank][col] = board->cells[nonblank][col];
        board->cells[nonblank][col] = 0;

        count++;

        blank++;
        nonblank++;
    }

    return count;
}

int slide_down(board_t *board, int col)
{
    int count = 0;
    int blank = SIZE - 1;
    while (blank >= 0)
    {
        while(blank >= 0)
        {
            if(!board->cells[blank][col])
                break;
            blank--;
        }

        int nonblank = blank - 1;

        while(nonblank >= 0)
        {
            if(board->cells[nonblank][col])
                break;
            nonblank--;
        }

        if (blank < 0 || nonblank < 0)
            break;

        board->cells[blank][col] = board->cells[nonblank][col];
        board->cells[nonblank][col] = 0;

        count++;

        blank--;
        nonblank--;
    }
    return count;
}

int board_move_right(board_t *board)
{
    int count = 0;
    for(int row=0; row < SIZE; row++)
    {
        count += slide_right(board, row);

        //sums up things next to each other.
        for(int col=SIZE - 1; col > 0; col--)
        {
            if(board->cells[row][col])
            {
                //we have a collision.
                if(board->cells[row][col - 1] == board->cells[row][col])
                {
                    board->cells[row][col] = board->cells[row][col - 1] + 1;
                    board->cells[row][col - 1] = 0;

                    count += 1;
                }
            }
        }

        count += slide_right(board, row);
    }
    return count;
}

int board_move_left(board_t *board)
{
    int count = 0;
    for(int row=0; row < SIZE; row++)
    {
        count += slide_left(board, row);

        //sums up things next to each other.
        for(int col=0; col < SIZE - 1; col++)
        {
            if(board->cells[row][col])
            {
                //we have a collision.
                if(board->cells[row][col] == board->cells[row][col + 1])
                {
                    board->cells[row][col] = board->cells[row][col + 1] + 1;
                    board->cells[row][col + 1] = 0;

                    count += 1;
                }
            }
        }

        count += slide_left(board, row);
    }
    return count;
}

int board_move_up(board_t *board)
{
    int count = 0;
    for(int col=0; col < SIZE; col++)
    {
        count += slide_up(board, col);

        //sums up things next to each other.
        for(int row=0; row < SIZE - 1; row++)
        {
            if(board->cells[row][col])
            {
                //we have a collision.
                if(board->cells[row][col] == board->cells[row + 1][col])
                {
                    board->cells[row + 1][col] = board->cells[row][col] + 1;
                    board->cells[row][col] = 0;

                    count += 1;
                }
            }
        }

        count += slide_up(board, col);
    }
    return count;
}

void board_fill_random(board_t *board)
{
    int blank_cells = 0;
    for (int row=0; row < SIZE; row++)
    {
        for(int col=0; col < SIZE; col++)
        {
            if(!board->cells[row][col])
                blank_cells++;
        }
    }

    int random = rand() % blank_cells;

    for (int row=0; row < SIZE; row++)
    {
        for(int col=0; col < SIZE; col++)
        {
            if(!board->cells[row][col])
            {
                if(random-- == 0)
                {
                    int dice = rand() % 10;

                    if (dice == 9)
                        board->cells[row][col] = 2;
                    else
                        board->cells[row][col] = 1;
                    return;
                }
            }
        }
    }
}

int board_move_down(board_t *board)
{
    int count = 0;
    for(int col=0; col < SIZE; col++)
    {
        count += slide_down(board, col);

        //sums up things next to each other.
        for(int row=SIZE - 1; row > 0; row--)
        {
            if(board->cells[row][col])
            {
                //we have a collision.
                if(board->cells[row][col] == board->cells[row - 1][col])
                {
                    board->cells[row - 1][col] = board->cells[row][col] + 1;
                    board->cells[row][col] = 0;

                    count += 1;
                }
            }
        }

        count += slide_down(board, col);
    }

    return count;
}

void board_move(board_t *board, int move)
{
    int count = 0;
    if (move == RIGHT)
    {
        count = board_move_right(board);
    }
    else if (move == LEFT)
    {
        count = board_move_left(board);
    }
    else if (move == UP)
    {
        count = board_move_up(board);
    }
    else if (move == DOWN)
    {
        count = board_move_down(board);
    }

    if (count > 0)
        board_fill_random(board);
}

uint64_t board_convert(board_t board)
{
    uint64_t result = 0;
    for (int row=0; row < SIZE; row++)
    {
        for (int col=0; col < SIZE; col+=2)
        {
            int first = board.cells[row][col];
            int second = board.cells[row][col + 1];

            char byte = (char) (((first << 28) >> 24) | ((second << 28) >> 28));

            result = result << 8;
            result = result + byte;
        }
    }

    return result;
}

void board_draw(board_t *board)
{
    printf("\033[H");

    for (int row=0; row < SIZE; row++)
    {
        for (int col=0; col < SIZE; col++)
        {
            printf("%d", board->cells[row][col]);
        }
    }
}

bool board_check_game_over(board_t *board)
{
    //check for horizontal moves.
    for(int row=0; row < SIZE; row++)
    {
        for(int col=0; col < SIZE - 1; col++)
        {
            if(board->cells[row][col] == board->cells[row][col + 1])
                return false;
        }
    }

    for(int col=0; col < SIZE; col++)
    {
        for(int row=0; row < SIZE - 1; row++)
        {
            if(board->cells[row][col] == board->cells[row + 1][col])
                return false;
        }
    }

    for(int row=0; row < SIZE; row++)
    {
        for(int col=0; col < SIZE; col++)
        {
            if(!board->cells[row][col])
                return false;
        }
    }

    return true;
}
