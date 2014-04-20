import java.util.Scanner;
import java.util.Random;
import java.io.*;
public class Game
{
    private static String ttyConfig;

    public static void main(String[] args) throws IOException, InterruptedException
    {
        Random r = new Random();

        Board board = new Board(r);

        board.fillRandom();

        boolean play = true;

        setTerminalToCBreak();

        while(!board.checkGameOver())
        {
            System.out.println(board);
            System.out.println(board.getValidMoves());

            int c = getChar();

            switch (c)
            {
                case 'h':
                case 'a':
                case 0:
                    board.move(Board.MOVE.LEFT);
                    break;
                case 'j':
                case 's':
                case 1:
                    board.move(Board.MOVE.DOWN);
                    break;
                case 'k':
                case 'w':
                case 2:
                    board.move(Board.MOVE.UP);
                    break;
                case 'l':
                case 'd':
                case 3:
                    board.move(Board.MOVE.RIGHT);
                    break;
                default:
                    play = false;
                    break;
            }
        }
    }

    private static int getChar() throws IOException
    {
        int c = 0;
        while (true) {
            if ( System.in.available() != 0 ) {
                c = System.in.read();
                if ( c == 0x1B ) {
                    break;
                }
                return c;
            }
        }
        return c;
    }

    private static void setTerminalToCBreak() throws IOException, InterruptedException {

        ttyConfig = stty("-g");

        // set the console to be character-buffered instead of line-buffered
        stty("-icanon min 1");

        // disable character echoing
        stty("-echo");
    }

    /**
     *  Execute the stty command with the specified arguments
     *  against the current active terminal.
     */
    private static String stty(final String args)
                    throws IOException, InterruptedException {
        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[] {
                    "sh",
                    "-c",
                    cmd
                });
    }

    /**
     *  Execute the specified command and return the output
     *  (both stdout and stderr).
     */
    private static String exec(final String[] cmd)
                    throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        String result = new String(bout.toByteArray());
        return result;
    }

}
