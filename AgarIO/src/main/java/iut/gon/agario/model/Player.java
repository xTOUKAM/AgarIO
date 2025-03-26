package iut.gon.agario.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Player implements Entity {
    private static int idCounter = 0;
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

    public Player(double startX, double startY, double startMass, Color color) {
        this.id = idCounter++;
        this.color = color;
        this.cells = new ArrayList<>();
        this.cells.add(new Cell(id,startX, startY, startMass, color, this)); // Ajout d'une cellule au départ
        this.speed = currentMaxSpeed() / Math.sqrt(startMass);
    }

    public void split() {
        if (cells.size() > 0) {
            Cell cell = cells.get(0);
            if (cell.getMass() > 20) {
                double newMass = cell.getMass() / 2;
                cell.setMass(newMass);
                Cell newCell1 = new Cell(idCounter++, cell.getX()+50, cell.getY()+50, newMass, color, this);
                newCell1.GiveSpeedBoost();
                if(cell.getRepresentation().getParent() instanceof Pane parent) {
                   parent.getChildren().add(newCell1.getRepresentation());
                }
                this.cells.add(newCell1);
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
    public double calculateRadius(double mass) {
        return 10 * Math.sqrt(mass);
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
}
