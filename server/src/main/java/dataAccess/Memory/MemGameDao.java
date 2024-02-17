package dataAccess.Memory;

import dataAccess.GameDAO;
import model.GameData;

import java.util.HashMap;

public class MemGameDao implements GameDAO {
    HashMap<String, GameData> gameData;

    public MemGameDao() {
        gameData = new HashMap<>();
    }

    public GameData addGame() {

    }

}
