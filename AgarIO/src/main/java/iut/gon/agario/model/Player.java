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
    private long lastSpeedBoostTime = 0;
    private static final double MIN_SPEED = 0;
    private static final double INITIAL_MAX_SPEED = 100.0;
    private static final double COEFFICIENT_ATTENUATION = 0.3;
    private static final double ABSORPTION_RATIO = 1.33;
    private static final double MERGE_OVERLAP = 0.33;
    private static final double DECAY_FACTOR = 5.0;
    private static final long SPEED_DECAY_DURATION = 1300;
    private static final long CONTROL_RADIUS = 1000;

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

    private double calculateRadius(double mass) {
        return 10 * Math.sqrt(mass);
    }

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

    public DoubleProperty yProperty() {
        return y;
    }

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

    public void move(double cursorX, double cursorY) {
        double dx = cursorX - this.getX();
        double dy = cursorY - this.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            this.speed = MIN_SPEED;
        } else {
            double maxSpeed = currentMaxSpeed() / Math.sqrt(this.getMass());
            directionX = dx / distance;
            directionY = dy / distance;
            if (this.speed > currentMaxSpeed()) {
                long elapsedTime = System.currentTimeMillis() - lastSpeedBoostTime;
                if (elapsedTime >= SPEED_DECAY_DURATION) {
                    this.speed = maxSpeed;
                } else {
                    double decayFactor = Math.exp(-DECAY_FACTOR * elapsedTime / SPEED_DECAY_DURATION);
                    this.speed = maxSpeed + (this.speed - maxSpeed) * decayFactor;
                }
            } else {
                this.speed = maxSpeed * Math.min(1.0, distance / CONTROL_RADIUS);
            }
        }
        setX(this.getX() + directionX * speed);
        setY(this.getY() + directionY * speed);
    }

    public double overlap(Player other) {
        double distance = Math.sqrt(Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2));
        double combinedRadius = this.calculateRadius(this.getMass()) + other.calculateRadius(this.getMass());
        return (combinedRadius - distance) / combinedRadius;
    }

    public boolean canAbsorb(Player other) {
        if ((this.id == other.getId()) && (overlap(other) >= MERGE_OVERLAP)) {
            return true;
        }
        return (this.getMass() >= other.getMass() * ABSORPTION_RATIO) && (overlap(other) >= MERGE_OVERLAP);
    }

    public void absorb(Player other) {
        if (canAbsorb(other)) {
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
        lastSpeedBoostTime = System.currentTimeMillis();

        return newCell;
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
}
