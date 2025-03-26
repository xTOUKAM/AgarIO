package iut.gon.agario.model;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Player implements Entity {
    private static int idCounter = 0;
    private final int id;
    private List<CompositePlayer> cells; // Liste des cellules
    private double speed;
    private final Color color;
    private double directionX, directionY;
    public String name;

    public String getName() {
        return name;
    }

    private GameWorld gameWorld;

    private long lastSpeedBoostTime = 0;
    private static final double INITIAL_MAX_SPEED = 100.0;
    private static final double COEFFICIENT_ATTENUATION = 0.3;
    private static final long MINIMUM_SPLIT = 40;

    public Player(double startX, double startY, double startMass, Color color) {
        this.id = idCounter++;
        this.color = color;
        this.cells = new ArrayList<>();
        this.cells.add(new CompositePlayer(id,this, gameWorld)); // Ajout d'une cellule au départ
        this.speed = currentMaxSpeed() / Math.sqrt(startMass);
    }

    public Player split() {
        if (cells.size() > 0) {
            CompositePlayer cell = cells.get(0);
            if (cell.getMass() > 20) {
                double newMass = cell.getMass() / 2;
                cell.setMass(newMass);
                CompositePlayer newCell1 = new CompositePlayer(idCounter++, this, gameWorld);
                newCell1.GiveSpeedBoost();
                if(cell.getRepresentation().getParent() instanceof Pane parent) {
                    parent.getChildren().add(newCell1.getRepresentation());
                }
                this.cells.add(newCell1);
                return newCell1.getPlayers().get(-1);
            }
        }
        return null;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public double getX() {
        return cells.get(0).getX();
    }

    public void setX(double x) {
        cells.get(0).setX(x);
    }

    @Override
    public double getY() {
        return cells.get(0).getY();
    }

    public void setY(double y) {
        cells.get(0).setY(y);
    }

    @Override
    public double getMass() {
        return cells.get(0).getMass();
    }

    public void setMass(double mass) {
        cells.get(0).setMass(mass);
    }

    public double getSpeed() {
        return cells.get(0).getSpeed();
    }

    public void setSpeed(double speed) {
        cells.get(0).setSpeed(speed);
    }

    public double getDirectionX() {
        return cells.get(0).getDirectionX();
    }

    public void setDirectionX(double x) {
        cells.get(0).setDirectionX(x);
    }

    public double getDirectionY() {
        return cells.get(0).getDirectionY();
    }

    public void setDirectionY(double y) {
        cells.get(0).setDirectionY(y);
    }

    public long getLastSpeedBoostTime() {
        return lastSpeedBoostTime;
    }

    public void setLastSpeedBoostTime(long lastSpeedBoostTime) {
        this.lastSpeedBoostTime = lastSpeedBoostTime;
    }

    @Override
    public double getWidth() {
        return cells.get(0).getWidth();
    }

    @Override
    public double getHeight() {
        return cells.get(0).getHeight();
    }

    @Override
    public double calculateRadius(double mass) {
        return 10 * Math.sqrt(mass);
    }

    public double currentMaxSpeed() {
        return 100.0 * Math.pow((10 / this.getMass()), 0.3);
    }

    public List<CompositePlayer> getCells() {
        return cells;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public Circle getRepresentation(){
        return cells.get(0).getRepresentation();
    }

    public CompositePlayer[] getCompositePlayer() {
        return cells.toArray(new CompositePlayer[cells.size()]);
    }
}