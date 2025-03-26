package iut.gon.agario.model.pastilles_speciales;

import iut.gon.agario.model.Cell;
import iut.gon.agario.model.Player;
import javafx.scene.paint.Color;

public class PastilleMassBoost extends PastilleSpeciale{


    public PastilleMassBoost(double startX, double startY, double startRadius, Color color, Player player) {
        super(startX, startY, startRadius, color, player);
    }

    @Override
    public void applyEffect() {
        for(Cell cell : player.getCells()) {
            cell.setMass(player.getMass() * 1.33);
        }
    }
}
