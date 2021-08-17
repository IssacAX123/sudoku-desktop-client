package main;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wsClient.InHouseDetails;
import wsClient.WSClient;

public class Controller {
    WSClient ws;
    public Controller(WSClient ws) {
        this.ws = ws;
}
    public void createGame(){
        JSONObject createGame = new JSONObject();
        createGame.put("event", "create_game");
        createGame.put("name", InHouseDetails.name);
        ws.send(createGame.toString());
    }


}
