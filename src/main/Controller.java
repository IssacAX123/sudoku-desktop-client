package main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.java_websocket.enums.ReadyState;
import org.json.JSONObject;
import wsClient.InHouseDetails;
import wsClient.WSClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Controller {
    WSClient ws;
    TextField[][] allTextFields;
    public Label codeLabel;


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

    public void setCode(String code){
        codeLabel.setText(code);
    }

    public void joinGame(ActionEvent event, String code) throws IOException {
        InHouseDetails.id = code;
        JSONObject createGame = new JSONObject();
        createGame.put("event", "join_game");
        createGame.put("code", code);
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

        codeLabel = new Label();
        root.getChildren().add(codeLabel);
        codeLabel.setFont(Font.font("sans-serif", 18));
        codeLabel.setLayoutX(218);
        codeLabel.setLayoutY(21);

        Button ExitGameBtn = new Button("Leave");
        ExitGameBtn.setId("checkGameBtn");
        ExitGameBtn.setFont(Font.font("sans-serif", FontWeight.BOLD, 16));
        ExitGameBtn.setStyle ("-fx-text-fill: white; -fx-background-color: red");
        ExitGameBtn.setPadding(new Insets(3, 4,3, 4));
        ExitGameBtn.setLayoutX(15);
        ExitGameBtn.setLayoutY(37);
        ExitGameBtn.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                ws.close();
                Group root = new Group();
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                Main.startScreen(root);
                Scene scene = new Scene(root, 990, 620);
                stage.setScene(scene);
                stage.show();
            }
        });
        root.getChildren().add(ExitGameBtn);

        Button checkGameBtn = new Button("Check");
        checkGameBtn.setId("checkGameBtn");
        checkGameBtn.setFont(Font.font("sans-serif", FontWeight.BOLD, 16));
        checkGameBtn.setStyle ("-fx-text-fill: white; -fx-background-color: green");
        checkGameBtn.setPadding(new Insets(3, 4,3, 4));
        checkGameBtn.setLayoutX(350);
        checkGameBtn.setLayoutY(37);
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "");
        checkGameBtn.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                boolean result = Arrays.deepEquals(InHouseDetails.playing_board, InHouseDetails.solved_board);
                if (result){
                    alert.setHeaderText("CORRECT");
                    alert.setContentText("You have solved the board");
                }else{
                    alert.setHeaderText("WRONG");
                    alert.setContentText("Fix the errors");
                }
                alert.showAndWait();

            }
        });
        root.getChildren().add(checkGameBtn);

        Button ResetGameBtn = new Button("Reset");
        ResetGameBtn.setId("SolveGameBtn");
        ResetGameBtn.setFont(Font.font("sans-serif", FontWeight.BOLD, 18));
        ResetGameBtn.setStyle ("-fx-text-fill: white; -fx-background-color: blue");
        ResetGameBtn.setPadding(new Insets(3, 4,3, 4));
        ResetGameBtn.setLayoutX(411);
        ResetGameBtn.setLayoutY(37);
        ResetGameBtn.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                InHouseDetails.playing_board = InHouseDetails.og_board;
                JSONObject createGame = new JSONObject();
                createGame.put("event", "move");
                createGame.put("extra_info", "RESET");
                createGame.put("name", InHouseDetails.name);
                createGame.put("code", InHouseDetails.id);
                ws.send(createGame.toString());
                updateValuesToGrid();
            }
        });
        root.getChildren().add(ResetGameBtn);

        Button SolveGameBtn = new Button("Solve");
        SolveGameBtn.setId("SolveGameBtn");
        SolveGameBtn.setFont(Font.font("sans-serif", FontWeight.BOLD, 18));
        SolveGameBtn.setStyle ("-fx-text-fill: white; -fx-background-color: blue");
        SolveGameBtn.setPadding(new Insets(3, 4,3, 4));
        SolveGameBtn.setLayoutX(479);
        SolveGameBtn.setLayoutY(37);
        SolveGameBtn.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent event) {
                System.out.println("before error");
                ws.close();
                System.out.println("after error");
                InHouseDetails.playing_board = InHouseDetails.solved_board;
                updateValuesToGrid();

            }
        });
        root.getChildren().add(SolveGameBtn);
    }

    public int validate(int value){
        String[] x ={"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (String element : x) {
            if (element.equals(String.valueOf(value))) {
                return value;
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
                }else{
                    int finalR = r;
                    int finalC = c;
                    TextField finalCell = allTextFields[r][c];
                    allTextFields[r][c].textProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue,
                                            String newValue) {
                            if (ws.getReadyState() == ReadyState.OPEN){
                                if (newValue.length() >1){
                                    finalCell.setText(String.valueOf(newValue.charAt(newValue.length()-1)));

                                } else if (!oldValue.equals(newValue)) {
                                    int passer;
                                    if (!newValue.equals("")) {
                                        passer = Integer.parseInt(newValue);
                                    } else {
                                        passer = 0;
                                    }
                                    int value = validate(passer);
                                    int[] location = {finalR, finalC};
                                    JSONObject createGame = new JSONObject();
                                    createGame.put("event", "move");
                                    createGame.put("extra_info", "SINGLE_MOVE");
                                    createGame.put("name", InHouseDetails.name);
                                    createGame.put("code", InHouseDetails.id);
                                    createGame.put("location", location);
                                    createGame.put("value", value);
                                    ws.send(createGame.toString());
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public void updateValuesToGrid() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (InHouseDetails.playing_board[r][c] != 0) {
                    allTextFields[r][c].setText(String.valueOf(InHouseDetails.playing_board[r][c]));
                }else{
                    allTextFields[r][c].setText("");
                }
            }
        }
    }

    public void displayErrors(){
        int row;
        int col;
        for (Map.Entry<Integer, ArrayList<ArrayList<Integer>>> mapElement : InHouseDetails.errors.entrySet()) {
            for (int i = 0; i < mapElement.getValue().size(); i++){
                row = mapElement.getValue().get(i).get(0);
                col = mapElement.getValue().get(i).get(1);
                allTextFields[row][col].setStyle("-fx-control-inner-background: #ff726f");
            }
        }
    }
    public void removeErrors(int changeLocationID){
        int row;
        int col;
        ArrayList<ArrayList<Integer>> mapElement = InHouseDetails.errors.get(changeLocationID);
        for (int i = 0; i < mapElement.size(); i++){
            row = mapElement.get(i).get(0);
            col = mapElement.get(i).get(1);
            allTextFields[row][col].setStyle("-fx-control-inner-background: #FFFFFF");
        }
    }
}
