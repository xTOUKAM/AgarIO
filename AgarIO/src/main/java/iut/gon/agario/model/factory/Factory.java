package iut.gon.agario.model.factory;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;

import java.util.HashMap;

public abstract class Factory {

    public Double maxX;
    public Double maxY;

    public Factory(Double maxX, Double maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public abstract Entity factory();
}
