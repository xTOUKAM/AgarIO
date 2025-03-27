package iut.gon.agario.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.json.JSONObject;

public class Cell implements Entity {
    private final int id;
    private final Circle representation;
    private final Circle representationPerimeter;
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty mass;
    private double speed;
    private final Color color;
    private double directionX, directionY;
    private long lastSpeedBoostTime;
    private final Player player;

    private long mergeTimer;

    public Cell(int id, double startX, double startY, double startMass, Color color,Player player) {
        this.id = id;
        this.x = new SimpleDoubleProperty(startX);
        this.y = new SimpleDoubleProperty(startY);
        this.mass = new SimpleDoubleProperty(startMass);
        this.color = color;
        this.representation = new Circle(calculateRadius(), Color.BLACK);
        this.representationPerimeter = new Circle(calculatePerimeter(startMass), this.color);
        bindProperties();
        this.speed = initialCurrentMaxSpeed() / (Math.sqrt(this.getMass()));
        this.player = player;
    }

    private void bindProperties() {
        this.representation.centerXProperty().bind(this.x);
        this.representation.centerYProperty().bind(this.y);
        DoubleBinding radiusBinding = Bindings.createDoubleBinding(
                this::calculateRadius,
                this.mass
        );
        this.representation.radiusProperty().bind(radiusBinding);

        this.representationPerimeter.centerXProperty().bind(this.x);
        this.representationPerimeter.centerYProperty().bind(this.y);
        DoubleBinding PerimetersBinding = Bindings.createDoubleBinding(
                () -> calculatePerimeter(this.mass.get()),
                this.mass
        );
        this.representationPerimeter.radiusProperty().bind(PerimetersBinding);
    }

    public Player getPlayer(){
        return this.player;
    }

    public Circle getRepresentation() {
        return representation;
    }

    public Circle getRepresentationPerimeter() { return representationPerimeter; }


    public double calculatePerimeter(double mass) {
        return 3 * Math.PI * Math.sqrt(mass);
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

    @Override
    public double calculateRadius() {
        return 10 * Math.sqrt(this.getMass());
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
    @Override
    public JSONObject getJSON() {
        return null;
    }


}

