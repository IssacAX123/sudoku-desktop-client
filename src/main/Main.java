package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.java_websocket.enums.ReadyState;
import main.Controller;

import java.io.IOException;
import java.net.URI;

import wsClient.WSClient;

public class Main extends Application {
    static WSClient ws;
    static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
//        Controller controller = new Controller(this.ws);
//        loader.setController(controller);
        Group root = new Group();
        startScreen(root);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 990, 620));
        primaryStage.show();
    }


    public static void startScreen(Group root){
        Label title = new Label("Sudoku Online:- Single or Multiplayer");
        title.setFont(Font.font("Nexa", FontWeight.BOLD, 36));
        title.setLayoutX(175);
        title.setLayoutY(70);
        root.getChildren().add(title);

        Button createGameBtn = new Button("Create Game");
        createGameBtn.setId("createGameBtn");
        createGameBtn.setFont(new Font("sans-serif", 26));
        createGameBtn.setStyle ("-fx-text-fill: white; -fx-background-color: blue");
        createGameBtn.setLayoutX(403);
        createGameBtn.setLayoutY(230);
        createGameBtn.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                try {
                    Main.controller.createGame(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        root.getChildren().add(createGameBtn);

        TextField codeTextField = new TextField();
        codeTextField.setPromptText("Enter Game Code");
        codeTextField.setFocusTraversable(false);
        codeTextField.setFont(new Font("Verdana", 16));
//        codeTextField.setStyle("-fx-text-fill: black; -fx-background-color: white");
        codeTextField.setPrefWidth(165);
        codeTextField.setPrefHeight(37);
        codeTextField.setLayoutX(350);
        codeTextField.setLayoutY(330);
        root.getChildren().add(codeTextField);

        Button joinGameBtn = new Button("Join Game");
        joinGameBtn.setFont(new Font("sans-serif", 16));
        joinGameBtn.setStyle("-fx-text-fill: white; -fx-background-color: blue");
        joinGameBtn.setLayoutX(525);
        joinGameBtn.setLayoutY(330);
        joinGameBtn.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                try {
                    String code = codeTextField.getText();
                    Main.controller.joinGame(event, code);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        root.getChildren().add(joinGameBtn);
    }
    //create game
    //184.5478515625
    //56.0

    // 113.4140625
    //37.0
    @Override
    public void init() throws Exception{
        ws = new WSClient(new URI("ws://localhost:80"));
        controller = new Controller(ws);
        ws.setController(controller);
        ws.connectBlocking();
        while (ws.getReadyState()==ReadyState.NOT_YET_CONNECTED || ws.getReadyState()==ReadyState.CLOSED){
            ws.reconnectBlocking();
        }


    }

    public static void main(String[] args) {
        launch(args);
    }
}
