#define _POSIX_C_SOURCE 199309L
#include <stdio.h>
#include "board.h"
#include <stdlib.h>
#include <time.h>

int main()
{
    srand(time(NULL));

    time_t start;
    time(&start);

    for(int i=0; i < 1e6; i++)
    {
        board_t board = board_init();

        board_fill_random(&board);

        int play = 1;
        while (play && !board_check_game_over(&board))
        {
            //board_print(board);

            //int c = getchar();
            int c = rand() % 4;
            switch(c)
            {
                case 'h':
                case 68:
                case 0:
                    board_move(&board, LEFT);
                    break;
                case 'j':
                case 66:
                case 1:
                    board_move(&board, DOWN);
                    break;
                case 'k':
                case 65:
                case 2:
                    board_move(&board, UP);
                    break;
                case 'l':
                case 67:
                case 3:
                    board_move(&board, RIGHT);
                    break;
                default:
                    play = 0;
                    break;
            }
        }
        /*if (i % 1000 == 0)*/
            /*printf("Iteration %i\n", i);*/
        //printf("The game is over:\n");
        //board_print(board);
    }

    time_t end;
    time(&end);

    printf("Time elapsed is %0f\n", difftime(end, start));
}
