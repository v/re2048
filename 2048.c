#include <stdio.h>
#include "board.h"

int main()
{
    board_t board = board_init();

    board_fill_random(&board);

    int play = 1;

    while (play)
    {
        board_print(board);
        printf("enter your move:");

        char buffer[1024];

        if (fgets(buffer, 1024, stdin) == NULL)
            break;

        switch(buffer[0])
        {
            case 'h':
                board_move(&board, LEFT);
                break;
            case 'j':
                board_move(&board, DOWN);
                break;
            case 'k':
                board_move(&board, UP);
                break;
            case 'l':
                board_move(&board, RIGHT);
                break;
            default:
                play = 0;
                break;
        }
    }

}
