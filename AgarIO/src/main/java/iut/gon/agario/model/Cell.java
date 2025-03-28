package iut.gon.agario.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Cell implements Entity {
    private final int id;
    private final Circle representation;
    private final Circle representationPerimettre;
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty mass;
    private double speed;
    private final Color color;
    private double directionX, directionY;
    private long lastSpeedBoostTime;
    private Player player;
    private Animation animation;

    private long mergeTimer;

    public Cell(int id, double startX, double startY, double startMass, Color color,Player player) {
        this.id = id;
        this.x = new SimpleDoubleProperty(startX);
        this.y = new SimpleDoubleProperty(startY);
        this.mass = new SimpleDoubleProperty(startMass);
        this.color = color;
        this.representation = new Circle(calculateRadius(startMass), Color.BLACK);
        this.representationPerimettre = new Circle(calculatePerimeter(startMass), this.color);
        bindProperties();
        this.speed = initialCurrentMaxSpeed() / (Math.sqrt(this.getMass()));
        this.player = player;
    }

    private void bindProperties() {
        this.representation.centerXProperty().bind(this.x);
        this.representation.centerYProperty().bind(this.y);
        DoubleBinding radiusBinding = Bindings.createDoubleBinding(
                () -> calculateRadius(this.mass.get()),
                this.mass
        );
        this.representation.radiusProperty().bind(radiusBinding);

        this.representationPerimettre.centerXProperty().bind(this.x);
        this.representationPerimettre.centerYProperty().bind(this.y);
        DoubleBinding PerimetersBinding = Bindings.createDoubleBinding(
                () -> calculateAngle(this.mass.get()),
                this.mass
        );
        this.representationPerimettre.radiusProperty().bind(PerimetersBinding);
    }

    public void unBindProperties() {
        this.representation.centerXProperty().unbind();
        this.representation.centerYProperty().unbind();
        this.representation.radiusProperty().unbind();
        this.representationPerimettre.centerXProperty().unbind();
        this.representationPerimettre.centerYProperty().unbind();
        this.representationPerimettre.radiusProperty().unbind();
        this.animation = new Animation(this.representation, this.representationPerimettre);
    }

    public Player getPlayer(){
        return this.player;
    }

    public Circle getRepresentation() {
        return representation;
    }

    public Circle getRepresentationPerimeter() { return representationPerimettre; }

    @Override
    public double calculateRadius(double mass) {
        return 10 * Math.sqrt(mass);
    }

    public double calculatePerimeter(double mass) {
        return 3 * Math.PI * Math.sqrt(mass);
    }

    public double calculateAngle(double mass) {
        return (3 * Math.sqrt(3) / 4) * mass * mass;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public double getX() {
        return x.get();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public DoubleProperty xProperty() {
        return x;
    }

    @Override
    public double getY() {
        return y.get();
    }

    public void setY(double y) {
        this.y.set(y);
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

    @Override
    public double getMass() {
        return mass.get();
    }

    public void setMass(double mass) {
        this.mass.set(mass);
    }

    public Color getColor() {
        return this.color;
    }

    public DoubleProperty massProperty() {
        return mass;
    }

    public double initialCurrentMaxSpeed() {
        return 10 ;
    }

    @Override
    public double getWidth() {
        return this.representation.getRadius() * 2;
    }

    @Override
    public double getHeight() {
        return this.representation.getRadius() * 2;
    }

    public long GetLastSpeedBoostTime() {
        return lastSpeedBoostTime;
    }

    public void GiveSpeedBoost(){
        lastSpeedBoostTime = System.currentTimeMillis();
    }

    public void setMergeTimer(){
        mergeTimer = System.currentTimeMillis();
    }

    public long getMergeTimer(){
        return this.mergeTimer;
    }
}
