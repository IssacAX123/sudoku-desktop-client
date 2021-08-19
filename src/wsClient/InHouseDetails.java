package wsClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InHouseDetails {
    public static String name;
    public static String id;
    public static int[][] solved_board;
    public static int[][] playing_board;
    public static int[][] og_board;
    public static ArrayList<String> players;
    public static HashMap<Integer, ArrayList<ArrayList<Integer>>> errors = new HashMap<>();
}
