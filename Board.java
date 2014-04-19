import java.util.Random;

public class Board
{
    int cells[][];
    private static int SIZE = 4;
    public static enum MOVE { LEFT, RIGHT, UP, DOWN };
    Random r;

    public Board(Random r)
    {
        cells = new int[SIZE][SIZE];
        this.r = r;
    }

    public String toString()
    {
        String result = "";
        for(int i=0; i<cells.length; i++)
        {
            for(int j=0; j < cells[i].length; j++)
            {
                if (this.cells[i][j] > 0)
                    result += "  "+ (1 << this.cells[i][j])+ "  ";
                else
                    result += "  .  ";
            }

            result += "\n";
        }

        return result;
    }


    private int slideRight(int row)
    {
        int count = 0;

        int blank = SIZE - 1;
        while (blank >= 0)
        {
            while(blank >= 0)
            {
                if(this.cells[row][blank] == 0)
                    break;
                blank--;
            }

            int nonblank = blank - 1;

            while(nonblank >= 0)
            {
                if(this.cells[row][nonblank] != 0)
                    break;
                nonblank--;
            }

            if (blank < 0 || nonblank < 0)
                break;

            this.cells[row][blank] = this.cells[row][nonblank];
            this.cells[row][nonblank] = 0;
            count++;

            blank--;
            nonblank--;
        }

        return count;
    }


    private int slideLeft(int row)
    {
        int count = 0;
        int blank = 0;
        while (blank < SIZE)
        {
            while(blank < SIZE)
            {
                if(this.cells[row][blank] == 0)
                    break;
                blank++;
            }

            int nonblank = blank + 1;

            while(nonblank < SIZE)
            {
                if(this.cells[row][nonblank] != 0)
                    break;
                nonblank++;
            }

            if (blank >= SIZE || nonblank >= SIZE)
                break;

            this.cells[row][blank] = this.cells[row][nonblank];
            this.cells[row][nonblank] = 0;

            count++;

            blank++;
            nonblank++;
        }

        return count;
    }

    private int slideUp(int col)
    {
        int count = 0;
        int blank = 0;
        while (blank < SIZE)
        {
            while(blank < SIZE)
            {
                if(this.cells[blank][col] == 0)
                    break;
                blank++;
            }

            int nonblank = blank + 1;

            while(nonblank < SIZE)
            {
                if(this.cells[nonblank][col] != 0)
                    break;
                nonblank++;
            }

            if (blank >= SIZE || nonblank >= SIZE)
                break;

            this.cells[blank][col] = this.cells[nonblank][col];
            this.cells[nonblank][col] = 0;

            count++;

            blank++;
            nonblank++;
        }

        return count;
    }

    private int slideDown(int col)
    {
        int count = 0;
        int blank = SIZE - 1;
        while (blank >= 0)
        {
            while(blank >= 0)
            {
                if(this.cells[blank][col] == 0)
                    break;
                blank--;
            }

            int nonblank = blank - 1;

            while(nonblank >= 0)
            {
                if(this.cells[nonblank][col] != 0)
                    break;
                nonblank--;
            }

            if (blank < 0 || nonblank < 0)
                break;

            this.cells[blank][col] = this.cells[nonblank][col];
            this.cells[nonblank][col] = 0;

            count++;

            blank--;
            nonblank--;
        }
        return count;
    }

    private int moveRight()
    {
        int count = 0;
        for(int row=0; row < SIZE; row++)
        {
            count += slideRight(row);

            //sums up things next to each other.
            for(int col=SIZE - 1; col > 0; col--)
            {
                if(this.cells[row][col] != 0)
                {
                    //we have a collision.
                    if(this.cells[row][col - 1] == this.cells[row][col])
                    {
                        this.cells[row][col] = this.cells[row][col - 1] + 1;
                        this.cells[row][col - 1] = 0;

                        count += 1;
                    }
                }
            }

            count += slideRight(row);
        }
        return count;
    }

    private int moveLeft()
    {
        int count = 0;
        for(int row=0; row < SIZE; row++)
        {
            count += slideLeft(row);

            //sums up things next to each other.
            for(int col=0; col < SIZE - 1; col++)
            {
                if(this.cells[row][col] != 0)
                {
                    //we have a collision.
                    if(this.cells[row][col] == this.cells[row][col + 1])
                    {
                        this.cells[row][col] = this.cells[row][col + 1] + 1;
                        this.cells[row][col + 1] = 0;

                        count += 1;
                    }
                }
            }

            count += slideLeft(row);
        }
        return count;
    }

