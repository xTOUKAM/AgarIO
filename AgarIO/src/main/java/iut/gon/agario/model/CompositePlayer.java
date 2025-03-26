package iut.gon.agario.model;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class CompositePlayer implements Entity {
    private final List<Player> players = new ArrayList<>();
    private final GameWorld gameWorld;
    private double x;
    private double y;
    private static final long MINIMUM_SPLIT = 40;

    // Constructeur
    public CompositePlayer(Player initialPlayer, GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        players.add(initialPlayer);
        this.x = calculateCompositeX();
        this.y = calculateCompositeY();
    }

    // Méthode pour effectuer un split des joueurs du composite
    public void split(double cursorX, double cursorY) {
        List<Player> newCells = new ArrayList<>();

        for (Player player : players) {
            if (player.getMass() >= MINIMUM_SPLIT) {
                Player newCell = player.split(cursorX, cursorY);
                if (newCell != null) {
                    newCells.add(newCell);
                    gameWorld.addEntity(newCell);
                }
            }
        }
        players.addAll(newCells);
    }

    // Méthode de déplacement de toutes les cellules du composite player
    public void move(double cursorX, double cursorY) {
        boolean moving = Math.abs(cursorX - this.x) > 1 || Math.abs(cursorY - this.y) > 1;

        for (Player player : players) {
            gameWorld.move(cursorX, cursorY, player);
        }

        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player p1 = players.get(i);
                Player p2 = players.get(j);
                double dx = p2.getX() - p1.getX();
                double dy = p2.getY() - p1.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double minDistance = p1.getRepresentation().getRadius() + p2.getRepresentation().getRadius();

                if (distance < minDistance) {
                    double overlap = minDistance - distance;
                    double adjustX = (dx / distance) * overlap * 0.5;
                    double adjustY = (dy / distance) * overlap * 0.5;

                    p1.setX(p1.getX() - adjustX);
                    p1.setY(p1.getY() - adjustY);
                    p2.setX(p2.getX() + adjustX);
                    p2.setY(p2.getY() + adjustY);
                }
            }
        }
    }


    // Calculer la masse totale de toutes les cellules
    public double getTotalMass() {
        return players.stream().mapToDouble(Player::getMass).sum();
    }

    // Récupérer la couleur du composite player
    public Color getColor() {
        return players.get(0).getColor();  // On suppose que toutes les cellules partagent la même couleur
    }

    // Méthode pour calculer la position X moyenne du composite player
    private double calculateCompositeX() {
        double sumX = 0;
        for (Player player : players) {
            sumX += player.getX();
        }
        return sumX / players.size();
    }

    // Méthode pour calculer la position Y moyenne du composite player
    private double calculateCompositeY() {
        double sumY = 0;
        for (Player player : players) {
            sumY += player.getY();
        }
        return sumY / players.size();
    }

    // Récupérer la position X du composite player
    @Override
    public double getX() {
        return x;
    }

    // Récupérer la position Y du composite player
    @Override
    public double getY() {
        return y;
    }

    // Récupérer la masse totale du composite player
    @Override
    public double getMass() {
        return getTotalMass();
    }

    // Récupérer la largeur d'une cellule du composite player (toutes les cellules ont la même taille)
    @Override
    public double getWidth() {
        return players.get(0).getWidth();
    }

    // Récupérer la hauteur d'une cellule du composite player (toutes les cellules ont la même taille)
    @Override
    public double getHeight() {
        return players.get(0).getHeight();
    }

    // Récupérer l'ID du composite player (celui de la première cellule)
    @Override
    public int getId() {
        return players.get(0).getId();
    }

    // Calculer le rayon du composite player basé sur la masse totale
    @Override
    public double calculateRadius(double mass) {
        return players.get(0).calculateRadius(mass);
    }

    // Ajouter un joueur au composite
    public void addPlayer(Player player) {
        players.add(player);
    }

    // Récupérer la liste des joueurs du composite
    public List<Player> getPlayers() {
        return players;
    }

    // Mettre à jour la position et la masse du composite player
    public void update() {
        // Déplacer chaque joueur du composite
        for (Player player : players) {
            gameWorld.move(player.getX(), player.getY(), player);
        }
        // Mettre à jour la position du composite player (moyenne des positions des cellules)
        this.x = calculateCompositeX();
        this.y = calculateCompositeY();
    }

    // Méthode pour gérer l'absorption des autres joueurs par ce composite player
    public void absorb(Entity other) {
        if (other instanceof Player otherPlayer) {
            // Si l'absorption est possible, fusionner les masses des joueurs
            if (canAbsorb(otherPlayer)) {
                for (Player player : players) {
                    player.setMass(player.getMass() + otherPlayer.getMass());
                }
                gameWorld.removeEntity(otherPlayer);
            }
        }
    }

    // Vérifier si ce composite player peut absorber un autre joueur
    public boolean canAbsorb(Player otherPlayer) {
        // Absorber un autre joueur si la masse du composite player est suffisante
        return this.getMass() > otherPlayer.getMass() * 1.33;
    }
}
