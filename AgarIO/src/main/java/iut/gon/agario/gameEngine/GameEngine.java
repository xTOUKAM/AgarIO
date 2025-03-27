package iut.gon.agario.gameEngine;

import iut.gon.agario.model.*;
import iut.gon.agario.model.factory.PelletFactory;
import iut.gon.agario.model.factory.PlayerFactory;
import iut.gon.serveur.GameServer;
import iut.gon.serveur.MessageType;
import javafx.scene.layout.Pane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class GameEngine extends Thread {

    private static final double ABSORPTION_RATIO = 1.33;
    private static final double MERGE_OVERLAP = 0.33;
    private static final double DECAY_FACTOR = 5.0;
    private static final long SPEED_DECAY_DURATION = 1500;
    private static final long CONTROL_RADIUS = 1000;
    private static final double MIN_SPEED = 0;
    private static final double MIN_TIME_SPLIT = 10000;

    private final GameServer gameServer;
    private final QuadTree gameMap;
    private final double maxWidth;
    private final double maxHeight;
    private final HashMap<Integer, Player> players = new HashMap<>();
    private final HashMap<Integer, Coords> playersCursors = new HashMap<>();


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

        JSONArray entityArray = new JSONArray();
        for(Entity entity : entities){
            entityArray.put(entity.getJSON());
        }



        JSONObject camera = new JSONObject();
        camera.put("starX", player.getX());
        camera.put("starY", player.getY());
        camera.put("width", 52 * player.calculateRadius());
        camera.put("height", 32 * player.calculateRadius());

        stringPackage.put("camera", camera);
        stringPackage.put("entities", entityArray);

        return stringPackage.toString();
    }

    public void addPlayer(int ID){
        Player newPlayer = (Player) PlayerFactory.factory(maxWidth, maxHeight, ID);
        players.put(ID, newPlayer);
        gameMap.addEntity(newPlayer);
    }

    public void updateCursor(int ID, double x, double y){
        this.playersCursors.put(ID, new Coords(x, y));
    }


    public double exponentialSpeedDecay(double x, double maxSpeed) {
        double A = 20 * maxSpeed;
        double B = -Math.log(0.01 / (9 * maxSpeed)) / 1000;
        return A * Math.exp(-B * x) + maxSpeed;
    }

    public void move(double cursorX, double cursorY, Player player) {
        for (Cell cell : player.getCells()) {
            double dx = cursorX - cell.getX();
            double dy = cursorY - cell.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance == 0) {
                cell.setSpeed(MIN_SPEED);
            } else {
                double maxSpeed = cell.initialCurrentMaxSpeed() / Math.sqrt((Math.sqrt(cell.getMass())/2));
                cell.setDirectionX(dx / distance);
                cell.setDirectionY(dy / distance);
                if (cell.getSpeed() > maxSpeed) {
                    long elapsedTime = System.currentTimeMillis() - cell.GetLastSpeedBoostTime();
                    if (elapsedTime >= SPEED_DECAY_DURATION) {
                        cell.setSpeed(maxSpeed);
                    } else {
                        cell.setSpeed(exponentialSpeedDecay(elapsedTime,maxSpeed));
                    }
                } else {
                    cell.setSpeed(maxSpeed * Math.min(1.0, distance / CONTROL_RADIUS));
                }
            }

            double newX = cell.getX() + cell.getDirectionX() * cell.getSpeed();
            double newY = cell.getY() + cell.getDirectionY() * cell.getSpeed();

            for (Cell otherCell : player.getCells()) {
                if(cell != otherCell) {
                    double distBetween = Math.sqrt(Math.pow(newX - otherCell.getX(), 2) + Math.pow(newY - otherCell.getY(), 2));
                    double minDist = cell.calculateRadius();

                    if(distBetween < minDist &&
                            (System.currentTimeMillis()-cell.getMergeTimer()) < (MIN_TIME_SPLIT + (cell.getMass() / 100)) &&
                            (System.currentTimeMillis()-otherCell.getMergeTimer()) < (MIN_TIME_SPLIT + (otherCell.getMass() / 100))) {
                        double overlap = minDist - distBetween;
                        double pushX = (newX - otherCell.getX()) / distBetween * overlap;
                        double pushY = (newY - otherCell.getY()) / distBetween * overlap;
                        newX+=pushX;
                        newY+=pushY;
                    }
                }
            }
            cell.setX(newX);
            cell.setY(newY);

        }
    }

    public void deleteEntity(Entity entity, Player player) {
        if (entity instanceof Cell cell) {
            player.getCells().remove(cell);
            gameMap.removeEntity(entity);
        } else if (entity instanceof Pellet) {
            gameMap.removeEntity(entity);
            gameMap.addEntity( PelletFactory.factory(maxWidth, maxHeight));
        }
    }

    public double overlap(Entity other, Cell cell) {
        double x1 = cell.getX();
        double y1 = cell.getY();
        double x2 = other.getX();
        double y2 = other.getY();

        double r1 = cell.calculateRadius();
        double r2 = other.calculateRadius();

        double d = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

        // Si les cercles ne se chevauchent pas
        if (d >= r1 + r2) {
            return 0;
        }

        // Si un cercle est complètement à l'intérieur de l'autre
        if (d <= Math.abs(r1 - r2)) {
            return 1; // Le plus petit est totalement recouvert
        }

        // Calcul de l'aire d'intersection
        double r1Sq = r1 * r1;
        double r2Sq = r2 * r2;

        double part1 = r1Sq * Math.acos((d*d + r1Sq - r2Sq) / (2 * d * r1));
        double part2 = r2Sq * Math.acos((d*d + r2Sq - r1Sq) / (2 * d * r2));
        double part3 = 0.5 * Math.sqrt((-d + r1 + r2) * (d + r1 - r2) * (d - r1 + r2) * (d + r1 + r2));

        double intersectionArea = part1 + part2 - part3;

        // Calcul du pourcentage de recouvrement par rapport au plus petit cercle
        double smallerArea = Math.PI * Math.min(r1Sq, r2Sq);

        return intersectionArea / smallerArea;
    }

    public boolean canAbsorb(Cell other, Cell cell) {
        return ((overlap(other, cell) >= MERGE_OVERLAP && cell.getMass() >= other.getMass() * ABSORPTION_RATIO) &&
                other.getPlayer() != cell.getPlayer()) ||
                (overlap(other, cell) >= MERGE_OVERLAP && other.getPlayer() == cell.getPlayer() &&
                        (System.currentTimeMillis() - cell.getMergeTimer()) >= (MIN_TIME_SPLIT + (cell.getMass() / 100)) && (System.currentTimeMillis() - other.getMergeTimer()) >= (MIN_TIME_SPLIT + (other.getMass() / 100)));
    }

    public boolean canAbsorbPellet(Pellet other, Cell cell) {
        return overlap(other, cell) >= MERGE_OVERLAP;

    }


    public void absorb(Entity other, Cell cell) {
        if(other instanceof Cell other1) {
            if (canAbsorb(other1, cell)) {
                cell.setMass(cell.getMass() + other1.getMass());
                if (cell.getRepresentation().getParent() instanceof Pane parent) {
                    parent.getChildren().remove(other1.getRepresentation());
                    parent.getChildren().remove(other1.getRepresentationPerimeter());
                }
                deleteEntity(other1, other1.getPlayer());
            }
        }else if(other instanceof Pellet other2){
            if (canAbsorbPellet(other2,cell)){
                cell.setMass(cell.getMass() + 1);
                if (cell.getRepresentation().getParent() instanceof Pane parent) {
                    parent.getChildren().remove(other2.getRepresentation());
                }
                deleteEntity(other2,cell.getPlayer());
            }
        }
    }


    private void checkCollisions(Player player, ArrayList<Entity> allScope) {
        for (Cell cell : player.getCells()){
            for (Entity entity : allScope) {
                if(entity instanceof Pellet pellet){
                    if (canAbsorbPellet(pellet, cell)) {
                        absorb(pellet, cell);
                        gameMap.removeEntity(entity);
                    }
                }
            }
        }
        try{
            for (Cell cell : player.getCells()){
                for(Cell cell2 : player.getCells()) {
                    if(cell != cell2){
                        absorb(cell,cell2);
                    }
                }
            }
        }
        catch (Exception ignore){
        }
    }


    @Override
    public void run(){
        try {
            while(true){
                Thread.sleep(33);
                Set<Integer> allPlayerID =  this.players.keySet();
                for(Integer playerID : allPlayerID){

                    Player player = this.players.get(playerID);
                    //get all scope entities
                    ArrayList<Entity> allEntities = gameMap.getEntitiesFromPoint(player.getX(), player.getY(), 200, 200);


                    //movement
                    Coords cursor = playersCursors.get(player.getId());
                    move(cursor.x, cursor.y, player);

                    checkCollisions(player, allEntities);


                    //send update to players
                    String json = getJson(player, allEntities);
                    gameServer.sendToClientByID(MessageType.SERVER_GAME_STATE, json, player.getId(), true);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
