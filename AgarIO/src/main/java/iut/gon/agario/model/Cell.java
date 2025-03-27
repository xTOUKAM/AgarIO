package iut.gon.agario.model;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
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
    // Ajoutez une propriété pour gérer l'angle d'ouverture de la bouche
    private DoubleProperty mouthAngle; // Angle de la bouche, en radians
    private static final double MAX_MOUTH_ANGLE = Math.PI / 2; // Maximum de l'angle d'ouverture (90°)
    private static final double MOUTH_SPEED = 0.1; // Vitesse de l'animation de la bouche (modifiable)
    // Déclarez un Arc qui représente la bouche de la cellule
    private Arc mouthRepresentation;

    private long mergeTimer;

    public Cell(int id, double startX, double startY, double startMass, Color color,Player player) {
        this.id = id;
        this.x = new SimpleDoubleProperty(startX);
        this.y = new SimpleDoubleProperty(startY);
        this.mass = new SimpleDoubleProperty(startMass);
        this.color = color;
        this.mouthAngle = new SimpleDoubleProperty(0.0); // Initialisation de l'angle à 0 (bouche fermée)
        this.representation = new Circle(calculateRadius(startMass), Color.BLACK);
        this.mouthRepresentation = new Arc();
        this.mouthRepresentation.setType(ArcType.ROUND);
        this.mouthRepresentation.setFill(Color.BLACK);
        this.mouthRepresentation.centerXProperty().bind(this.x);
        this.mouthRepresentation.centerYProperty().bind(this.y);
        this.mouthRepresentation.radiusXProperty().bind(this.representation.radiusProperty());
        this.mouthRepresentation.radiusYProperty().bind(this.representation.radiusProperty());
        this.representationPerimettre = new Circle(calculatePerimeter(startMass), this.color);
        this.mouthRepresentation.setLength(mouthAngle.get());
        this.mouthAngle.addListener((observable, oldValue, newValue) -> {
            mouthRepresentation.setLength(newValue.doubleValue());
        });
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
                () -> calculatePerimeter(this.mass.get()),
                this.mass
        );
        this.representationPerimettre.radiusProperty().bind(PerimetersBinding);
    }

    public void openMouth() {
        if (this.mouthAngle.get() < MAX_MOUTH_ANGLE) {
            this.mouthAngle.set(this.mouthAngle.get() + MOUTH_SPEED); // Augmenter progressivement l'angle
        }
    }

    public void closeMouth() {
        if (this.mouthAngle.get() > 0) {
            this.mouthAngle.set(this.mouthAngle.get() - MOUTH_SPEED); // Réduire progressivement l'angle
        }
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
        return 50 * Math.pow((10 / this.getMass()), 0.1);
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

    public double getMouthAngle() {
        return this.mouthAngle.get();
    }

    public void setMouthAngle(double angle) {
        this.mouthAngle.set(angle);
    }

    public DoubleProperty mouthAngleProperty() {
        return mouthAngle;
    }
}

