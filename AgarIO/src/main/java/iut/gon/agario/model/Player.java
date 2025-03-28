package iut.gon.agario.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Player implements Entity {
    private final int id;
    private List<Cell> cells; // Liste des cellules
    private double speed;
    private final Color color;
    private double directionX, directionY;
    public String name;

    public String getName() {
        return name;
    }

    private long lastSpeedBoostTime = 0;
    private static final double INITIAL_MAX_SPEED = 100.0;
    private static final double COEFFICIENT_ATTENUATION = 0.3;
    private static final long MINIMUM_SPLIT = 40;

    public Player(double startX, double startY, double startMass, Color color, int id) {
        this.id = id;
        this.color = color;
        this.cells = new ArrayList<>();
        this.cells.add(new Cell(id,startX, startY, startMass, color, this)); // Ajout d'une cellule au départ
        this.speed = currentMaxSpeed() / Math.sqrt(startMass);
    }

    public void split() {
        int i = this.cells.size();
        if(this.cells.size() < 16) {
            if (cells.size() > 0) {
                List<Cell> originalCells = new ArrayList<>(cells);
                for (Cell cell : originalCells) {
                    if(i < 16) {
                        i += 1;
                        cell.setMergeTimer();
                        if (cell.getMass() > 10) {
                            double newMass = cell.getMass() / 2;
                            cell.setMass(newMass);
                            Cell newCell1 = new Cell(id, cell.getX(), cell.getY(), newMass, color, this);
                            newCell1.setMergeTimer();
                            newCell1.GiveSpeedBoost();
                            newCell1.setSpeed(cell.getSpeed() * 10);

                            if (cell.getRepresentation().getParent() instanceof Pane parent) {
                                parent.getChildren().add(newCell1.getRepresentation());
                                parent.getChildren().add(newCell1.getRepresentationPerimeter());
                            }
                            this.cells.add(newCell1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public double getX() {
        return cells.get(0).getX();
    }

    @Override
    public double getY() {
        return cells.get(0).getY();
    }

    @Override
    public double getMass() {
        return cells.stream().mapToDouble(Cell::getMass).sum();
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
    public double calculateRadius() {
        return 10 * Math.sqrt(this.getMass());
    }

    public double currentMaxSpeed() {
        return 100.0 * Math.pow((10 / this.getMass()), 0.3);
    }

    public List<Cell> getCells() {
        return cells;
    }

    public char[] getPosition() {
        String position = "(" + this.getX() + ", " + this.getY() + ")";
        return position.toCharArray();
    }

    public char[] getScore() {
        return String.valueOf(this.getMass()).toCharArray();
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public JSONObject getJSON() {
        JSONObject JSONPlayer = new JSONObject();
        JSONPlayer.put("isPlayer", true);
        JSONPlayer.put("name", this.name);
        JSONPlayer.put("x", this.getX());
        JSONPlayer.put("y", this.getY());
        JSONPlayer.put("mass", this.getMass());
        JSONPlayer.put("color", this.color);
        JSONPlayer.put("id", id);
        return JSONPlayer;
    }

    public void setX(double newX) {
        this.directionX = newX;
    }

    public void setY(double newY) {
        this.directionY = newY;
    }
}
