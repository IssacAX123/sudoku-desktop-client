package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import wsClient.WSClient;

public class Main extends Application {
    WSClient ws;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Controller controller = new Controller(this.ws);
        loader.setController(controller);
        Parent root = loader.load();

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 760));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception{
        ws = new WSClient(new URI("ws://localhost:80"));
        ws.connect();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
