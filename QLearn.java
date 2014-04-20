import java.util.Random;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.ArrayList;

class QTable extends HashMap<Integer, EnumMap<Board.MOVE, Double>>
{

    public QTable(int capacity)
    {
        super(capacity);
    }

    public String toString()
    {
        String result = "";

        result += "Qtable containing "+this.size()+ " games.";

        //for (long state: this.keySet())
        //{
            //EnumMap<Board.MOVE, Double> states = this.get(state);
            //for (Board.MOVE move: states.keySet())
            //{
                //result += (Long.toHexString(state) + " " + move + ": "+states.get(move));
                //result += "\n";
            //}
        //}

        return result;
    }
}


public class QLearn
{
    private static final double EXPLORATION_RATE = 1;
    private static final double LEARNING_RATE = 0.8;
    private static final double DISCOUNT_FACTOR = 0.1;

    public static Random r;

    public static void main(String[] args)
    {
        QTable qtable = new QTable(100000000);

        r = new Random();

        int[] max_values = new int[15];

        int revisit_count = 0;
        int state_visit_count = 0;

        for (int count=0; count < 1e6; count++)
        {

            Board board = new Board(r);

            board.fillRandom();
            int previous = board.encodeKyle();

            while(!board.checkGameOver())
            {
                double choice = r.nextDouble();

                ArrayList<Board.MOVE> validMoves = board.getValidMoves();

                if (!qtable.containsKey(previous))
                {
                    EnumMap<Board.MOVE, Double> states = new EnumMap<Board.MOVE, Double>(Board.MOVE.class);

                    for(Board.MOVE valid_move: Board.MOVE.values())
                    {
                        states.put(valid_move, 0.0);
                    }

                    qtable.put(previous, states);
                }
                else
                {
                    revisit_count++;
                }

                Board.MOVE move;
                //figure out if we want to explore or exploit.
                if (choice > EXPLORATION_RATE)
                {
                    move = getBestMove(qtable, previous, validMoves);
                }
                else
                {
                    move = validMoves.get(r.nextInt(validMoves.size()));
                }

                board.move(move);

                state_visit_count++;

                double reward = 1;

                final double BLANK_CELLS_FACTOR = 1;
                final double MONOTONE_FACTOR = -1;

                reward += MONOTONE_FACTOR * board.monotonicity() / 240.0;
                reward += BLANK_CELLS_FACTOR * board.blankCells();
                reward += 100 * ( 1 << board.maxValue());

                if (board.checkGameOver())
                    reward = -1000;

                int now = board.encodeKyle();

                double old_value = qtable.get(previous).get(move);
                double new_value = old_value + LEARNING_RATE * (reward + DISCOUNT_FACTOR * getOptimalValue(qtable, now, board) - old_value);

                qtable.get(previous).put(move, new_value);

                previous = now;
            }

            int max_value = board.maxValue();

            max_values[max_value]++;

            if ( count % 1000 == 0)
            {
                double percent = revisit_count / (float) state_visit_count;
                System.out.println(count + " " + qtable.size() + " " + percent);
            }

        }

        for (int i=0; i < max_values.length; i++)
        {
            System.out.println((1 << i) + " " + max_values[i]);
        }

        System.out.println(qtable.size());
    }

    public static Board.MOVE getBestMove(QTable qtable, int state, ArrayList<Board.MOVE> validMoves)
    {
        double max = 0;
        Board.MOVE best_move = validMoves.get(r.nextInt(validMoves.size()));

        for(Board.MOVE move: validMoves)
        {
            double value = qtable.get(state).get(move);

            if(value > max)
            {
                max = value;
                best_move = move;
            }
        }

        return best_move;
    }

    public static double getOptimalValue(QTable qtable, int state, Board board)
    {
        if (!qtable.containsKey(state))
        {
            return 0;
        }

        double max = 0;
        ArrayList<Board.MOVE> validMoves = board.getValidMoves();

        for(Board.MOVE move: validMoves)
        {
            double value = qtable.get(state).get(move);

            if(value > max)
            {
                max = value;
            }
        }

        return max;
    }
}
