import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is used to read in a state of a tic tac toe board. It creates a MinMax object and passes the state to it. What returns is a list
 * of possible moves for the player X that have been given min/max values by the method findMoves. The moves that can result in a win or a
 * tie for X are printed out with the method printBestMoves()
 *
 * @author Mark Hallenbeck
 *
 * Copyright© 2014, Mark Hallenbeck, All Rights Reservered.
 *
 */
public class AI_MinMax {

    private String[] init_board;

    private ArrayList<Node> movesList;

    AI_MinMax(String[] board)
    {
        init_board = board;

        if(init_board.length != 9)
            System.exit(-1);

        MinMax sendIn_InitState = new MinMax(init_board);
        movesList = sendIn_InitState.findMoves();

    }

    public ArrayList<Node> getBestMoves()
    {
        return movesList;
    }
}
