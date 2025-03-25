package iut.gon.agario.model;

public abstract class Entity {

    abstract int getId();
    abstract double getX();

    abstract double getY();

    abstract double getMass();

    abstract double getWidth();

    abstract double getHeight();

    abstract void destruct();

    abstract double calculateRadius(double mass);
}
