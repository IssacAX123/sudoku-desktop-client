package main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wsClient.InHouseDetails;
import wsClient.WSClient;

import java.beans.EventHandler;
import java.io.IOException;
import java.util.Arrays;

public class Controller {
    WSClient ws;
    TextField[][] allTextFields;


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
        allTextFields = new TextField[9][9];
        for (int r = 0; r < 9; r++){
            for (int c = 0; c < 9; c++){
                cell = new TextField();
                cell.setPrefWidth(40);
                cell.setPrefHeight(40);
                cell.setLayoutX(15 + 60*c);
                cell.setLayoutY(75 + 60*r);
                allTextFields[r][c] = cell;
                TextField finalCell = cell;
                cell.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue,
                                        String newValue) {
                        if (!newValue.matches("\\d")) {
                            finalCell.setText(newValue.replaceAll("[^\\d]", ""));
                        }else{
                            int value = validate(Integer.parseInt(newValue));
                            int row = (int) Math.floor(Integer.parseInt(newValue)/9);
                            int col = Integer.parseInt(newValue) % 9;
                            int[] location= {row, col};
                            JSONObject createGame = new JSONObject();
                            createGame.put("event", "move");
                            createGame.put("name", InHouseDetails.name);
                            createGame.put("code", InHouseDetails.id);
                            createGame.put("location", location);
                            createGame.put("value", value);
                            ws.send(createGame.toString());
                        }
                    }
                });
                cell.setFont(Font.font("sans-serif", FontWeight.SEMI_BOLD, 16));
                cell.setAlignment(Pos.CENTER);
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

    public int validate(int value){
        String[] x ={"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (String element : x) {
            if (element.equals(String.valueOf(value))) {
                return value;
            }else{
                return 0;
            }
        }
        return 0;
    }

    public void drawValuesToGrid(){
        for (int r = 0; r < 9; r++){
            for (int c = 0; c < 9; c++){
                if (InHouseDetails.playing_board[r][c] != 0){
                    allTextFields[r][c].setText(String.valueOf(InHouseDetails.playing_board[r][c]));
                    allTextFields[r][c].setFont(Font.font("sans-serif", FontWeight.MEDIUM, 16));
                    allTextFields[r][c].setDisable(true);
                }
            }
        }
    }
}
