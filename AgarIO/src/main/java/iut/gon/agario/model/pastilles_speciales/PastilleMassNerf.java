package iut.gon.agario.model.pastilles_speciales;

import iut.gon.agario.model.Pastille;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public class PastilleMassNerf extends PastilleSpeciale {


    public PastilleMassNerf(double startX, double startY, double startRadius, Color color, Player player) {
        super(startX, startY, startRadius, color, player);
    }

    @Override
    public void applyEffect() {
        player.setMass(player.getMass()*0.66);
    }
}
