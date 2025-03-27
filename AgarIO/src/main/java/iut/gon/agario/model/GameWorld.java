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
    private final ObservableList<Player> players;
    private final List<Pellet> pellets;
    private final ObservableList<AIPlayer> bots;
    private final QuadTree quadTree;

    private static final double ABSORPTION_RATIO = 1.33;
    private static final double MERGE_OVERLAP = 0.33;
    private static final double DECAY_FACTOR = 6;
    private static final long SPEED_DECAY_DURATION = 1500;
    private static final long CONTROL_RADIUS = 100;
    private static final double MIN_SPEED = 0;
    private static final double MIN_TIME_SPLIT = 10000;

    public GameWorld(double width, double height) {
        this.width = new SimpleDoubleProperty(width);
        this.height = new SimpleDoubleProperty(height);
        this.players = FXCollections.observableArrayList();
        this.bots = FXCollections.observableArrayList();
        this.pellets = new ArrayList<Pellet>();
        this.quadTree = new QuadTree(0, new Boundary(0, 0, width, height));
    }

    public void addPlayer(Player player) {
        players.add(player);
        quadTree.insert((Entity) player);
        for (Cell cell : player.getCells()) {
            quadTree.insert(cell); // Ajouter les cellules dans le quadTree
        }
    }

    public void removeCell(Cell cell) {
        players.remove(cell);
        quadTree.remove((Entity) cell);
        quadTree.remove(cell);
    }

    public void addPellet(Pellet pellet) {
        pellets.add(pellet);
        quadTree.insert((Entity) pellet);
    }

    public void removePellet(Pellet pellet) {
        pellets.remove(pellet);
        quadTree.remove((Entity) pellet);
    }

    public List<Player> getPlayers() {
        return new CopyOnWriteArrayList<>(players);
    }

    public List<Pellet> getPellets() {
        return pellets;
    }

    public void update() {
        List<Player> toRemovePlayers = new ArrayList<>();
        List<Pellet> toRemovePellets = new ArrayList<>();

        Iterator<Player> playerIterator = players.iterator();  // Iterator for Player
        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            List<Entity> nearbyEntities = quadTree.retrieve((Entity) player);

            for (Entity entity : nearbyEntities) {
                if (entity instanceof Pellet pastille) {
                    for (Cell cell : player.getCells()) {
                        if (cell.getRepresentation().getBoundsInParent().intersects(pastille.getRepresentation().getBoundsInParent())) {
                            cell.setMass(cell.getMass() + pastille.getRadius());
                            removePellet(pastille);
                        }
                    }
                } else if (entity instanceof Player otherPlayer) {
                    if (player != otherPlayer) {
                        for (Cell cell : player.getCells()) {
                            for (Cell otherCell : otherPlayer.getCells()) {
                                if (cell.getRepresentation().getBoundsInParent().intersects(otherCell.getRepresentation().getBoundsInParent())) {
                                    if (canAbsorb(otherCell, cell)) {
                                        absorb(otherCell, cell);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Remove players and pellets after iteration
        /*for (Player player : toRemovePlayers) {
            removePlayer(player);
        }

        for (Pellet pellet : toRemovePellets) {
            removePellet(pellet);
        }*/
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

    public void deleteEntity(Entity entity, Player player) {
        if (entity instanceof Cell cell) {
            player.getCells().remove(cell);
            removeCell(cell);
        } else if (entity instanceof Pellet) {
            pellets.remove(entity);
            PelletFactory fabPast = new PelletFactory(this);
            pellets.add((Pellet) fabPast.factory());
        }
    }

    public double overlap(Entity other, Cell cell) {
        double distance = Math.sqrt(Math.pow(cell.getX() - other.getX(), 2) + Math.pow(cell.getY() - other.getY(), 2));
        double combinedRadius = cell.calculateRadius(cell.getMass()) + other.calculateRadius(other.getMass());
        return (combinedRadius - distance) / combinedRadius;
    }

    public boolean canAbsorb(Cell other, Cell cell) {
        if (((overlap(other, cell) >= MERGE_OVERLAP && cell.getMass() >= other.getMass() * ABSORPTION_RATIO) &&
                other.getPlayer() != cell.getPlayer()) ||
                (overlap(other, cell) >= MERGE_OVERLAP && other.getPlayer() == cell.getPlayer() &&
                (System.currentTimeMillis()-cell.getMergeTimer())>= (MIN_TIME_SPLIT + (cell.getMass() / 100)) && (System.currentTimeMillis()-other.getMergeTimer())>=(MIN_TIME_SPLIT + (other.getMass() / 100)))) {
            return true;
        }
        return false;
    }

    public boolean canAbsorbPellet(Pellet other, Cell cell) {
        if ((overlap(other, cell) >= MERGE_OVERLAP)) {
            return true;
        }
        return false;
    }

    public List<AIPlayer> getBots() {
        return bots;
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

            double newX = cell.getX() + cell.getDirectionX() * cell.getSpeed();
            double newY = cell.getY() + cell.getDirectionY() * cell.getSpeed();

            for (Cell otherCell : player.getCells()) {
                if(cell != otherCell) {
                    double distBetween = Math.sqrt(Math.pow(newX - otherCell.getX(), 2) + Math.pow(newY - otherCell.getY(), 2));
                    double minDist = cell.calculateRadius(cell.getMass() + otherCell.calculateRadius(otherCell.getMass()));

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

    public void draw(GraphicsContext gc, Camera camera) {
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.setFill(Color.GREEN);
        for (Player player : players) {
            for (Cell cell : player.getCells()) {
                if (camera.getViewBounds().contains(cell.getX(), cell.getY())) {
                    gc.fillOval(cell.getX(), cell.getY(), cell.getWidth(), cell.getHeight());
                }
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
