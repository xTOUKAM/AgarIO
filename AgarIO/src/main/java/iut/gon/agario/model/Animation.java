package iut.gon.agario.model;

import javafx.animation.Transition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;

public class Animation extends Transition {
    private final Circle pacMan;   // Représentation de la cellule animée
    private final double originalRadius; // Le rayon initial de la cellule
    private final Color originalColor; // Couleur originale de la cellule
    private boolean isOpenMouth;  // Etat de la bouche (ouverte ou fermée)
    private DoubleProperty radiusProperty;

    public Animation(Circle pacMan) {
        this.pacMan = pacMan;
        this.originalRadius = pacMan.getRadius();  // Sauvegarder le rayon d'origine
        this.originalColor = (Color) pacMan.getFill();  // Conserver la couleur de la cellule
        this.isOpenMouth = false;
        radiusProperty = new SimpleDoubleProperty(originalRadius);

        // Définir la durée du cycle de l'animation (temps d'ouverture/fermeture)
        this.setCycleDuration(Duration.millis(200));
        this.setCycleCount(Transition.INDEFINITE); // Animation indéfinie
    }

    @Override
    protected void interpolate(double frac) {
        // Si frac est inférieur à 0.5, on ouvre la bouche (réduit le rayon)
        if (frac < 0.5) {
            pacMan.setRadius(originalRadius * (1 - frac * 2));  // Modifier le rayon sans liaison
        } else {
            pacMan.setRadius(originalRadius * (1 - (1 - frac) * 2));  // Modifier le rayon sans liaison
        }

        // Restaurer la couleur originale de la cellule à chaque cycle
        pacMan.setFill(originalColor);
    }

    public void startAnimation() {
        this.play();  // Démarrer l'animation
    }

    public void resetAnimation() {
        // Stopper l'animation avant de réinitialiser le rayon
        this.stop();

        // Réinitialiser le rayon en utilisant setRadius (sans affecter la liaison)
        pacMan.setRadius(originalRadius);

        // Réinitialiser la couleur à sa valeur d'origine
        pacMan.setFill(originalColor);
    }
}
