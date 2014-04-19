import java.util.Random;
import java.util.HashMap;
import java.util.EnumMap;

class QTable extends HashMap<Long, EnumMap<Board.MOVE, Double>>
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
    private static final double EXPLORATION_RATE = 0.25;
    private static final double LEARNING_RATE = 0.1;
    private static final double DISCOUNT_FACTOR = 0.99;

    public static Random r;

    public static void main(String[] args)
    {
        QTable qtable = new QTable(100000000);

        r = new Random();

        int[] max_values = new int[15];

        int revisit_count = 0;
        int state_visit_count = 0;

        for (int count=0; count < 1e5; count++)
        {

            Board board = new Board(r);

            board.fillRandom();
            long previous = board.encode();

            while(!board.checkGameOver())
            {
                double choice = r.nextDouble();

                Board.MOVE move;

                //figure out if we want to explore or exploit.
                if (choice < EXPLORATION_RATE)
                {
                    move = getBestMove(qtable, previous);
                }
                else
                {
                    move = Board.MOVE.values()[r.nextInt(4)];
                }

                board.move(move);

                long now = board.encode();

                if (!qtable.containsKey(previous))
                {
                    EnumMap<Board.MOVE, Double> states = new EnumMap<Board.MOVE, Double>(Board.MOVE.class);

                    states.put(Board.MOVE.LEFT, 0.0);
                    states.put(Board.MOVE.RIGHT, 0.0);
                    states.put(Board.MOVE.UP, 0.0);
                    states.put(Board.MOVE.DOWN, 0.0);

                    qtable.put(previous, states);
                }
                else
                {
                    revisit_count++;
                }

                state_visit_count++;

                EnumMap<Board.MOVE, Double> states = qtable.get(previous);

                double reward = 0.02;

                final double BLANK_CELLS_FACTOR = 600;
                final double MONOTONE_FACTOR = 300;

                reward += BLANK_CELLS_FACTOR * board.monotonicity();
                reward += MONOTONE_FACTOR * board.blankCells();
                reward += 1 << board.maxValue();

                if (board.checkGameOver())
                    reward = -1000;

                double old_value = states.get(move);
                double new_value = old_value + LEARNING_RATE * (reward + DISCOUNT_FACTOR * getOptimalValue(qtable, now) - old_value);

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

    public static Board.MOVE getBestMove(QTable qtable, long state)
    {
        if (!qtable.containsKey(state))
        {
            return Board.MOVE.values()[r.nextInt(4)];
        }

        double max = 0;
        Board.MOVE best_move = Board.MOVE.LEFT;

        for(Board.MOVE move: Board.MOVE.values())
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

    public static double getOptimalValue(QTable qtable, long state)
    {
        if (!qtable.containsKey(state))
        {
            return 0;
        }

        double max = 0;

        for(Board.MOVE move: Board.MOVE.values())
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
