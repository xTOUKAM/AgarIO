package iut.gon.agario.model.fabrique;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.GameWorld;

public abstract class Fabrique {

    private GameWorld gameWorld;

    public Fabrique(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public abstract Entity fabrique();
}
