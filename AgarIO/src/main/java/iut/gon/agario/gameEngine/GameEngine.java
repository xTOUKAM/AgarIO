package iut.gon.agario.gameEngine;

import iut.gon.agario.model.*;
import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.factory.PelletFactory;
import iut.gon.agario.model.factory.PlayerFactory;
import iut.gon.serveur.GameServer;
import iut.gon.serveur.MessageType;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Pane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameEngine extends Thread {

    private static final double ABSORPTION_RATIO = 1.33;
    private static final double MERGE_OVERLAP = 0.33;
    private static final double DECAY_FACTOR = 5.0;
    private static final long SPEED_DECAY_DURATION = 1500;
    private static final long CONTROL_RADIUS = 1000;
    private static final double MIN_SPEED = 0;

    private final GameServer gameServer;
    private final QuadTree gameMap;
    private final double maxWidth;
    private final double maxHeight;
    private final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<Coords> playersCursors = new ArrayList<>();

    public GameEngine(GameServer gameServer, double mapWidth, double mapHeight, int nbPellet){
        this.gameServer = gameServer;
        gameMap = QuadTree.buildEmptyTree(mapWidth, mapHeight, 6);
        maxWidth = mapWidth;
        maxHeight = mapHeight;


        //Game Initialization

        //Pellet
        for(int i = 0; i < nbPellet; i++){
            gameMap.addEntity(PelletFactory.factory(maxWidth, maxHeight));
        }

    }

    public String getJson(Player player, ArrayList<Entity> entities){
        JSONObject stringPackage = new JSONObject();

        JSONArray pelletArray = new JSONArray();
        JSONArray playerArray = new JSONArray();
        for(Entity entity : entities){
            if(entity instanceof Player){
                Player playerEntity = (Player)entity;
                JSONObject JSONPlayer = new JSONObject();
                JSONPlayer.put("name", playerEntity.name);
                JSONPlayer.put("x", playerEntity.getX());
                JSONPlayer.put("y", playerEntity.getY());
                JSONPlayer.put("radius", playerEntity.calculateRadius(playerEntity.getMass()));
                JSONPlayer.put("color", "00FF00");
                playerArray.put(JSONPlayer);
            }else{
                JSONObject JSONPellet = new JSONObject();
                JSONPellet.put("x", entity.getX());
                JSONPellet.put("y", entity.getY());
                JSONPellet.put("radius", entity.calculateRadius(entity.getMass()));
                JSONPellet.put("color", "FF0000");

                playerArray.put(JSONPellet);
            }
        }



        JSONObject camera = new JSONObject();
        camera.put("starX", player.getX());
        camera.put("starY", player.getY());
        camera.put("width", 200);
        camera.put("height", 200);

        stringPackage.put("camera", camera);
        stringPackage.put("pellet", pelletArray);
        stringPackage.put("players", playerArray);

        return stringPackage.toString();
    }

    public void addPlayer(int ID){
        Player newPlayer = (Player) PlayerFactory.factory(maxWidth, maxHeight);
        players.add(ID, newPlayer);
        gameMap.addEntity(newPlayer);
    }


    public void move(double cursorX, double cursorY, Player player) {
        for (Cell cell : player.getCells()) {
            double dx = cursorX - cell.getX();
            double dy = cursorY - cell.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance == 0) {
                cell.setSpeed(MIN_SPEED);
            } else {
                double maxSpeed = cell.initialCurrentMaxSpeed() / Math.sqrt(cell.getMass());
                cell.setDirectionX(dx / distance);
                cell.setDirectionY(dy / distance);
                if (cell.getSpeed() > cell.initialCurrentMaxSpeed()) {
                    long elapsedTime = System.currentTimeMillis() - cell.GetLastSpeedBoostTime();
                    if (elapsedTime >= SPEED_DECAY_DURATION) {
                        cell.setSpeed(maxSpeed);
                    } else {
                        double decayFactor = Math.exp(-DECAY_FACTOR * elapsedTime / SPEED_DECAY_DURATION);
                        cell.setSpeed(maxSpeed + (cell.getSpeed() - maxSpeed) * decayFactor);
                    }
                } else {
                    cell.setSpeed(maxSpeed * Math.min(1.0, distance / CONTROL_RADIUS));
                }
            }
            cell.setX(cell.getX() + cell.getDirectionX() * cell.getSpeed());
            cell.setY(cell.getY() + cell.getDirectionY() * cell.getSpeed());
        }
    }


    @Override
    public void run(){
        try {
            while(true){
                Thread.sleep(500);

                for(Player player : this.players){

                    //movement
                    Coords cursor = playersCursors.get(player.getId());
                    move(cursor.x, cursor.y, player);

                    //
                    String json = getJson(player, gameMap.getEntitiesFromPoint(player.getX(), player.getY(), 200, 200));
                    System.out.println(json);
                    gameServer.sendToClientByID(MessageType.SERVER_GAME_STATE, json, player.getId(), true);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
