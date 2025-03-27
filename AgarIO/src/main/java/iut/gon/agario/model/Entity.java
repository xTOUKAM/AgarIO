package iut.gon.agario.model;

import org.json.JSONObject;

public interface  Entity {

     int getId();
     double getX();

     double getY();

     double getMass();

     double getWidth();

     double getHeight();

     double calculateRadius();

     JSONObject getJSON();
}