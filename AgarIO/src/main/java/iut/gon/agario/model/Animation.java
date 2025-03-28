package iut.gon.agario.model;

import javafx.scene.shape.Circle;

public class Animation{

    public Animation(Circle representation, Circle representationPerimettre) {

    }


    public double calculateAngle(double mass) {
        double angle = Math.PI * Math.pow(mass, 2);
        return angle - (angle / 4);
    }
}
