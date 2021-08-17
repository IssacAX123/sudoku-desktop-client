package wsClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import jdk.nashorn.internal.parser.JSONParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wsClient.InHouseDetails;

public class WSClient extends WebSocketClient {

    public WSClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WSClient(URI serverURI) {
        super(serverURI);
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
    public void onMessage(String message) {
        try{
            JSONObject myJSON = new JSONObject(message);
            String response = myJSON.getString("response");
            if (response.equals("GAME_JSON")){
                InHouseDetails.og_board =  (int[][]) myJSON.get("og_board");
                InHouseDetails.playing_board =  (int[][]) myJSON.get("playing_board");
                InHouseDetails.solved_board =  (int[][]) myJSON.get("solved_board");
                InHouseDetails.id =  myJSON.getString("_id");
                JSONArray JSONArrayPlayers =  myJSON.getJSONArray("players");
                for (int i = 0; i< JSONArrayPlayers.length(); i++){
                    InHouseDetails.players.add(JSONArrayPlayers.getString(i));
                }
            } else if (response.equals("INVALID_GAME_CODE")){
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
        }
        catch (JSONException e){
            System.out.println("JSON Loading Error");
        }
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

}
