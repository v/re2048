#include <inttypes.h>
#include <stdbool.h>

#define SIZE 4

#define UP 0
#define DOWN 1
#define LEFT 2
#define RIGHT 3

typedef struct board_t {
    int cells[SIZE][SIZE];
} board_t;

board_t board_init();
void board_print(board_t);
void board_move(board_t*, int);

int board_move_right(board_t *board);
int board_move_left(board_t *board);
int board_move_up(board_t *board);
int board_move_down(board_t *board);

bool board_check_game_over(board_t *board);

void board_fill_random(board_t *board);

uint64_t board_convert(board_t board);

void setBufferedInput(bool);