    private int moveUp()
    {
        int count = 0;
        for(int col=0; col < SIZE; col++)
        {
            count += slideUp(col);

            //sums up things next to each other.
            for(int row=0; row < SIZE - 1; row++)
            {
                if(this.cells[row][col] != 0)
                {
                    //we have a collision.
                    if(this.cells[row][col] == this.cells[row + 1][col])
                    {
                        this.cells[row + 1][col] = this.cells[row][col] + 1;
                        this.cells[row][col] = 0;

                        count += 1;
                    }
                }
            }

            count += slideUp(col);
        }
        return count;
    }

    public void fillRandom()
    {
        int blank_cells = 0;
        for (int row=0; row < SIZE; row++)
        {
            for(int col=0; col < SIZE; col++)
            {
                if(this.cells[row][col] == 0)
                    blank_cells++;
            }
        }

        int random = r.nextInt(blank_cells);

        for (int row=0; row < SIZE; row++)
        {
            for(int col=0; col < SIZE; col++)
            {
                if(this.cells[row][col] == 0)
                {
                    if(random-- == 0)
                    {
                        int dice = r.nextInt(10);

                        if (dice == 9)
                            this.cells[row][col] = 2;
                        else
                            this.cells[row][col] = 1;
                        return;
                    }
                }
            }
        }
    }

    private int moveDown()
    {
        int count = 0;
        for(int col=0; col < SIZE; col++)
        {
            count += slideDown(col);

            //sums up things next to each other.
            for(int row=SIZE - 1; row > 0; row--)
            {
                if(this.cells[row][col] != 0)
                {
                    //we have a collision.
                    if(this.cells[row][col] == this.cells[row - 1][col])
                    {
                        this.cells[row - 1][col] = this.cells[row][col] + 1;
                        this.cells[row][col] = 0;

                        count += 1;
                    }
                }
            }

            count += slideDown(col);
        }

        return count;
    }

    public void move(MOVE move)
    {
        int count = 0;
        if (move == MOVE.RIGHT)
        {
            count = moveRight();
        }
        else if (move == MOVE.LEFT)
        {
            count = moveLeft();
        }
        else if (move == MOVE.UP)
        {
            count = moveUp();
        }
        else if (move == MOVE.DOWN)
        {
            count = moveDown();
        }

        if (count > 0)
            fillRandom();
    }

    public long encode()
    {
        long result = 0;
        for (int row=0; row < SIZE; row++)
        {
            for (int col=0; col < SIZE; col+=2)
            {
                int first = this.cells[row][col];
                int second = this.cells[row][col + 1];

                int left_part = ((first << 28) >>> 24);
                int right_part = ((second << 28) >>> 28);

                char b = (char) (left_part | right_part);

                result = result << 8;
                result = result + b;
            }
        }

        return result;
    }

    public void draw()
    {
        for (int row=0; row < SIZE; row++)
        {
            for (int col=0; col < SIZE; col++)
            {
                System.out.printf("%d", this.cells[row][col]);
            }
        }
    }

    public boolean checkGameOver()
    {
        //check for horizontal moves.
        for(int row=0; row < SIZE; row++)
        {
            for(int col=0; col < SIZE - 1; col++)
            {
                if(this.cells[row][col] == this.cells[row][col + 1])
                    return false;
            }
        }

        for(int col=0; col < SIZE; col++)
        {
            for(int row=0; row < SIZE - 1; row++)
            {
                if(this.cells[row][col] == this.cells[row + 1][col])
                    return false;
            }
        }

        for(int row=0; row < SIZE; row++)
        {
            for(int col=0; col < SIZE; col++)
            {
                if(this.cells[row][col] == 0)
                    return false;
            }
        }

        return true;
    }

    public int maxValue()
    {
        int max = 0;

        for (int row=0; row < SIZE; row++)
        {
            for (int col=0; col < SIZE; col++)
            {
                if (max < this.cells[row][col])
                    max = this.cells[row][col];
            }
        }

        return max;
    }

    public int monotonicity()
    {
        int result = 0;

        for(int row=0; row < SIZE; row++)
        {
            for(int col=0; col < SIZE - 1; col++)
            {
                result += Math.abs(this.cells[row][col] - this.cells[row][col + 1]);
            }
        }

        for(int col=0; col < SIZE; col++)
        {
            for(int row=0; row < SIZE - 1; row++)
            {
                result += Math.abs(this.cells[row][col] - this.cells[row + 1][col]);
            }
        }

        return result;
    }

    public int blankCells()
    {
        int result = 0;

        for(int row=0; row < SIZE; row++)
        {
            for(int col=0; col < SIZE - 1; col++)
            {
                if(this.cells[row][col] == 0)
                    result++;
            }
        }

        return result;
    }
}
