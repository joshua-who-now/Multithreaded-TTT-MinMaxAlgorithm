import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TicTacToe extends Application{

	/* Variable and Board Declarations */
	HashMap<String, Scene> sceneMap;
	Client clientConnection;
	Label ip_label;
	TextField ip_field;
	Label port_label;
	TextField port_field;
	Button connect_btn, confirmMove, confirmDifficulty, playAgain, quitGame; 
	RadioButton easy , medium, expert; //RadioButtons to allow for difficulty selection
	int pID; //Player's ID
	ListView<String> top3 = new ListView<String>();  //top 3 players
	ArrayList<String> state = new ArrayList<String>();  //contains state of the game
	ArrayList<String> move = new ArrayList<String>();  //used for move selection by user
	ArrayList<Board> boards = new ArrayList<Board>();  //contains all squares
	ArrayList<Board> resultsBoards = new ArrayList<Board>();  //contains all squares
	String clientDifficulty;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Let's Play Tic Tac Toe!!!");

		for(int i = 0; i < 9; i++) {   //initialize state of board to 0
			state.add(i," ");
			move.add(i, " ");
		}

		// labels and textfield corresponding to IP address
		ip_label = new Label("Enter IP Address To Connect To: ");
		ip_field = new TextField();

		// labels and textfield corresponding to port number
		port_label = new Label("Enter Port To Listen To: ");
		port_field = new TextField();

		// button used to connect to server once relevant information is entered
		connect_btn = new Button("Connect");
		confirmDifficulty = new Button("Confirm");
		confirmMove = new Button("Confirm");

		playAgain = new Button("Play Again");
		quitGame = new Button("Quit");

		/*~~~~~~~~~~~~~~~~~~~~~~Event Handlers~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

		//When Game if over, if you choose to play again, go back to difficulty selection screen
		playAgain.setOnAction(e-> primaryStage.setScene(sceneMap.get("difficultySelection")));

		//Closing Client Connection Handler
		quitGame.setOnAction(q-> {
			if (clientConnection != null) {
				try {
					clientConnection.socketClient.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Platform.exit();
		});

		//Hashmap of Scenes
		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("init",  intro());
		sceneMap.put("difficultySelection", difficulty());
		sceneMap.put("playfield", game());
		sceneMap.put("results",  results());


		// main event handler that initializes the clientconnection class
		connect_btn.setOnAction(e-> {primaryStage.setScene(sceneMap.get("difficultySelection"));
			clientConnection = new Client(data->{
				Platform.runLater(()->{
					//Update Client Gui Via RunLater Here
					GameInfo info = (GameInfo) data;
					primaryStage.setTitle("Player " + info.playerID);
					pID = info.playerID;
				});},

					ip_field.getText(), Integer.valueOf(port_field.getText()),

					game->{  //display server's choice
						Platform.runLater(()-> {
							int temp = (int) game;
							state.set(temp,"X");
							boards.get(temp).choice.setText("X");
						});
					},
					results->{
						Platform.runLater(()-> {
							primaryStage.setScene(sceneMap.get("results"));
							GameInfo temp = (GameInfo) results;
							//redraw the final state of the game onto the results board
							for(int i = 0; i < temp.gameState.size(); i++) {
								resultsBoards.get(i).choice.setText(temp.gameState.get(i)); //display all choices made
								boards.get(i).choice.setText(" "); //reset board display
								state.set(i, " "); //reset original board list
							}

						});
					},
					leaderboard->{
						Platform.runLater(()-> {
							ArrayList<AllPlayers> temp = (ArrayList<AllPlayers>) leaderboard; //get top 3 players
							top3.getItems().clear();
							for(int i = 0; i < 3; i++) {  //display top 3 players to clients
								top3.getItems().add("Player: " + temp.get(i).playerID + " | Score: " + temp.get(i).score);
							}
						});
					});
			clientConnection.start(); // starts client thread
		});
		confirmDifficulty.setOnAction(e->{
			if(easy.isSelected()){
				clientDifficulty = (easy.getText());
			}
			else if(medium.isSelected()){
				clientDifficulty = (medium.getText());
			}
			else if(expert.isSelected()){
				clientDifficulty = (expert.getText());
			}
			primaryStage.setScene(sceneMap.get("playfield"));
		});

		//Closing Client Connection Handler
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				if(clientConnection != null)
				{
					try {
						clientConnection.socketClient.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Platform.exit();
				//System.exit(0);
			}
		});

		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Set Scene ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

		primaryStage.setScene(sceneMap.get("init"));
		primaryStage.show();

	}

	/* Initial
	 *
	 * This is the first scene that the user sees. Here the user gets to input the IP address and port that they
	 * want to connect to
	 */
	public Scene initial() {
		// Grouping together all input fields and text related to entering connection details into a VBox
		VBox port = new VBox(20, ip_label, ip_field, port_label, port_field, connect_btn);
		BorderPane pane = new BorderPane();
		pane.setLeft(port);

		return new Scene(pane, 400, 300);
	}

	public class Board extends StackPane {  //represents each square on board
		Text choice = new Text();  //move
		int index; //index of each square 0-8
		Board () {
			Rectangle rect = new Rectangle(100, 100);
			rect.setFill(null);
			rect.setStroke(Color.BLACK);
			setAlignment(Pos.CENTER);
			getChildren().addAll(rect, choice);
			choice.setFont(Font.font(60));
		}
	}

	//Starting Screen where you enter the IP and Port
	public Scene intro() {
		BorderPane intro = new BorderPane();
		port_field.setMaxWidth(200);
		ip_field.setMaxWidth(200);
		Label title = new Label("Tic Tac Toe");
		title.setFont(Font.font(30));
		VBox vB = new VBox();
		vB.getChildren().addAll(title, ip_label, ip_field, port_label, port_field, connect_btn);
		vB.setMargin(port_field, new Insets(10, 0, 10, 0));
		vB.setMargin(ip_field, new Insets(10, 0, 10, 0));
		vB.setAlignment(Pos.CENTER);
		intro.setCenter(vB);
		intro.setMargin(title, new Insets(50, 0, 20, 0));
		return new Scene(intro,600,600);
	}

	//GUI to select difficulty
	public Scene difficulty() {
		BorderPane diff = new BorderPane();
		Label selectDifficulty = new Label("Select Difficulty");
		selectDifficulty.setFont(Font.font(30));
		HBox buttons = new HBox();
		VBox page = new VBox(10); //combining all components
		easy = new RadioButton("Easy");
		easy.setOnAction(e->{
			medium.setSelected(false);
			expert.setSelected(false);
		});
		medium = new RadioButton("Medium");
		medium.setOnAction(e->{
			easy.setSelected(false);
			expert.setSelected(false);
		});
		expert = new RadioButton("Expert");
		expert.setOnAction(e->{
			easy.setSelected(false);
			medium.setSelected(false);
		});
		confirmDifficulty = new Button("Confirm");
		buttons.getChildren().addAll(easy, medium, expert);
		buttons.setMargin(medium, new Insets(0, 10, 0, 10));
		buttons.setAlignment(Pos.CENTER);
		page.getChildren().addAll(selectDifficulty, buttons, confirmDifficulty);
		page.setAlignment(Pos.CENTER);
		diff.setCenter(page);
		return new Scene(diff, 600, 600);
	}

	//GUI for game
	//https://www.youtube.com/watch?v=Uj8rPV6JbCE referenced this on making the board
	public Scene game() {
		Pane pane = new Pane();
		int spots = 0;
		BorderPane bp = new BorderPane();
		confirmMove.setDisable(true);  //disable confirm button at start
		VBox vB = new VBox();
		vB.setAlignment(Pos.TOP_CENTER);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Board board = new Board();
				board.setLayoutX(150);  //starting x pos  (top left)
				board.setLayoutY(100);  //starting y pos
				board.index = spots;    //set index of each square 0-8
				board.setTranslateX(j * 100);  //set each square to be 100 x 100
				board.setTranslateY(i * 100);
				boards.add(spots, board);  //arraylist of boards/tiles
				spots++;
				pane.getChildren().addAll(board);
				board.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if (state.get(board.index) == " ") {  //was an empty space clicked
							for (int i = 0; i < move.size(); i++) {   //BEFORE USER CONFIRMS MOVE!
								if (i != board.index && move.get(i) != " ") {  //reset previous moves
									move.set(i, " ");
									boards.get(i).choice.setText("");
								}
							}
							move.set(board.index, "O");  //set official move
							board.choice.setText("O");  //display move
							confirmMove.setDisable(false);  //allow user to confirm only after they've selected a move
						}
					}
				});
			}
		}

		confirmMove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GameInfo info = new GameInfo();
				state.set(move.indexOf("O"), "O");   //add move to game state
				boards.get(move.indexOf("O")).choice.setText("O");  //draw O onto selected area
				info.move = move.indexOf("O");
				move.set(move.indexOf("O"), " ");  //reset list for next move
				confirmMove.setDisable(true);  //disable confirm until new move is clicked
				info.gameState = state;
				info.mode = clientDifficulty;
				info.status = 1;
				info.playerID = pID;
				clientConnection.send(info);
			}
		});

		vB.getChildren().addAll(pane, confirmMove);
		vB.setMargin(confirmMove, new Insets(250, 0, 0, 0));
		bp.setCenter(vB);
		return new Scene(bp, 600, 600);
	}

	//Results Scene displays Who Won, Final State of the Game, and Top 3 players across the server
	public Scene results(){
		Pane results = new Pane();
		Label gameOver = new Label("Game Over");
		BorderPane bp = new BorderPane();
		VBox vB = new VBox();
		HBox hbox = new HBox(50);
		hbox.getChildren().addAll(playAgain, quitGame);
		Label topScore = new Label("Top 3");
		int spots = 0; //index for each space
		top3.setMaxWidth(200);
		top3.setMaxHeight(80);
		vB.setMargin(topScore, new Insets(10,0,5,0));
		vB.setAlignment(Pos.TOP_CENTER);
		for(int i = 0; i < 3; i++) {  //draw empty board
			for(int j = 0; j < 3; j++) {
				Board board = new Board();
				board.setLayoutX(150);  //starting x pos  (top left)
				board.setLayoutY(50);  //starting y pos
				board.index = spots;    //set index of each square 0-8
				board.setTranslateX(j * 100);  //set each square to be 100 x 100
				board.setTranslateY(i * 100);
				resultsBoards.add(spots, board);  //add new board into resultsBoards list
				spots++;
				results.getChildren().addAll(board);
			}
		}
		hbox.setAlignment(Pos.CENTER);
		vB.getChildren().addAll(topScore, top3, results, gameOver, hbox);  //combine all components together
		vB.setMargin(gameOver, new Insets(220, 0, 10, 0));
		bp.setCenter(vB);
		return new Scene(bp,600, 600);
	}
}

