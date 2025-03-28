package iut.gon.renderer;

import iut.gon.agario.model.Entity;
import iut.gon.agario.model.Player;
import iut.gon.agario.model.Pellet;
import iut.gon.serveur.GameServer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameRenderer {
    private final Canvas canvas;
    private List<Player> players; // Tous les joueurs visibles
    private Player player;        // Joueur principal
    private CopyOnWriteArrayList<Pellet> pellets; // Toutes les pastilles visibles
    private double cameraX, cameraY, cameraZoom;

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.players = new ArrayList<>();
        this.pellets = new CopyOnWriteArrayList<>();
        this.cameraZoom = 1.0; // Par défaut
    }

    public void decodeJSON(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);

        // Caméra centrée sur le joueur
        JSONObject cameraObject = jsonObject.getJSONObject("camera");
        cameraX = cameraObject.getDouble("starX");
        cameraY = cameraObject.getDouble("starY");
        cameraZoom = 100 / Math.sqrt(player != null ? player.getMass() : 1); // Zoom basé sur la masse du joueur

        // Décodage des entités (joueurs/pastilles)
        decodeEntities(jsonObject.getJSONArray("entities"));
    }

    private void decodeEntities(JSONArray entityArray) {
        pellets.clear();
        players.clear();
        for (int i = 0; i < entityArray.length(); i++) {
            JSONObject entityObject = entityArray.getJSONObject(i);
            if (entityObject.getBoolean("isPlayer")) {
                Player p = new Player(
                        entityObject.getDouble("x"),
                        entityObject.getDouble("y"),
                        entityObject.getDouble("mass"),
                        Color.web(entityObject.getString("color")),
                        entityObject.getInt("id")
                );
                players.add(p);
                if (player == null || p.getId() == player.getId()) {
                    player = p; // Définit le joueur principal
                }
            } else {
                pellets.add(new Pellet(
                        entityObject.getDouble("x"),
                        entityObject.getDouble("y"),
                        entityObject.getInt("radius"),
                        Color.web(entityObject.getString("color"))
                ));
            }
        }
    }

    public void update() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        renderGameWorld(gc);
        renderPlayers(gc);
        renderPellets(gc);
        renderMiniMap(gc);
        renderScorePanel(gc);
    }

    private void renderGameWorld(GraphicsContext gc) {
        // Couleur de fond
        gc.setFill(Color.BEIGE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Paramètres de la grille
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(0.3);
        double gridSpacing = 50 * cameraZoom;

        // Calcul des coordonnées de départ en fonction de la caméra
        double halfCanvasWidth = canvas.getWidth() / 2;
        double halfCanvasHeight = canvas.getHeight() / 2;
        double startX = -cameraX * cameraZoom % gridSpacing + halfCanvasWidth % gridSpacing;
        double startY = -cameraY * cameraZoom % gridSpacing + halfCanvasHeight % gridSpacing;

        // Dessiner les lignes verticales
        for (double x = startX; x < canvas.getWidth(); x += gridSpacing) {
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }

        // Dessiner les lignes horizontales
        for (double y = startY; y < canvas.getHeight(); y += gridSpacing) {
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }
    }

    // GameRenderer
    private void renderPlayers(GraphicsContext gc) {
        for (Player p : players) {
            // Calcul des coordonnées ajustées selon la caméra
            double adjustedX = (p.getX() - cameraX) * cameraZoom + canvas.getWidth() / 2;
            double adjustedY = (p.getY() - cameraY) * cameraZoom + canvas.getHeight() / 2;
            double size = Math.sqrt(p.getMass()) * cameraZoom;

            // Vérifiez si le joueur dépasse les limites du terrain
            double terrainWidth = GameServer.WIDTH_MAP; // Largeur du terrain définie dans GameEngine
            double terrainHeight = GameServer.HEIGHT_MAP; // Hauteur du terrain définie dans GameEngine
            if (p.getX() < 0 || p.getX() > terrainWidth || p.getY() < 0 || p.getY() > terrainHeight) {
                continue; // Ignorer le rendu pour les joueurs hors limites
            }

            // Dessiner le joueur
            gc.setFill(p.getColor());
            gc.fillOval(adjustedX, adjustedY, size, size);

            // Afficher le pseudo au centre de la masse
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Arial", Math.min(12 * cameraZoom, 20)));
            String playerName = "Player " + p.getId();

            double textX = adjustedX + size / 2 - gc.getFont().getSize() * playerName.length() / 4;
            double textY = adjustedY + size / 2 + gc.getFont().getSize() / 2;
            gc.fillText(playerName, textX, textY);
        }
    }

    private void renderPellets(GraphicsContext gc) {
        for (Pellet pellet : pellets) {
            // Calcul des coordonnées ajustées selon la caméra
            double adjustedX = (pellet.getX() - cameraX) * cameraZoom + canvas.getWidth() / 2;
            double adjustedY = (pellet.getY() - cameraY) * cameraZoom + canvas.getHeight() / 2;
            double size = pellet.getRadius() * 2 * cameraZoom; // Taille basée sur le rayon et le zoom

            // Vérifiez si le pellet dépasse les limites du terrain
            double terrainWidth = GameServer.WIDTH_MAP;
            double terrainHeight = GameServer.HEIGHT_MAP;
            if (pellet.getX() < 0 || pellet.getX() > terrainWidth || pellet.getY() < 0 || pellet.getY() > terrainHeight) {
                continue; // Ignorer les pellets hors limites
            }

            // Vérifier si le pellet est visible sur le canvas
            if (adjustedX < -size || adjustedX > canvas.getWidth() + size || adjustedY < -size || adjustedY > canvas.getHeight() + size) {
                continue; // Ignorer les pellets hors du champ de vision
            }

            // Dessiner le pellet
            gc.setFill(pellet.getColor());
            gc.fillOval(pellet.getX() - cameraX / 2, pellet.getY() - cameraY / 2, size, size);
        }
    }

    private void renderMiniMap(GraphicsContext gc) {
        double miniMapSize = 150; // Taille de la mini-map
        double margin = 20; // Marge autour de la mini-map
        double xStart = canvas.getWidth() - miniMapSize - margin; // Position X de la mini-map
        double yStart = canvas.getHeight() - miniMapSize - margin; // Position Y de la mini-map

        // Fond sombre avec coins arrondis
        gc.setFill(Color.rgb(20, 20, 20, 0.95));
        gc.fillRoundRect(xStart, yStart, miniMapSize, miniMapSize, 20, 20);

        // Dimensions totales du terrain
        double terrainWidth = GameServer.WIDTH_MAP;
        double terrainHeight = GameServer.HEIGHT_MAP;

        // Échelle pour convertir les positions globales en coordonnées sur la mini-map
        double scaleX = miniMapSize / terrainWidth;
        double scaleY = miniMapSize / terrainHeight;

        // Ajout des joueurs dans la mini-map
        for (Player p : players) {
            double scaledX = xStart + (p.getX() * scaleX);
            double scaledY = yStart + (p.getY() * scaleY);

            if (p.equals(player)) {
                gc.setFill(Color.YELLOW); // Couleur spécifique pour le joueur principal
                gc.fillOval(scaledX - 4, scaledY - 4, 8, 8); // Taille plus grande
            } else {
                gc.setFill(p.getColor()); // Couleur des autres joueurs
                gc.fillOval(scaledX - 2, scaledY - 2, 4, 4); // Taille standard
            }
        }

        // Ajout des pellets dans la mini-map
        for (Pellet pellet : pellets) {
            double scaledX = xStart + (pellet.getX() * scaleX);
            double scaledY = yStart + (pellet.getY() * scaleY);

            gc.setFill(pellet.getColor());
            gc.fillOval(scaledX - 1, scaledY - 1, 2, 2); // Taille réduite pour les pellets
        }

        // Titre de la mini-map
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 12));
        gc.fillText("Mini-Map", xStart + 10, yStart + 15);
    }

    private void renderScorePanel(GraphicsContext gc) {
        double panelWidth = 200;
        double panelHeight = 300;
        double xStart = canvas.getWidth() - panelWidth - 20;
        double yStart = 20;

        // Fond avec coins arrondis et style moderne
        gc.setFill(Color.rgb(240, 240, 240));
        gc.fillRoundRect(xStart, yStart, panelWidth, panelHeight, 15, 15);

        // Bordure discrète
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.strokeRoundRect(xStart, yStart, panelWidth, panelHeight, 15, 15);


        // Titre du tableau des scores
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Verdana", 18));
        gc.fillText("Scoreboard:", xStart + 15, yStart + 30);

        // Trier les joueurs par masse (top 10)
        players.sort(Comparator.comparingDouble(Player::getMass).reversed());
        List<Player> topPlayers = players.subList(0, Math.min(10, players.size()));

        // Afficher chaque joueur dans le tableau
        double textY = yStart + 50;
        for (Player p : topPlayers) {
            gc.setFill(Color.DARKGRAY);
            gc.setFont(new Font("Arial", 14));
            String scoreText = "Player " + p.getId() + ": " + (int) p.getMass();
            gc.fillText(scoreText, xStart + 15, textY);
            textY += 20;
        }
    }
}