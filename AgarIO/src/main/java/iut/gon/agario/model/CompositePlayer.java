package iut.gon.agario.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Circle;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class CompositePlayer implements Entity {
    private final List<Player> players = new ArrayList<>();
    private final GameWorld gameWorld;
    private final DoubleProperty x;
    private final DoubleProperty y;
    private static final long MINIMUM_SPLIT = 40;
    private double speed;
    private double directionX, directionY;
    private long lastSpeedBoostTime;
    private final DoubleProperty mass;
    private final Circle representation;
    private final int id;
    private final Color color;

    // Constructeur pour le composite player avec l'ID du joueur
    public CompositePlayer(int playerId, Player initialPlayer, GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        players.add(initialPlayer);
        this.x = new SimpleDoubleProperty(calculateCompositeX());
        this.y = new SimpleDoubleProperty(calculateCompositeY());
        this.mass = new SimpleDoubleProperty(initialPlayer.getMass());
        this.color = initialPlayer.getColor();
        this.representation = new Circle(calculateRadius(initialPlayer.getMass()));
        bindProperties();
        this.speed = initialCurrentMaxSpeed() / Math.sqrt(this.getMass());
        this.id = playerId;  // Assignation de l'ID passé en paramètre
    }

    // Méthode pour lier les propriétés
    private void bindProperties() {
        this.representation.centerXProperty().bind(this.x);
        this.representation.centerYProperty().bind(this.y);
        DoubleBinding radiusBinding = Bindings.createDoubleBinding(
                () -> calculateRadius(this.mass.get()),
                this.mass
        );
        this.representation.radiusProperty().bind(radiusBinding);
    }

    // Split des joueurs du composite
    public void split(double cursorX, double cursorY) {
        List<Player> newCells = new ArrayList<>();

        for (Player player : players) {
            if (player.getMass() >= MINIMUM_SPLIT) {
                Player newCell = player.split();
                if (newCell != null) {
                    newCells.add(newCell);
                    gameWorld.addEntity(newCell);
                }
            }
        }
        players.addAll(newCells);
    }

    // Déplacement du composite player et de ses cellules
    public void move(double cursorX, double cursorY) {
        boolean moving = Math.abs(cursorX - this.x.get()) > 1 || Math.abs(cursorY - this.y.get()) > 1;

        for (Player player : players) {
            gameWorld.move(cursorX, cursorY, player);
        }

        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player p1 = players.get(i);
                Player p2 = players.get(j);
                double dx = p2.getX() - p1.getX();
                double dy = p2.getY() - p1.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double minDistance = p1.getRepresentation().getRadius() + p2.getRepresentation().getRadius();

                if (distance < minDistance) {
                    double overlap = minDistance - distance;
                    double adjustX = (dx / distance) * overlap * 0.5;
                    double adjustY = (dy / distance) * overlap * 0.5;

                    p1.setX(p1.getX() - adjustX);
                    p1.setY(p1.getY() - adjustY);
                    p2.setX(p2.getX() + adjustX);
                    p2.setY(p2.getY() + adjustY);
                }
            }
        }
    }

    public double initialCurrentMaxSpeed() {
        return 100.0 * Math.pow((10 / this.getMass()), 0.3);
    }

    // Calculer la masse totale de toutes les cellules
    public double getTotalMass() {
        return players.stream().mapToDouble(Player::getMass).sum();
    }

    // Récupérer la couleur du composite player
    public Color getColor() {
        return this.color;  // Toutes les cellules ont la même couleur
    }

    // Calculer la position X moyenne du composite player
    private double calculateCompositeX() {
        double sumX = 0;
        for (Player player : players) {
            sumX += player.getX();
        }
        return sumX / players.size();
    }

    // Calculer la position Y moyenne du composite player
    private double calculateCompositeY() {
        double sumY = 0;
        for (Player player : players) {
            sumY += player.getY();
        }
        return sumY / players.size();
    }

    // Récupérer la position X du composite player
    @Override
    public double getX() {
        return x.get();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    // Récupérer la position Y du composite player
    @Override
    public double getY() {
        return y.get();
    }

    public void setY(double y) {
        this.y.set(y);
    }

    // Récupérer la masse totale du composite player
    @Override
    public double getMass() {
        return getTotalMass();
    }

    public void setMass(double Mass){
        this.mass.set(Mass);
    }

    // Récupérer la largeur d'une cellule du composite player
    @Override
    public double getWidth() {
        return players.get(0).getWidth();
    }

    // Récupérer la hauteur d'une cellule du composite player
    @Override
    public double getHeight() {
        return players.get(0).getHeight();
    }

    // Récupérer l'ID du composite player
    @Override
    public int getId() {
        return id;
    }

    public double getDirectionX() {
        return this.directionX;
    }

    public void setDirectionX(double x) {
        this.directionX = x;
    }

    public double getDirectionY() {
        return this.directionY;
    }

    public void setDirectionY(double y) {
        this.directionY = y;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    // Calculer le rayon du composite player basé sur la masse totale
    @Override
    public double calculateRadius(double mass) {
        return 10 * Math.sqrt(mass);
    }

    // Ajouter un joueur au composite
    public void addPlayer(Player player) {
        players.add(player);
    }

    // Récupérer la liste des joueurs du composite
    public List<Player> getPlayers() {
        return players;
    }

    // Mettre à jour la position et la masse du composite player
    public void update() {
        for (Player player : players) {
            gameWorld.move(player.getX(), player.getY(), player);
        }
        this.x.set(calculateCompositeX());
        this.y.set(calculateCompositeY());
    }

    // Absorber un autre joueur
    public void absorb(Entity other) {
        if (other instanceof Player otherPlayer) {
            if (canAbsorb(otherPlayer)) {
                for (Player player : players) {
                    player.setMass(player.getMass() + otherPlayer.getMass());
                }
                gameWorld.removeEntity(otherPlayer);
            }
        }
    }

    // Vérifier si ce composite player peut absorber un autre joueur
    public boolean canAbsorb(Player otherPlayer) {
        return this.getMass() > otherPlayer.getMass() * 1.33;
    }

    public void GiveSpeedBoost() {
        lastSpeedBoostTime = System.currentTimeMillis();
    }

    public long GetLastSpeedBoostTime() {
        return lastSpeedBoostTime;
    }

    public Circle getRepresentation() {
        return representation;
    }
}


