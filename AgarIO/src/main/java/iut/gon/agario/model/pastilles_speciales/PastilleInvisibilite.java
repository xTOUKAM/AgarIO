package iut.gon.agario.model.pastilles_speciales;

import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public class PastilleInvisibilite extends PastilleSpeciale{

    public PastilleInvisibilite(double startX, double startY, double startRadius, Color color, Player player) {
        super(startX, startY, startRadius, color, player);
    }

    @Override
    public void applyEffect() {
        player.getRepresentation().opacityProperty().set(0.33);
    }
}
