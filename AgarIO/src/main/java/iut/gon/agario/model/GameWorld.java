package iut.gon.agario.model;

import iut.gon.agario.model.*;
import iut.gon.agario.model.fabrique.FabriquePastille;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameWorld {
    private final DoubleProperty width;
    private final DoubleProperty height;
    private final ObservableList<Player> players;
    private final ObservableList<Pastille> pastilles;
    private final QuadTree quadTree;

    private static final double ABSORPTION_RATIO = 1.33;
    private static final double MERGE_OVERLAP = 0.33;
    private static final double DECAY_FACTOR = 5.0;
    private static final long SPEED_DECAY_DURATION = 1300;
    private static final long CONTROL_RADIUS = 1000;
    private static final double MIN_SPEED = 0;

    public GameWorld(double width, double height) {
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
        this.players = FXCollections.observableArrayList();
        this.pastilles = FXCollections.observableArrayList();
        this.quadTree = new QuadTree(0, new Boundary(0, 0, width, height));
    }

    public void addPlayer(Player player) {
        players.add(player);
        quadTree.insert((Entity) player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        quadTree.remove((Entity) player);
    }

    public void addPastille(Pastille pastille) {
        pastilles.add(pastille);
        quadTree.insert((Entity) pastille);
    }

    public void removePastille(Pastille pastille) {
        pastilles.remove(pastille);
        quadTree.remove((Entity) pastille);
    }

    public List<Player> getPlayers() {
        return new CopyOnWriteArrayList<>(players);
    }

    public List<Pastille> getPastilles() {
        return new CopyOnWriteArrayList<>(pastilles);
    }

    public void update() {
        for (Player player : players) {
            List<Entity> nearbyEntities = quadTree.retrieve((Entity) player);
            for (Entity entity : nearbyEntities) {
                if (entity instanceof Pastille pastille) {
                    if (player.getRepresentation().getBoundsInParent().intersects(pastille.getRepresentation().getBoundsInParent())) {
                        player.setMass(player.getMass() + pastille.getRadius());
                        removePastille(pastille);
                    }
                } else if (entity instanceof Player otherPlayer) {
                    if (player != otherPlayer && player.getRepresentation().getBoundsInParent().intersects(otherPlayer.getRepresentation().getBoundsInParent())) {
                        if (player.getMass() >= otherPlayer.getMass() * 1.33) {
                            player.setMass(player.getMass() + otherPlayer.getMass());
                            removePlayer(otherPlayer);
                        }
                    }
                }
            }
        }
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public double getWidth() {
        return width.get();
    }

    public double getHeight() {
        return height.get();
    }

    public void deleteEntity(Entity entity){
        if (entity instanceof Player){
            players.remove(entity);
            entity = null;
        } else if (entity instanceof Pastille){
            pastilles.remove(entity);
            entity = null;
            FabriquePastille fabPast = new FabriquePastille(this);
            pastilles.add((Pastille) fabPast.fabrique());
        }
    }

    public double overlap(Entity other, Player player){
        double distance = Math.sqrt(Math.pow(player.getX() - other.getX(), 2) + Math.pow(player.getY() - other.getY(), 2));
        double combinedRadius = player.calculateRadius(player.getMass()) + other.calculateRadius(player.getMass());
        return (combinedRadius - distance) / combinedRadius;
    }

    public boolean canAbsorb(Entity other, Player player){
        if((player.getId() == other.getId()) && (overlap(other,player) >= MERGE_OVERLAP)) {
            return true;
        }
        return (player.getMass() >= other.getMass() * ABSORPTION_RATIO) && (overlap(other,player) >= MERGE_OVERLAP);
    }

    public void absorb(Entity other, Player player) {
        if (canAbsorb(other, player)) {
            player.setMass(player.getMass() + other.getMass());
            deleteEntity(other);
        }
    }

    public void move(double cursorX, double cursorY, Player player){
        double dx = cursorX - player.getX();
        double dy = cursorY - player.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if(distance == 0) {
            player.setSpeed(MIN_SPEED);
        } else {
            double maxSpeed = player.currentMaxSpeed() / Math.sqrt(player.getMass());
            player.setDirectionX(dx / distance);
            player.setDirectionY(dy / distance);
            if(player.getSpeed() > player.currentMaxSpeed()){
                long elapsedTime = System.currentTimeMillis() - player.GetLastSpeedBoostTime();
                if (elapsedTime >= SPEED_DECAY_DURATION) {
                    player.setSpeed(maxSpeed);
                } else {
                    double decayFactor = Math.exp(-DECAY_FACTOR * elapsedTime / SPEED_DECAY_DURATION);
                    player.setSpeed(maxSpeed + (player.getSpeed() - maxSpeed) * decayFactor);
                }
            } else {
                player.setSpeed(maxSpeed * Math.min(1.0, distance / CONTROL_RADIUS));
            }
        }
        player.setX(player.getX() + player.getDirectionX() * player.getSpeed());
        player.setY(player.getY() + player.getDirectionY() * player.getSpeed());
    }

    public void draw(GraphicsContext gc, Camera camera) {
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.setFill(Color.GREEN);
        for (Player player : players) {
            if (camera.getViewBounds().contains(player.getX(), player.getY())) {
                gc.fillOval(player.getX(), player.getY(), player.getWidth(), player.getHeight());
            }
        }
    }

    public List<Player> getTopPlayers(int topN) {
        return players.stream()
                .sorted((p1, p2) -> Double.compare(p2.getMass(), p1.getMass()))
                .limit(topN)
                .collect(Collectors.toList());
    }


}