package iut.gon.agario.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Pellet implements Entity {
    private static int idCounter = 0;
    private final int id;
    private final Circle representation;
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty radius;
    private static final double PELLET_MASS = 5;
    private Color color;
    public Pellet(double startX, double startY, double startRadius, Color color) {
        this.id = idCounter++;
        this.x = new SimpleDoubleProperty(startX);
        this.y = new SimpleDoubleProperty(startY);
        this.radius = new SimpleDoubleProperty(startRadius);
        this.representation = new Circle(startRadius, color);
        this.color = color;
        bindProperties();
    }

    private void bindProperties() {
        this.representation.centerXProperty().bind(this.x);
        this.representation.centerYProperty().bind(this.y);
        this.representation.radiusProperty().bind(this.radius);
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

    @Override
    public double calculateRadius(double mass) {
        return 10 * Math.sqrt(mass);
    }

    @Override
    public double getMass() {
        return PELLET_MASS;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public double getRadius() {
        return radius.get();
    }
    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }

    @Override
    public double getWidth() {
        return radius.get() * 2;
    }

    @Override
    public double getHeight() {
        return radius.get() * 2;
    }

    public Color getColor() {
        return this.color;
    }
}