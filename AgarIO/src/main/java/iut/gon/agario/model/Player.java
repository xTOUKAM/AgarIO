package iut.gon.agario.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Player extends Entity{
    private static int idCounter = 0;
    private final int id;
    private final Circle representation;
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty mass;
    private double speed;
    private final Color color;
    private double directionX, directionY;
    private long lastSpeedBoostTime = 0;
    private static final double INITIAL_MAX_SPEED = 100.0;
    private static final double COEFFICIENT_ATTENUATION = 0.3;


    public Player(double startX, double startY, double startMass, Color color) {
        this.id = idCounter++;
        this.x = new SimpleDoubleProperty(startX);
        this.y = new SimpleDoubleProperty(startY);
        this.mass = new SimpleDoubleProperty(startMass);
        this.color = color;
        this.representation = new Circle(calculateRadius(startMass), this.color);
        bindProperties();
        this.speed = currentMaxSpeed() / Math.sqrt(this.getMass());
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

    @Override
    public double calculateRadius(double mass) {
        return 10 * Math.sqrt(mass);
    }
    @Override
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
    public long GetLastSpeedBoostTime(){
        return this.lastSpeedBoostTime;
    }
    public void SetLastSpeedBoostTime(long last){
        this.lastSpeedBoostTime = last;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    @Override
    public double getMass() {
        return mass.get();
    }

    @Override
    double getWidth() {
        return 0;
    }

    @Override
    double getHeight() {
        return 0;
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

    public double currentMaxSpeed(){
        return INITIAL_MAX_SPEED * Math.pow((10 / this.getMass()), COEFFICIENT_ATTENUATION);
    }
}