package iut.gon.agario.model;


import iut.gon.agario.model.AI.AIPlayer;
import iut.gon.agario.model.factory.PelletFactory;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameWorld {
    private final DoubleProperty width;
    private final DoubleProperty height;
    private final ObservableList<Pellet> pellets;
    private final QuadTree quadTree;
    private final ObservableList<Entity> entities;

    private static final double ABSORPTION_RATIO = 1.33;
    private static final double MERGE_OVERLAP = 0.33;
    private static final double DECAY_FACTOR = 5.0;
    private static final long SPEED_DECAY_DURATION = 1300;
    private static final long CONTROL_RADIUS = 1000;
    private static final double MIN_SPEED = 0;

    public GameWorld(double width, double height) {
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
        this.entities = FXCollections.observableArrayList();
        this.pellets = FXCollections.observableArrayList();
        this.quadTree = new QuadTree(0, new Boundary(0, 0, width, height));
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        quadTree.insert(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        quadTree.remove(entity);
    }

    public List<Entity> getEntities() {
        return new CopyOnWriteArrayList<>(entities);
    }

    public void addPlayer(Player player) {
        addEntity(player);
    }

    public List<Player> getPlayers() {
        return entities.stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());
    }

    public void addPellet(Pellet pellet) {
        pellets.add(pellet);
        quadTree.insert((Entity) pellet);
    }

    public void removePellet(Pellet pellet) {
        pellets.remove(pellet);
        quadTree.remove((Entity) pellet);
    }

    public List<Pellet> getPellets() {
        return new CopyOnWriteArrayList<>(pellets);
    }

    public List<Pellet> getPastilles() {
        return new CopyOnWriteArrayList<>(pellets);
    }

    public void update() {
        List<Player> toRemovePlayers = new ArrayList<>();
        List<Pellet> toRemovePellets = new ArrayList<>();


        Iterator<Entity> playerIterator = entities.iterator();  // Iterator for Player
        while (playerIterator.hasNext()) {
            if (playerIterator instanceof CompositePlayer compositePlayer) {
                compositePlayer.update();
            }

            Player player = (Player) playerIterator.next();
            List<Entity> nearbyEntities = quadTree.retrieve(player);

            for (Entity entity : nearbyEntities) {
                if (entity instanceof Pellet pellet) {
                    if (player.getRepresentation().getBoundsInParent().intersects(pellet.getRepresentation().getBoundsInParent())) {
                        player.setMass(player.getMass() + pellet.getRadius());
                        toRemovePellets.add(pellet);
                    }
                } else if (entity instanceof Player otherPlayer) {
                    if (player != otherPlayer && player.getRepresentation().getBoundsInParent().intersects(otherPlayer.getRepresentation().getBoundsInParent())) {
                        if (player.getMass() >= otherPlayer.getMass() * 1.33) {
                            player.setMass(player.getMass() + otherPlayer.getMass());
                            toRemovePlayers.add(otherPlayer);
                        }
                    }
                }
            }
        }
        // Remove players and pellets after iteration
        for (Player player : toRemovePlayers) {
            removeEntity(player);
        }

        for (Pellet pellet : toRemovePellets) {
            removePellet(pellet);
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

    public QuadTree getQuadTree() {
        return quadTree;
    }

    public void deleteEntity(Entity entity){
        if (entity instanceof Player){
            this.removeEntity(entity);
            entity = null;
        } else if (entity instanceof Pellet){
            pellets.remove(entity);
            entity = null;
            PelletFactory fabPast = new PelletFactory(this);
            pellets.add((Pellet) fabPast.factory());
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

    public void move(double cursorX, double cursorY, Entity entity) {
        if (entity instanceof CompositePlayer compositePlayer) {
            compositePlayer.move(cursorX, cursorY);
        } else if (entity instanceof Player player) {
            double dx = cursorX - player.getX();
            double dy = cursorY - player.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance == 0) {
                player.setSpeed(MIN_SPEED);
            } else {
                double maxSpeed = player.currentMaxSpeed() / Math.sqrt(player.getMass());
                player.setDirectionX(dx / distance);
                player.setDirectionY(dy / distance);
                if (player.getSpeed() > player.currentMaxSpeed()) {
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
    }


    public void draw(GraphicsContext gc, Camera camera) {
        gc.clearRect(0, 0, getWidth(), getHeight());
        for (Entity entity : entities) {
            if (camera.getViewBounds().contains(entity.getX(), entity.getY())) {
                // Vérifier si l'entité est un CompositePlayer
                if (entity instanceof CompositePlayer compositePlayer) {
                    // Récupérer la couleur du CompositePlayer
                    Color playerColor = compositePlayer.getColor(); // Suppose que getColor() retourne la couleur du CompositePlayer

                    // Appliquer la couleur à l'objet GraphicsContext
                    gc.setFill(playerColor);
                } else if (entity instanceof Player player) {
                    // Pour les autres entités Player (pas CompositePlayer)
                    Color playerColor = player.getColor();  // Récupérer la couleur du Player
                    gc.setFill(playerColor);
                }

                // Dessiner l'entité (cercle ou autre forme selon les propriétés de l'entité)
                gc.fillOval(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
            }
        }
    }

    public List<Player> getTopPlayers(int topN) {
        return entities.stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .sorted((p1, p2) -> Double.compare(p2.getMass(), p1.getMass()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    public void checkBotCollisions(List<AIPlayer> bots) {
        List<AIPlayer> eatenBots = new ArrayList<>();

        // Vérifier les collisions entre bots
        for (AIPlayer bot : bots) {
            for (AIPlayer otherBot : bots) {
                if (bot != otherBot && bot.getRepresentation().getBoundsInParent().intersects(otherBot.getRepresentation().getBoundsInParent())) {
                    if (bot.getMass() > otherBot.getMass() * 1.33) {
                        eatenBots.add(otherBot);
                        bot.setMass(bot.getMass() + otherBot.getMass());  // Le bot mange l'autre bot
                    }
                }
            }
        }

        // Retirer les bots mangés de la liste des bots et de la scène
        bots.removeAll(eatenBots);
        for (AIPlayer eatenBot : eatenBots) {
            if (eatenBot.getRepresentation().getParent() instanceof Pane parent) {
                parent.getChildren().remove(eatenBot.getRepresentation());
            }
        }
    }


}