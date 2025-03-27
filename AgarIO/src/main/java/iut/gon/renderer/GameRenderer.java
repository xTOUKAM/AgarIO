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

        JSONArray playersArray = jsonObject.getJSONArray("players");

        // On supprime les anciennes informations des joueurs
        players.clear();

        // On récupère les informations des joueurs
        decodePlayers(jsonObject.getJSONArray("players"));

        // On récupère les informations des pastilles
        decodePellets(jsonObject.getJSONArray("pellets"));

        // On récupère les informations du monde
        decodeGameWorld(jsonObject.getJSONObject("gameWorld"));
    }

    public void decodePlayers(JSONArray playersArray) {
        players.clear();
        for(int i = 0; i < playersArray.length(); i++) {
            JSONObject playerObject = playersArray.getJSONObject(i);
            String name = playerObject.getString("name");
            double x = playerObject.getDouble("x");
            double y = playerObject.getDouble("y");
            double mass = playerObject.getDouble("mass");
            String color = playerObject.getString("color");
            players.add(new Player(x,y,mass, Color.web(color)));
        }
    }

    public void decodePellets(JSONArray pelletsArray) {
        pellets.clear();
        for(int i = 0; i < pelletsArray.length(); i++) {
            JSONObject pelletObject = pelletsArray.getJSONObject(i);
            double x = pelletObject.getDouble("x");
            double y = pelletObject.getDouble("y");
            double radius = pelletObject.getDouble("radius");
            String color = pelletObject.getString("color");
            pellets.add(new Pellet(x,y,radius,Color.web(color)));
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
