package main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wsClient.InHouseDetails;
import wsClient.WSClient;

import java.beans.EventHandler;
import java.io.IOException;

public class Controller {
    WSClient ws;


    public Controller(WSClient ws) {
        this.ws = ws;
}
    public void createGame(ActionEvent event) throws IOException {
        JSONObject createGame = new JSONObject();
        createGame.put("event", "create_game");
        createGame.put("name", InHouseDetails.name);
        ws.send(createGame.toString());
        switchToGameLayout(event);
    }

    public void switchToGameLayout(ActionEvent event) throws IOException {
        Group root = new Group();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        buildGameLayout(root);
        Scene scene = new Scene(root, 550, 620);
        stage.setScene(scene);
        stage.show();
    }

    public void buildGameLayout(Group root){
        TextField cell;
        for (int r = 0; r < 9; r++){
            for (int c = 0; c < 9; c++){
                cell = new TextField();
                cell.setPrefWidth(40);
                cell.setPrefHeight(40);
                cell.setLayoutX(15 + 60*c);
                cell.setLayoutY(75 + 60*r);
                root.getChildren().add(cell);
            }
        }
        int[] verticalLines  = {185, 365};
        int[] horizontalLines  = {245, 425};
        Line line;
        for (int x: verticalLines){
            line = new Line(x, 75, x, 595);
            line.setStrokeWidth(4);
            root.getChildren().add(line);
        }
        for (int y: horizontalLines){
            line = new Line(15, y, 535, y);
            line.setStrokeWidth(4);
            root.getChildren().add(line);
        }
    }
}
