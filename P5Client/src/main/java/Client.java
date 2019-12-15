import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Client extends Thread{

    // connection our client gets connected to
    Socket socketClient;
    // IP address client wants to connect to
    String IP;
    // Port number client wants to connect to
    int Port;
    // stream out of client
    ObjectOutputStream out;
    // stream into client
    ObjectInputStream in;
    // object used to pass information to clients
    GameInfo info;
    /* Serializable Callback Function Declarations */
    private Consumer<Serializable> callback;
    private Consumer<Serializable> gameback;  //for gamestate list
    private Consumer<Serializable> resultsback;  //for displaying results information
    private Consumer<Serializable> leaderboardback; //for displaying top 3 players to clients

    /* Client
     *
     * Constructor for Client Class initializes Class variables
     */
    Client(Consumer<Serializable> call, String ip, int port, Consumer<Serializable> game, Consumer<Serializable> results, Consumer<Serializable> leaderboard){
        info = new GameInfo();
        IP = ip;
        Port = port;
        callback = call;
        gameback = game;
        resultsback = results;
        leaderboardback = leaderboard;
    }

    // run function for client class - continually checks for input from server and updates gui scenes as necessary
    public void run() {

        // open connections to the server
        try {
            socketClient= new Socket(IP, Port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }
        catch(Exception e) {
            callback.accept("Not Connected To The Server. Close The App And Try Again.");
        }

        // continually check for input from server
        while(true) {

            try {
                // read in gameinfo object
                info = (GameInfo)in.readObject();

                // check the status field to determine action to perform
                switch(info.status) {
                    case 0: // case 0 - lets client know his id
                        callback.accept(info);
                        break;
                    case 1: // case 1 - if no one wins, game continues (draw's the computer's/server's move)
                        gameback.accept(info.move);
                        break;
                    case 2:	// case 2 - if someone wins the game
                        resultsback.accept(info);
                        break;
                    case 3: // case 3 - to display top 3 players
                        leaderboardback.accept(info.leaderboard);
                        break;
                }
            }
            catch(Exception e) {
                System.out.println("Not Connected To The Server. Close The App And Try Again.");
                break;
            }
        }

    }

    /* send
     *
     * function is used to send information regarding player's hand choice to the server
     * choice1 field is overwritten in gameinfo object and sent then the object is sent to the
     * server
     */
    public void send(GameInfo data) {
        try {
            out.writeObject(data);
            out.reset();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

/*  GameInfo
 *  used to send and recieve information between server and clients 
 */
class GameInfo implements Serializable{
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
class AllPlayers implements Serializable{  //keeps track of player stats
    private static final long serialVersionUID = 1L;
    int playerID;
    int score;

    public AllPlayers(int p, int s)
    {
        playerID = p;
        score = s;
    }
}