package iut.gon.agario.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameWorld {
    private final DoubleProperty width;
    private final DoubleProperty height;
    private final ObservableList<Player> players;
    private final ObservableList<Pastille> pastilles;
    private final QuadTree quadTree;

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


}
