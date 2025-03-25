package iut.gon.agario.model.pastilles_speciales;

import iut.gon.agario.model.Pastille;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public abstract class PastilleSpeciale extends Pastille {

    public Player player;

    public PastilleSpeciale(double startX, double startY, double startRadius, Color color, Player player) {
        super(startX, startY, startRadius, color);
        this.player = player;
    }

    public abstract void applyEffect();
}
