package iut.gon.agario.model;

public interface  Entity {

     int getId();
     double getX();

     double getY();

     double getMass();

     double getWidth();

     double getHeight();

     double calculateRadius();
}