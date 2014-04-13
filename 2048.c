#include <stdio.h>
#include "board.h"

int main()
{
    board_t board = board_init();

    board_fill_random(&board);

    int play = 1;

    setBufferedInput(0);
    while (play && !board_check_game_over(&board))
    {
        board_print(board);

        /*char buffer[1024];*/

        /*if (fgets(buffer, 1024, stdin) == NULL)*/
            /*break;*/

        //switch(buffer[0])
        int c = getchar();
        printf("%d \n", c);
        switch(c)
        {
            case 'h':
            case 68:
                board_move(&board, LEFT);
                break;
            case 'j':
            case 66:
                board_move(&board, DOWN);
                break;
            case 'k':
            case 65:
                board_move(&board, UP);
                break;
            case 'l':
            case 67:
                board_move(&board, RIGHT);
                break;
            default:
                play = 0;
                break;
        }
    }

    printf("The game is over:\n");
    board_print(board);
    setBufferedInput(1);
}
