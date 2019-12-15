import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class TicTacToe extends Application{

	HashMap<String, Scene> sceneMap;
	Server serverConnection;
	ListView<String> listItems;
	static final int picHeight = 150;
	static final int picWidth = 150;
	Label port_label;
	TextField port_field;
	Button port_btn;

	ListView<String> players = new ListView<String>();  //player traffic
	ListView<String> top3 = new ListView<String>();  //top players


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Let's Play Tic Tac Toe!!! - Server");

		// labels and textfield corresponding to port number
		port_label = new Label("Enter Port To Listen To: ");
		port_field = new TextField();

		// button used to start server
		port_btn = new Button("Begin Listening");

		// listview
		listItems = new ListView<String>();

		/*~~~~~~~~~~~~~~~~~~~~~~Event Handlers~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

		// main event handler that initializes the serverconnection class
		port_btn.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
			serverConnection = new Server(data->{
				Platform.runLater(()->{
					// updates player traffic (moves & scores)
					players.getItems().addAll(data.toString());

				});},
					Integer.valueOf(port_field.getText()),
					leaders->{   //updates top scores
						Platform.runLater(()-> {
							ArrayList<AllPlayers> temp = (ArrayList<AllPlayers>) leaders;
							top3.getItems().clear();
							for(int i = 0; i < temp.size(); i++) {
								top3.getItems().addAll("Player: " + temp.get(i).playerID + " | Score: " + temp.get(i).score);
							}
						});
					});
		});

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				if(serverConnection != null)
				{
					try {
						serverConnection.server.mysocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Platform.exit();
			}
		});


		/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Set Scene ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("init",  initialGui());
		sceneMap.put("server",  createServerGui());
		primaryStage.setScene(sceneMap.get("init"));
		primaryStage.show();
	}

	/* initialGui
	 *
	 * This is the first scene that the user sees. Here the user gets to input the port that they
	 * want to listen to
	 */
	public Scene initialGui() {
		// Grouping together all input fields and text related to entering connection details into a VBox
		port_field.setMaxWidth(100);
		VBox port = new VBox(20 , port_label, port_field, port_btn);
		port.setAlignment(Pos.CENTER);
		BorderPane pane = new BorderPane();
		pane.setCenter(port);
		return new Scene(pane, 400, 300);
	}

	/*
	 *
	 *
	 *
	 */
	public Scene createServerGui(){
		BorderPane stats = new BorderPane();
		Label allPlayers = new Label("Players");
		Label top = new Label("Top 3");
		VBox vR = new VBox();
		VBox vC = new VBox();
		vR.getChildren().addAll(top, top3);
		vR.setAlignment(Pos.CENTER);
		vC.setAlignment(Pos.CENTER);
		top3.setMaxWidth(200);
		top3.setMaxHeight(150);
		players.setMaxHeight(300);
		players.setMaxWidth(300);
		vC.getChildren().addAll(allPlayers, players);
		vR.setPadding(new Insets(0,50,0,0));
		stats.setRight(vR);
		stats.setCenter(vC);
		return new Scene(stats, 600, 600);
	}
}
