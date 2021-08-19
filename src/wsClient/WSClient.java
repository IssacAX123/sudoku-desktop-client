package wsClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import jdk.nashorn.internal.parser.JSONParser;
import main.Controller;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wsClient.InHouseDetails;

public class WSClient extends WebSocketClient {
    Controller controller;

    public WSClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WSClient(URI serverURI) {
        super(serverURI);
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onWebsocketCloseInitiated(WebSocket conn, int code, String reason){
        JSONObject closeJSON = new JSONObject();
        closeJSON.put("event", "DISCONNECTING");
        closeJSON.put("player", InHouseDetails.name);
        closeJSON.put("code", InHouseDetails.id);
        send(closeJSON.toString());
    }

    @Override
    public void onMessage(String message) {
//        try{
        JSONObject myJSON = new JSONObject(message);
        String response = myJSON.getString("response");

        String extra_info = myJSON.getString("extra_info");
        if (response.equals("GAME_JSON") && (extra_info.equals("CREATE_GAME") || extra_info.equals("JOIN_GAME"))){
            InHouseDetails.og_board =   JSONArrayToArray2D(myJSON.getJSONArray("og_board"));
            InHouseDetails.playing_board =  JSONArrayToArray2D(myJSON.getJSONArray("playing_board"));
            InHouseDetails.solved_board =  JSONArrayToArray2D(myJSON.getJSONArray("solved_board"));
            InHouseDetails.id =  myJSON.getString("_id");
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    controller.setCode("code: " + myJSON.getString("_id"));
                }
            });
            InHouseDetails.players =  JSONArrayToStringArray(myJSON.getJSONArray("players"));
            this.controller.drawValuesToGrid();
        } else if (response.equals("GAME_JSON") && extra_info.equals("MOVE_JSON")){
            InHouseDetails.playing_board =  JSONArrayToArray2D(myJSON.getJSONArray("playing_board"));
            this.controller.updateValuesToGrid();
        }else if (response.equals("INVALID_GAME_CODE")){
            System.out.println("alert error");
        } else if (response.equals("INNIT")){
            InHouseDetails.name =  myJSON.getString("name");
        }
        else if (response.equals("NEW_PLAYER")){
            InHouseDetails.players.add(myJSON.getString("name"));
        }
        else if (response.equals("PLAYER_DISCONNECTED")){
            InHouseDetails.players.remove(myJSON.getString("name"));
        }
        else if (response.equals("ERRORS")){
            ArrayList<Integer> changeLocation = JSONArrayToIntArray(myJSON.getJSONArray("changed_location"));
            int changeLocationID  = (changeLocation.get(0)*9) + changeLocation.get(1);
            if (myJSON.getJSONArray("errors").length() > 0){
                ArrayList<ArrayList<Integer>> errors = JSONArrayToArrayList2D(myJSON.getJSONArray("errors"));
                InHouseDetails.errors.put(changeLocationID, errors);
                controller.displayErrors();
            }else{
                if (InHouseDetails.errors.containsKey(changeLocationID)){
                    controller.removeErrors(changeLocationID);
                    InHouseDetails.errors.remove(changeLocationID);
                }
            }
        }else if (response.equals("RESET_JSON")){
            InHouseDetails.playing_board = InHouseDetails.og_board;
            controller.updateValuesToGrid();
        }
//    }
//        catch (JSONException e){
//            System.out.println("JSON Loading Error");
//        }
    }

    public int[][] JSONArrayToArray2D(JSONArray jsonArray){
        int[][] chosen = new int[9][9];
        for (int r = 0; r< jsonArray.length(); r++){
            for (int c = 0; c< jsonArray.getJSONArray(r).length(); c++){
                chosen[r][c] = jsonArray.getJSONArray(r).getInt(c);
            }
        }
        return chosen;
    }

    public ArrayList<ArrayList<Integer>> JSONArrayToArrayList2D(JSONArray jsonArray){
        ArrayList<ArrayList<Integer>> chosen = new ArrayList<>();
        JSONArray chosenRow;
        ArrayList<Integer> newChosenRow;
        for (int r = 0; r< jsonArray.length(); r++){
            chosenRow = jsonArray.getJSONArray(r);
            newChosenRow = JSONArrayToIntArray(chosenRow);
            chosen.add(r, newChosenRow);
        }
        return chosen;
    }

    public ArrayList<String> JSONArrayToStringArray(JSONArray jsonArray){
        ArrayList<String> chosen = new ArrayList<String>();
        for (int i = 0; i< jsonArray.length(); i++){
            chosen.add(jsonArray.getString(i));
        }
        return chosen;
    }

    public ArrayList<Integer> JSONArrayToIntArray(JSONArray jsonArray){
        ArrayList<Integer> chosen = new ArrayList<Integer>();
        for (int i = 0; i< jsonArray.length(); i++){
            chosen.add(jsonArray.getInt(i));
        }
        return chosen;
    }


    @Override
    public void onError(Exception ex) {
        System.out.println(ex);
    }

}
