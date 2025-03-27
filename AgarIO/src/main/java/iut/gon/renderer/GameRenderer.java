package iut.gon.renderer;

import iut.gon.agario.model.Camera;
import iut.gon.agario.model.GameWorld;
import iut.gon.agario.model.Pellet;
import iut.gon.agario.model.Player;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameRenderer extends Thread {
    private Canvas canvas;
    private List<Player> players;
    private Player player;
    private List<Pellet> pellets;
    private GameWorld gameWorld;
    private Camera camera;

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.players = new ArrayList<>();
        this.pellets = new ArrayList<>();
    }

    public void decodeJSON(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);

        // On récupère les informations des joueurs
        decodeEntity(jsonObject.getJSONArray("entities"));

    }



    public void decodeEntity(JSONArray entityArray) {
        pellets.clear();
        players.clear();
        for(int i = 0; i < entityArray.length(); i++) {
            JSONObject entityObject = entityArray.getJSONObject(i);
            if(entityObject.getBoolean("isPlayer")){
                players.add(new Player(entityObject.getDouble("x"), entityObject.getDouble("y"), entityObject.getDouble("mass"),Color.web(entityObject.getString("color")), entityObject.getInt("id")));
            }else{
                pellets.add(new Pellet(entityObject.getDouble("x"), entityObject.getDouble("y"), entityObject.getInt("radius"),Color.web(entityObject.getString("color"))));
            }
        }
    }

    private void decodeGameWorld(JSONObject gameWorldObject) {
        double width = gameWorldObject.getDouble("width");
        double height = gameWorldObject.getDouble("height");
        gameWorld = new GameWorld(width, height);
    }

    private void decodeCamera(JSONObject cameraObject) {
        // TODO
    }

    public void update() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Render players
        for (Player player : players) {
            gc.setFill(player.getColor());
            gc.fillOval(player.getX(), player.getY(), player.getMass(), player.getMass());
        }

        // Render pellets
        for (Pellet pellet : pellets) {
            gc.setFill(pellet.getColor());
            gc.fillOval(pellet.getX(), pellet.getY(), pellet.getRadius(), pellet.getRadius());
        }
    }

    public void run() {
        update();
    }
}
