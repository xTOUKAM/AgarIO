package iut.gon.agario.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pastille extends Entity{
    private static int idCounter = 0;
    private final int id;
    private final Circle representation;
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty radius;
    private static final double PASTILLE_MASS = 5;

    public Pastille(double startX, double startY, double startRadius, Color color) {
        this.id = idCounter++;
        this.x = new SimpleDoubleProperty(startX);
        this.y = new SimpleDoubleProperty(startY);
        this.radius = new SimpleDoubleProperty(startRadius);
        this.representation = new Circle(startRadius, color);
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

    @Override
    public double calculateRadius(double mass) {
        return 10 * Math.sqrt(mass);
    }

    @Override
    double getMass() {
        return PASTILLE_MASS;
    }

    @Override
    double getWidth() {
        return 0;
    }

    @Override
    double getHeight() {
        return 0;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public double getRadius() {
        return radius.get();
    }

}