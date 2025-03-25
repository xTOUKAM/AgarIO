package iut.gon.agario.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Leaderboard {

    private GameWorld game;
    private List<Player> sortedPlayers;

    public Leaderboard(GameWorld game) {
        this.game = game;
        sortedPlayers = new ArrayList<Player>();
    }

    public void topPlayers(int topN) {
        sortedPlayers = game.getPlayers().stream()
                .sorted((p1, p2) -> Double.compare(p2.getMass(), p1.getMass()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    public List<Player> getLeaderboard(int topN) {
        if (sortedPlayers.isEmpty()){
            topPlayers(topN);
        }
        return sortedPlayers;
    }
}
