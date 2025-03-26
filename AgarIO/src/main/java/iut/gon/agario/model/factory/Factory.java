package iut.gon.agario.model.factory;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;

public abstract class Factory {

    private GameWorld gameWorld;

    public Factory(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public abstract Entity factory();
}
