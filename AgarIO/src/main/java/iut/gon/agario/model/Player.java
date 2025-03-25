package iut.gon.agario.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Player {
    private static int idCounter = 0;
    private final int id;
    private final Circle representation;
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty mass;
    private double speed;
    private final Color color;
    private double directionX, directionY;
    private static final double MIN_SPEED = 0;
    private static final double MAX_SPEED = 100.0;
    private static final double ABSORPTION_RATIO = 1.33;
    private static final double MERGE_OVERLAP = 0.33;

    public Player(double startX, double startY, double startMass, Color color) {
        this.id = idCounter++;
        this.x = new SimpleDoubleProperty(startX);
        this.y = new SimpleDoubleProperty(startY);
        this.mass = new SimpleDoubleProperty(startMass);
        this.color = color;
        this.representation = new Circle(calculateRadius(startMass), this.color);
        bindProperties();
        this.speed = MAX_SPEED / Math.sqrt(this.getMass());
    }

    private void bindProperties() {
        this.representation.centerXProperty().bind(this.x);
        this.representation.centerYProperty().bind(this.y);
        DoubleBinding radiusBinding = Bindings.createDoubleBinding(
                () -> calculateRadius(this.mass.get()),
                this.mass
        );
        this.representation.radiusProperty().bind(radiusBinding);
    }

    private double calculateRadius(double mass) {
        return 10 * Math.sqrt(mass);
    }

    public int getId() {
        return id;
    }

    public Circle getRepresentation() {
        return representation;
    }

    public double getX() {
        return x.get();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public double getY() {
        return y.get();
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public double getMass() {
        return mass.get();
    }

    public void setMass(double mass) {
        this.mass.set(mass);
    }

    public Color getColor(){
        return this.color;
    }

    public DoubleProperty massProperty() {
        return mass;
    }

    public void move(double cursorX, double cursorY){
        double dx = cursorX - this.getX();
        double dy = cursorY - this.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if(distance == 0) {
            this.speed = MIN_SPEED;
        }else{
            double maxSpeed = MAX_SPEED / Math.sqrt(this.getMass());
            directionX = dx / distance;
            directionY = dy / distance;
            speed = maxSpeed * (distance / 500.0);
        }
        setX(this.getX() + directionX * speed);
        setY(this.getY() + directionY * speed);
    }

    public double overlap(Player other){
        double distance = Math.sqrt(Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2));
        double combinedRadius = this.calculateRadius(this.getMass()) + other.calculateRadius(this.getMass());
        return (combinedRadius - distance) / combinedRadius;
    }

    public boolean canAbsorb(Player other){
        if((this.id == other.getId()) && (overlap(other) >= MERGE_OVERLAP)) {
            return true;
        }

        return (this.getMass() >= other.getMass() * ABSORPTION_RATIO) && (overlap(other) >= MERGE_OVERLAP);
    }

    public void absorb(Player other){
        if(canAbsorb(other)){
            this.setMass(this.getMass() + other.getMass());
        }
    }

    public Player split() {
        if (this.getMass() < 20) return null;

        double newMass = this.getMass() / 2;
        this.setMass(newMass);
        Player newCell = new Player(this.getX(), this.getY(), newMass, this.getColor());

        newCell.setX(this.getX() + directionX * 10);
        newCell.setY(this.getY() + directionY * 10);
        newCell.speed = this.speed * 3;

        return newCell;
    }
}
