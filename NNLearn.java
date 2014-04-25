import java.util.Random;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.ArrayList;
import java.util.List;

import java.io.*;

import org.encog.util.simple.*;
import org.encog.neural.networks.*;
import org.encog.Encog;
import org.encog.ml.data.*;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.mathutil.randomize.*;



public class NNLearn
{
    private static double EXPLORATION_RATE = 1;
    private static final double Q_LEARNING_RATE = 0.001;
    private static final double Q_DISCOUNT_FACTOR = 0.9;

    private static final double NEURAL_LEARNING_RATE = 0.007;
    private static final double NEURAL_MOMENTUM = 0.002;

    private static final int ITERATIONS = (int) 1.0;


    public static Random r;
    private static BasicNetwork qtable;

    public static void main(String[] args)
    {
        qtable = new BasicNetwork();

        qtable.addLayer(new BasicLayer(new ActivationLinear(), true, 17));
        qtable.addLayer(new BasicLayer(new ActivationSigmoid(), true, 10));
        qtable.addLayer(new BasicLayer(new ActivationLinear(), false, 1));

        qtable.getStructure().finalizeStructure();

        new ConsistentRandomizer(-1, 1, 500).randomize(qtable);

        r = new Random();

        //run one game
        for(int i=0; i < ITERATIONS; i++)
        {
            MLDataSet trainingSet = runGame();

            if (i % 1000 == 0)
            {
                System.out.println("Game "+i + " training set size: "+ trainingSet.size());
                EXPLORATION_RATE -= 1000.0 / ITERATIONS;
            }

            final Backpropagation train = new Backpropagation(qtable, trainingSet, NEURAL_LEARNING_RATE, NEURAL_MOMENTUM);
            //final Backpropagation train = new Backpropagation(qtable, trainingSet);

            int epoch = 0;
            do {
                System.out.println("Epoch#" +epoch+" Error: "+train.getError());
                train.iteration();
                epoch++;
            } while(train.getError() > 0.005);

            train.finishTraining();
        }

        Encog.getInstance().shutdown();

		System.out.println("Saving network");
		EncogDirectoryPersistence.saveObject(new File("saved_network.eg"), qtable);
    }

    private static MLDataSet runGame()
    {

        Board board = new Board(r);
        board.fillRandom();

        int count = 0;

        ArrayList<Board> states = new ArrayList<Board>();
        ArrayList<Board.MOVE> moves = new ArrayList<Board.MOVE>();

        while (!board.checkGameOver())
        {
            List<Board.MOVE> valid_moves = board.getValidMoves();

            double choice = r.nextDouble();

            Board.MOVE move;
            if (choice > EXPLORATION_RATE)
            {
                // exploit.
                move = getBestMove(board, valid_moves);
            }
            else
            {
                // explore.
                move = valid_moves.get(r.nextInt(valid_moves.size()));
            }

            board.move(move);

            states.add(new Board(r, board.cells));
            moves.add(move);

            count++;
        }

        BasicMLDataSet gameData = new BasicMLDataSet();

        for(int i=0; i < states.size(); i++)
        {
            Board state = states.get(i);

            Board.MOVE move = moves.get(i);

            double[] input = state.neuralEncode();
            input[16] = move.ordinal();

            //implement Q Learning

            double old_value = qtable.compute(new BasicMLData(input)).getData()[0];
            //double reward = computeReward(state, count);

            double reward = 0;

            double new_value = old_value;

            // if we have a next state.
            if (i < states.size() - 1)
            {
                reward = -1;
                Board next = states.get(i + 1);

                new_value += Q_LEARNING_RATE * ( reward + Q_DISCOUNT_FACTOR * getOptimalValue(next, next.getValidMoves()) - old_value);
            }
            else
            {
                reward = 1000;
                new_value += Q_LEARNING_RATE * (reward  - old_value);
            }

            new_value = board.maxValue();

            double[] output = new double[1];
            output[0] = new_value;

            gameData.add(new BasicMLData(input), new BasicMLData(output));

            count--;
        }


        return gameData;
    }

    private static double computeReward(Board state, int ttl)
    {
        int difference = state.monotonicity();
        int blank_cells = state.blankCells();

        final double DIFFERENCE_FACTOR = -1.0;
        final double BLANK_CELLS_FACTOR = 1.0;
        final double TTL_FACTOR = 1.0;


        return DIFFERENCE_FACTOR * difference
            + BLANK_CELLS_FACTOR * blank_cells
            + TTL_FACTOR * ttl;
    }


    private static double getOptimalValue(Board board, List<Board.MOVE> valid_moves)
    {
        if (valid_moves.isEmpty())
        {
            return 0;
        }

        double max = 0;

        Board.MOVE best_move = valid_moves.get(r.nextInt(valid_moves.size()));
        double[] state = board.neuralEncode();

        for (Board.MOVE move: valid_moves)
        {
            state[16] = move.ordinal();

            double[] result = qtable.compute(new BasicMLData(state)).getData();

            if(result[0] > max)
            {
                max = result[0];
                best_move = move;
            }
        }


        return max;
    }

    private static void printArray(double[] items)
    {
        for(double item: items)
        {
            System.out.print(item + ", ");
        }
        System.out.println();
    }


    private static Board.MOVE getBestMove(Board board, List<Board.MOVE> valid_moves)
    {
        double max = 0;

        Board.MOVE best_move = valid_moves.get(r.nextInt(valid_moves.size()));
        double[] state = board.neuralEncode();

        for (Board.MOVE move: valid_moves)
        {
            state[16] = move.ordinal();

            double[] result = qtable.compute(new BasicMLData(state)).getData();

            if(result[0] > max)
            {
                max = result[0];
                best_move = move;
            }
        }

        return best_move;
    }
}
