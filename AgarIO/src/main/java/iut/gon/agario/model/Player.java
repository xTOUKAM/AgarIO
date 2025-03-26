package iut.gon.agario.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Player implements Entity {
    private static int idCounter = 0;
    private final int id;
    private final Circle representation;
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty mass;
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

    public void setMass(double mass) {
        this.mass.set(mass);
    }

    public Color getColor() {
        return this.color;
    }

    public DoubleProperty massProperty() {
        return mass;
    }

    public double currentMaxSpeed() {
        return INITIAL_MAX_SPEED * Math.pow((10 / this.getMass()), COEFFICIENT_ATTENUATION);
    }

    public void setMaxSpeed(double val){
        this.speed = val;
    }
    @Override
    public double getWidth() {
        return this.representation.getRadius() * 2;
    }

    @Override
    public double getHeight() {
        return this.representation.getRadius() * 2;
    }

    public Player split() {
        if (this.getMass() < MINIMUM_SPLIT) return null;

        double newMass = this.getMass() / 2;
        this.setMass(newMass);
        Player newCell = new Player(this.getX(), this.getY(), newMass, this.getColor());

        newCell.setX(this.getX() + this.getDirectionX() * 10);
        newCell.setY(this.getY() + this.getDirectionY() * 10);
        newCell.setSpeed(this.getSpeed() * 3);
        this.SetLastSpeedBoostTime(System.currentTimeMillis());

        return newCell;
    }
}
