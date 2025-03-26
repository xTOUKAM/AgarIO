package iut.gon.agario.model;

import javafx.scene.shape.Circle;

public interface  Entity {

     int getId();
     double getX();

     double getY();

     double getMass();

     double getWidth();

     double getHeight();

     double calculateRadius(double mass);

     Circle getRepresentation();
}