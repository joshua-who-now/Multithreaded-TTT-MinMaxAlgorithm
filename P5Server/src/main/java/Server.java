import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class Server{

    // number of clients currently connected to the server
    int count = 1;
    // list clients currently connected
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    // thread that runs our server
    TheServer server;
    // port number currently monitoring
    int Port;
    // object used to pass information to clients
    GameInfo info;
    // this consumers accept function is used to update the listview displaying messages in the
    // server gui
    private Consumer<Serializable> callback;
    private Consumer<Serializable> leaderback; //to show top 3
    ArrayList<AllPlayers> players = new ArrayList<AllPlayers>();  //player traffic
    ArrayList<AllPlayers> Top3 = new ArrayList<AllPlayers>();  //top 3 list

    FindNextMove findnextmove = new FindNextMove();

    /* Server
     *
     * constructor for server class initializes class variables and starts the server thread
     */
    Server(Consumer<Serializable> call, int port, Consumer<Serializable> leader)
    {
        Port = port;
        callback = call;
        leaderback = leader;
        info = new GameInfo();
        server = new TheServer();
        server.start();
        Top3.add(new AllPlayers(0,0));
        Top3.add(new AllPlayers(0,0));
        Top3.add(new AllPlayers(0,0));
    }

    // theServer class - runs our server thread where new clients are accepted put on their own threads
    public class TheServer extends Thread{

        ServerSocket mysocket;
        // run function for theServer thread - accepts new clients and puts them on their own thread
        public void run() {

            // opens socket connection
            try{
                mysocket = new ServerSocket(Port);
                // continually checks for new connections
                while(true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    clients.add(c); // adds new clients to clients arraylist
                    callback.accept("Player: " + count + " | Score: 0");
                    leaderback.accept(Top3);
                    c.start(); // starts accepted client's thread
                    AllPlayers p = new AllPlayers(count, 0); //add new player with init score of 0
                    players.add(p);
                    count++;
                }
            }//end of try
            catch(Exception e) {
                //callback.accept("Server socket did not launch" + e);
            }
        }//end of while
    }


    // clientthread class - each new client gets put on its on thread
    class ClientThread extends Thread{

        // connection to client
        Socket connection;
        // client number
        int count;
        // input stream from client
        ObjectInputStream in;
        // output stream to client
        ObjectOutputStream out;

        /* clientthread
         *
         * constructor initializes every clientthread object with the new clients connection and
         * client number
         */
        ClientThread(Socket s, int count){
            this.connection = s;
            this.count = count;
        }

        /* UpdateSingleClientStatus Function
         * clientID = Parameter for Player's ID | stat = Status of the Game | board = tictactoe board
         * Function Purpose: used to update a single client on the server
         */
        public synchronized void updateSingleClientStatus(int clientID, int stat, ArrayList<String> board)
        {
            info.playerID = clientID;
            info.status = stat;
            info.gameState = board;

            ClientThread t = clients.get(clientID- 1);
            try {
                t.out.writeObject(info);
                t.out.reset();
            }
            catch(Exception e) {}
        }

        /* UpdateAllClientStatus Function
         * stat = Status of the Game
         * Function Purpose: used to update EVERY client on the server
         *
         * Will update every client with an updated list of the top 3 players on the server
         */
        public synchronized void updateAllClientStatus(int stat)
        {
            GameInfo temp = info;
            temp.status = stat;
            temp.leaderboard = Top3;

            for(int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                try {
                    t.out.writeObject(temp);
                    t.out.reset();
                }
                catch(Exception e) {}
            }
        }

        /* UpdateGame function
         * Server's logic for making a move using the provided Classes/Code given to use by Professor Mark Hallenbeck
         * Objective: To utilize the MinMax Algorithm to determine the possible options for a move that the
         *            server can make on the current board with each client
         *
         * Provided Code to Implement: AI_MinMax, MinMax, Node
         *
         */
        public synchronized int updateGame(GameInfo info)
        {
            for(int i = 0; i < 9; i++)
            {
                if(info.gameState.get(i).equals(" "))
                    info.gameState.set(i, "b");
            }

            String[] board = new String[info.gameState.size()];
            board = info.gameState.toArray(board);
            ArrayList<Node> moveList = findnextmove.nextMove(board);
            info.status = 1;
            int max = -10;
            info.move = moveList.get(0).getMovedTo();
            if(info.mode.equals("Expert")) { //gets highest weighted move
                for (int i = 0; i < moveList.size(); i++) {
                    if (max < moveList.get(i).getMinMax()) {
                        max = moveList.get(i).getMinMax();
                        info.move = moveList.get(i).getMovedTo() - 1;
                    }
                }
            }
            else if(info.mode.equals("Easy")) { //gets lowest weighted move
                max = 10;
                for (int i = 0; i < moveList.size(); i++) {
                    if (max > moveList.get(i).getMinMax()) {
                        max = moveList.get(i).getMinMax();
                        info.move = moveList.get(i).getMovedTo() - 1;
                    }
                }
            }
            else if(info.mode.equals("Medium")) { //gets random move
                Random rand = new Random();
                //Get a random move from the moves list
                info.move = moveList.get(rand.nextInt(moveList.size())).getMovedTo()-1;
            }

            ClientThread t = clients.get(info.playerID - 1);
            try {
                t.out.writeObject(info);
                t.out.reset();
            }
            catch(Exception e) {}
            return info.move;
        }

        // run function for clientthread class - each client thread is continually polled for input
        public void run(){

            // open streams to and from client
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {}
            updateSingleClientStatus(count, 0, info.gameState);

            while(true) {
                try {
                    GameInfo clientInfo = (GameInfo)in.readObject(); // read in from object
                    if(clientInfo.status == 1) //Status 1 == In a game of Tic Tac Toe
                    {
                        String winningPiece = " ";
                        callback.accept("Player: " + clientInfo.playerID + " | Score: " + players.get(count-1).score + " | Played: " + clientInfo.move); //update player traffic after every move
                        //Check if we have a winner once the player has sent a move
                        //Compares board pieces to eachother to determine winner
                        if ((clientInfo.gameState.get(0).equals("O") && clientInfo.gameState.get(1).equals("O") && clientInfo.gameState.get(2).equals("O")) ||
                                (clientInfo.gameState.get(3).equals("O") && clientInfo.gameState.get(4).equals("O") && clientInfo.gameState.get(5).equals("O")) ||
                                (clientInfo.gameState.get(6).equals("O") && clientInfo.gameState.get(7).equals("O") && clientInfo.gameState.get(8).equals("O")) ||
                                (clientInfo.gameState.get(0).equals("O") && clientInfo.gameState.get(3).equals("O") && clientInfo.gameState.get(6).equals("O")) ||
                                (clientInfo.gameState.get(1).equals("O") && clientInfo.gameState.get(4).equals("O") && clientInfo.gameState.get(7).equals("O")) ||
                                (clientInfo.gameState.get(2).equals("O") && clientInfo.gameState.get(5).equals("O") && clientInfo.gameState.get(8).equals("O")) ||
                                (clientInfo.gameState.get(0).equals("O") && clientInfo.gameState.get(4).equals("O") && clientInfo.gameState.get(8).equals("O")) ||
                                (clientInfo.gameState.get(2).equals("O") && clientInfo.gameState.get(4).equals("O") && clientInfo.gameState.get(6).equals("O")))
                        {
                            winningPiece = "O";
                        }


                        //If winning piece is not null (has been assigned) someone has won
                        if (!winningPiece.equals(" ")) {
                            GameInfo returnInfo = new GameInfo(); //creates new object to send back to client
                            returnInfo.winner = "Player"; //sets the winner
                            updateSingleClientStatus(clientInfo.playerID, 2, clientInfo.gameState);
                            //Change Array list to new top 3
                            //Then send arraylist
                            players.get(count - 1).score++;

                            //check if player is in top 3 after completed game
                            synchronized(Top3) {
                                if(players.get(count - 1).playerID == Top3.get(0).playerID)
                                    Top3.set(0, players.get(count - 1));
                                else if(players.get(count - 1).playerID == Top3.get(1).playerID) {
                                    Top3.set(1, players.get(count - 1));
                                    if(Top3.get(1).score > Top3.get(0).score)
                                    {
                                        int s = Top3.get(0).score;
                                        int id = Top3.get(0).playerID;
                                        AllPlayers temp = new AllPlayers(id, s);
                                        Top3.set(0, Top3.get(1));
                                        Top3.set(1, temp);
                                    }
                                }
                                else if(players.get(count - 1).playerID == Top3.get(2).playerID)
                                {
                                    Top3.set(2, players.get(count - 1));
                                    if(Top3.get(2).score > Top3.get(1).score)
                                    {
                                        int s = Top3.get(1).score;
                                        int id = Top3.get(1).playerID;
                                        AllPlayers temp = new AllPlayers(id, s);
                                        Top3.set(1, Top3.get(2));
                                        Top3.set(2, temp);
                                    }
                                    else if(Top3.get(2).score > Top3.get(0).score)
                                    {
                                        int s = Top3.get(0).score;
                                        int id = Top3.get(0).playerID;
                                        AllPlayers temp = new AllPlayers(id, s);
                                        Top3.set(0, Top3.get(2));
                                        Top3.set(2, temp);
                                    }
                                }
                                else if(players.get(count - 1).score > Top3.get(0).score)
                                {
                                    Top3.set(2, Top3.get(1));
                                    Top3.set(1, Top3.get(0));
                                    Top3.set(0, players.get(count - 1));
                                }
                                else if(players.get(count - 1).score > Top3.get(1).score)
                                {
                                    Top3.set(2, Top3.get(1));
                                    Top3.set(1, players.get(count - 1));
                                }
                                else if(players.get(count - 1).score > Top3.get(2).score)
                                {
                                    Top3.set(2, players.get(count - 1));
                                }
                            }
                            callback.accept("Player: " + clientInfo.playerID + " | Score: " + players.get(count-1).score);  //update traffic with score
                            leaderback.accept(Top3);  //update top 3 list in server
                            updateAllClientStatus(3);  //update top 3 list in all clients
                            continue;
                        }

                        //Check if the board is full
                        if (!clientInfo.gameState.get(0).equals(" ") && !clientInfo.gameState.get(1).equals(" ") && !clientInfo.gameState.get(2).equals(" ") &&
                                !clientInfo.gameState.get(3).equals(" ") && !clientInfo.gameState.get(4).equals(" ") && !clientInfo.gameState.get(5).equals(" ") &&
                                !clientInfo.gameState.get(6).equals(" ") && !clientInfo.gameState.get(7).equals(" ") && !clientInfo.gameState.get(8).equals(" "))
                        {
                            GameInfo returnInfo = new GameInfo(); //creates new gameinfo object to send back to client
                            returnInfo.status = 2; //sets the status to "not in game"
                            returnInfo.winner = "Tie"; //sets the winner
                            returnInfo.gameState = clientInfo.gameState; //sets the board
                            updateSingleClientStatus(clientInfo.playerID, 2, clientInfo.gameState);

                            continue;
                        }

                        //DoMinMaxAlgorithmFindNextMove
                        clientInfo.gameState.set(updateGame(clientInfo), "X");

                        //Check if we have a winner once the player has sent a move
                        //Compares board pieces to eachother to determine winner
                        if ((clientInfo.gameState.get(0).equals("X") && clientInfo.gameState.get(1).equals("X") && clientInfo.gameState.get(2).equals("X")) ||
                                (clientInfo.gameState.get(3).equals("X") && clientInfo.gameState.get(4).equals("X") && clientInfo.gameState.get(5).equals("X")) ||
                                (clientInfo.gameState.get(6).equals("X") && clientInfo.gameState.get(7).equals("X") && clientInfo.gameState.get(8).equals("X")) ||
                                (clientInfo.gameState.get(0).equals("X") && clientInfo.gameState.get(3).equals("X") && clientInfo.gameState.get(6).equals("X")) ||
                                (clientInfo.gameState.get(1).equals("X") && clientInfo.gameState.get(4).equals("X") && clientInfo.gameState.get(7).equals("X")) ||
                                (clientInfo.gameState.get(2).equals("X") && clientInfo.gameState.get(5).equals("X") && clientInfo.gameState.get(8).equals("X")) ||
                                (clientInfo.gameState.get(0).equals("X") && clientInfo.gameState.get(4).equals("X") && clientInfo.gameState.get(8).equals("X")) ||
                                (clientInfo.gameState.get(2).equals("X") && clientInfo.gameState.get(4).equals("X") && clientInfo.gameState.get(6).equals("X")))
                        {
                            winningPiece = "X";
                        }

                        //If winning piece is not null (has been assigned) someone has won
                        if (!winningPiece.equals(" ")) {
                            GameInfo returnInfo = new GameInfo(); //creates new object to send back to client
                            returnInfo.winner = "Computer"; //sets the winner
                            updateSingleClientStatus(clientInfo.playerID, 2, clientInfo.gameState);
                            continue;
                        }
                    }
                }
                catch(Exception e) { // this exception is entered when a client closes down
                    // display message to server gui that a client closed down
                    callback.accept("Something Wrong With The Socket From Client: " + count + "....Closing Down!");
                    // remove client from our list and update client counts
                    break;
                }
            }
        }//end of run
    }//end of client thread
}

//gameinfo class - used to send and recieve information between server and clients
class GameInfo implements Serializable{
    /*
     *
     */
    ArrayList<String> playerlist;
    private static final long serialVersionUID = 1L;
    int playerID;
    int move;
    ArrayList<String> gameState = new ArrayList<String>();  //contains state of the game
    ArrayList<AllPlayers> leaderboard;
    String winner;
    String mode;
    boolean playAgain;
    int status; // used to convey game status
}

/* AllPlayers Class
 * used to send and receive information about other clients
 */
class AllPlayers implements Serializable{
    private static final long serialVersionUID = 1L;
    int playerID;
    int score;

    public AllPlayers(int p, int s)
    {
        playerID = p;
        score = s;
    }
}

class FindNextMove {
    ArrayList<Node> moveList;

    public synchronized ArrayList<Node> nextMove(String[] init_board)
    {
        Thread t1 = new Thread(()->{
            AI_MinMax possibleMoves = new AI_MinMax(init_board);
            moveList = possibleMoves.getBestMoves();
        });

        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return moveList;
    }
}