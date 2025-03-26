package iut.gon.agario.model;

import iut.gon.agario.model.fabrique.FabriquePastille;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
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
    private static final long SPEED_DECAY_DURATION = 1500;
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
        for (Cell cell : player.getCells()) {
            quadTree.insert(cell); // Ajouter les cellules dans le quadTree
        }
    }

    public void removeCell(Cell cell) {
        players.remove(cell);
        quadTree.remove((Entity) cell);
        quadTree.remove(cell);
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
                    for (Cell cell : player.getCells()) {
                        if (cell.getRepresentation().getBoundsInParent().intersects(pastille.getRepresentation().getBoundsInParent())) {
                            cell.setMass(cell.getMass() + pastille.getRadius());
                            removePastille(pastille);
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

    public void deleteEntity(Entity entity, Player player) {
        if (entity instanceof Cell cell) {
            player.getCells().remove(cell);
            removeCell(cell);
        } else if (entity instanceof Pastille) {
            pastilles.remove(entity);
            FabriquePastille fabPast = new FabriquePastille(this);
            pastilles.add((Pastille) fabPast.fabrique());
        }
    }

    public double overlap(Entity other, Cell cell) {
        double distance = Math.sqrt(Math.pow(cell.getX() - other.getX(), 2) + Math.pow(cell.getY() - other.getY(), 2));
        double combinedRadius = cell.calculateRadius(cell.getMass()) + other.calculateRadius(other.getMass());
        return (combinedRadius - distance) / combinedRadius;
    }

    public boolean canAbsorb(Cell other, Cell cell) {
        if (overlap(other, cell) >= MERGE_OVERLAP && cell.getMass() >= other.getMass() * ABSORPTION_RATIO) {
            return true;
        }
        return false;
    }

    public void absorb(Cell other, Cell cell) {
        if (canAbsorb(other, cell)) {
            cell.setMass(cell.getMass() + other.getMass());
            if(cell.getRepresentation().getParent() instanceof Pane parent) {
                parent.getChildren().remove(other.getRepresentation());
            }
            deleteEntity(other,other.getPlayer());
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
            cell.setX(cell.getX() + cell.getDirectionX() * cell.getSpeed());
            cell.setY(cell.getY() + cell.getDirectionY() * cell.getSpeed());
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
