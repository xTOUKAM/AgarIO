package iut.gon.agario.model.specialpellets;

import iut.gon.agario.model.Pellet;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public abstract class SpecialPellet extends Pellet {

    public Player player;

    public SpecialPellet(double startX, double startY, double startRadius, Color color, Player player) {
        super(startX, startY, startRadius, color);
        this.player = player;
    }

    public abstract void applyEffect();
}
