package iut.gon.agario.model;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class CompositePlayer implements Entity {
    private final List<Player> players = new ArrayList<>();
    private final GameWorld gameWorld;
    private double x;
    private double y;

    public CompositePlayer(Player initialPlayer, GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        players.add(initialPlayer);
        this.x = calculateCompositeX();
        this.y = calculateCompositeY();
    }

    public void split() {
        List<Player> newCells = new ArrayList<>();
        for (Player player : players) {
            Player newCell = player.split();
            if (newCell != null) {
                newCells.add(newCell);
                gameWorld.addEntity(newCell);
            }
        }
        players.addAll(newCells);
    }

    public void move(double cursorX, double cursorY) {
        for (Player player : players) {
            gameWorld.move(cursorX, cursorY, player);
        }
    }

    public double getTotalMass() {
        return players.stream().mapToDouble(Player::getMass).sum();
    }

    public Color getColor() {
        return players.get(0).getColor();
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public double getX() {
        return players.stream().mapToDouble(Player::getX).average().orElse(0);
    }

    @Override
    public double getY() {
        return players.stream().mapToDouble(Player::getY).average().orElse(0);
    }

    @Override
    public double getMass() {
        return getTotalMass();
    }

    @Override
    public double getWidth() {
        return players.get(0).getWidth();
    }

    @Override
    public double getHeight() {
        return players.get(0).getHeight();
    }

    @Override
    public int getId() {
        return players.get(0).getId();
    }

    @Override
    public double calculateRadius(double mass) {
        return players.get(0).calculateRadius(mass);
    }

    private double calculateCompositeX() {
        double sumX = 0;
        for (Player player : players) {
            sumX += player.getX();
        }
        return sumX / players.size();
    }

    private double calculateCompositeY() {
        double sumY = 0;
        for (Player player : players) {
            sumY += player.getY();
        }
        return sumY / players.size();
    }

    public void update() {
        for (Player player : players) {
           gameWorld.move(player.getX(), player.getY(), player);
        }

        this.x = calculateCompositeX();
        this.y = calculateCompositeY();

    }
}

