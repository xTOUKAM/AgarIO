package iut.gon.agario;

import iut.gon.agario.model.*;

public class GameEngine extends Thread {

    private final GameWorld gameWorld;

    public GameEngine(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Mise à jour du monde du jeu
                gameWorld.update();


                // Attente avant la prochaine mise à jour (contrôler la vitesse de mise à jour du jeu)
                Thread.sleep(33);  // 30 FPS

                // Envoie l'état du jeu à tous les clients
                //sendGameStateToServer();

            } catch (InterruptedException e) {
                System.out.println("Game engine interrupted.");
                break;
            }
        }
    }

    // Nouvelle méthode pour récupérer l'état du jeu sous forme de chaîne
    public String getGameState() {
        StringBuilder state = new StringBuilder();

        // Vous pouvez ajouter plus d'informations à l'état du jeu si nécessaire
        state.append("Game World State: ").append("\n");
        state.append("Players: ").append(gameWorld.getPlayers().size()).append("\n");

        for (Player player : gameWorld.getPlayers()) {
            state.append("Player: ").append(player.getName())
                    .append(" (ID: ").append(player.getId())
                    .append(") - Position: ").append(player.getPosition())
                    .append(" - Score: ").append(player.getScore()).append("\n");
        }

        state.append("Other game world info (e.g., obstacles, game state)..."); // Ajoutez plus de détails sur le monde du jeu
        return state.toString();
    }
}
