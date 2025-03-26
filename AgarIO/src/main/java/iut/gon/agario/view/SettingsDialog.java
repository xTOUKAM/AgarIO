package iut.gon.agario.view;

import iut.gon.agario.Config;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Optional;

public class SettingsDialog extends Dialog<SettingsDialog.SettingsResult> {

    public SettingsDialog() {
        setTitle("Settings");
        setHeaderText("Change Values");

        // Set the button types
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create input fields
        TextField absorptionRatio = new TextField(String.valueOf(Config.ABSORPTION_RATIO));
        TextField mergeOverlap = new TextField(String.valueOf(Config.MERGE_OVERLAP));
        TextField decayFactor = new TextField(String.valueOf(Config.DECAY_FACTOR));
        TextField speedDecayDuration = new TextField(String.valueOf(Config.SPEED_DECAY_DURATION));
        TextField controlRadius = new TextField(String.valueOf(Config.CONTROL_RADIUS));
        TextField minSpeed = new TextField(String.valueOf(Config.MIN_SPEED));
        TextField pelletMass = new TextField(String.valueOf(Config.PELLET_MASS));
        TextField initialMaxSpeed = new TextField(String.valueOf(Config.INITIAL_MAX_SPEED));
        TextField coefficientAttenuation = new TextField(String.valueOf(Config.COEFFICIENT_ATTENUATION));
        TextField minimumSplit = new TextField(String.valueOf(Config.MINIMUM_SPLIT));
        TextField massBuffMultiplier = new TextField(String.valueOf(Config.MASS_BUFF_MULTIPLIER));
        TextField massNerfMultiplier = new TextField(String.valueOf(Config.MASS_NERF_MULTIPLIER));
        TextField speedBuffMultiplier = new TextField(String.valueOf(Config.SPEED_BUFF_MULTIPLIER));
        TextField speedNerfMultiplier = new TextField(String.valueOf(Config.SPEED_NERF_MULTIPLIER));
        TextField baseZoom = new TextField(String.valueOf(Config.BASE_ZOOM));

        // Create a layout for the fields

        VBox panel1 = new VBox(
                new Label("Absorption ratio : "), absorptionRatio,
                new Label("Merge overlap :"), mergeOverlap,
                new Label("Decay factor : "), decayFactor,
                new Label("Speed decay duration :"), speedDecayDuration,
                new Label("Control radius : "), controlRadius,
                new Label("Minimum speed :"), minSpeed,
                new Label("Default pellet mass : "), pelletMass,
                new Label("Maximum natural speed :"), initialMaxSpeed
        );
        VBox panel2 = new VBox(
                new Label("Attenuation coefficient : "), coefficientAttenuation,
                new Label("Minimum mass for split :"), minimumSplit,
                new Label("Mass buff multiplier : "), massBuffMultiplier,
                new Label("Mass nerf multiplier :"), massNerfMultiplier,
                new Label("Speed buff multiplier : "), speedBuffMultiplier,
                new Label("Speed nerf multiplier :"), speedNerfMultiplier,
                new Label("Default zoom : "), baseZoom
        );

        HBox settingsPanel = new HBox(panel1, panel2);

        // Set the content of the dialog
        getDialogPane().setContent(settingsPanel);

        // Convert the result to a SettingsResult
        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    double rAbsorptionRatio = Double.parseDouble(absorptionRatio.getText());
                    double rMergeOverlap = Double.parseDouble(mergeOverlap.getText());
                    double rDecayFactor = Double.parseDouble(decayFactor.getText());
                    long rSpeedDecayDuration = Long.parseLong(speedDecayDuration.getText());
                    long rControlRadius = Long.parseLong(controlRadius.getText());
                    double rMinSpeed = Double.parseDouble(minSpeed.getText());
                    double rPelletMass = Double.parseDouble(pelletMass.getText());
                    double rInitialMaxSpeed = Double.parseDouble(initialMaxSpeed.getText());
                    double rCoefficientAttenuation = Double.parseDouble(coefficientAttenuation.getText());
                    long rMinimumSplit = Long.parseLong(minimumSplit.getText());
                    double rMassBuffMultiplier = Double.parseDouble(massBuffMultiplier.getText());
                    double rMassNerfMultiplier = Double.parseDouble(massNerfMultiplier.getText());
                    double rSpeedBuffMultiplier = Double.parseDouble(speedBuffMultiplier.getText());
                    double rSpeedNerfMultiplier = Double.parseDouble(speedNerfMultiplier.getText());
                    double rBaseZoom = Double.parseDouble(baseZoom.getText());
                    return new SettingsResult(rAbsorptionRatio,rMergeOverlap,rDecayFactor,rSpeedDecayDuration,rControlRadius,
                            rMinSpeed,rPelletMass,rInitialMaxSpeed,rCoefficientAttenuation,rMinimumSplit, rMassBuffMultiplier,
                            rMassNerfMultiplier,rSpeedBuffMultiplier,rSpeedNerfMultiplier,rBaseZoom);
                } catch (NumberFormatException ex) {
                    // Handle invalid input
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input. Please enter valid numbers.", ButtonType.OK);
                    alert.showAndWait();
                }
            }
            return null;
        });
    }

    public static class SettingsResult {
        private double rAbsorptionRatio;
        private  double rMergeOverlap;
        private double rDecayFactor;
        private long rSpeedDecayDuration;
        private  long rControlRadius;
        private double rMinSpeed;
        private double rPelletMass;
        private  double rInitialMaxSpeed;
        private double rCoefficientAttenuation;
        private long rMinimumSplit;
        private  double rMassBuffMultiplier;
        private  double rMassNerfMultiplier;
        private  double rSpeedBuffMultiplier;
        private  double rSpeedNerfMultiplier;
        private double rBaseZoom;


        public double getrAbsorptionRatio() {
            return rAbsorptionRatio;
        }

        public SettingsResult(double rAbsorptionRatio, double rMergeOverlap, double rDecayFactor, long rSpeedDecayDuration,
                              long rControlRadius, double rMinSpeed, double rPelletMass, double rInitialMaxSpeed,
                              double rCoefficientAttenuation, long rMinimumSplit, double rMassBuffMultiplier,
                              double rMassNerfMultiplier, double rSpeedBuffMultiplier, double rSpeedNerfMultiplier,
                              double rBaseZoom) {
            this.rAbsorptionRatio = rAbsorptionRatio;
            this.rMergeOverlap = rMergeOverlap;
            this.rDecayFactor = rDecayFactor;
            this.rSpeedDecayDuration = rSpeedDecayDuration;
            this.rControlRadius = rControlRadius;
            this.rMinSpeed = rMinSpeed;
            this.rPelletMass = rPelletMass;
            this.rInitialMaxSpeed = rInitialMaxSpeed;
            this.rCoefficientAttenuation = rCoefficientAttenuation;
            this.rMinimumSplit = rMinimumSplit;
            this.rMassBuffMultiplier = rMassBuffMultiplier;
            this.rMassNerfMultiplier = rMassNerfMultiplier;
            this.rSpeedBuffMultiplier = rSpeedBuffMultiplier;
            this.rSpeedNerfMultiplier = rSpeedNerfMultiplier;
            this.rBaseZoom = rBaseZoom;

        }

        public double getrMergeOverlap() {
            return rMergeOverlap;
        }

        public double getrDecayFactor() {
            return rDecayFactor;
        }

        public long getrSpeedDecayDuration() {
            return rSpeedDecayDuration;
        }

        public long getrControlRadius() {
            return rControlRadius;
        }

        public double getrMinSpeed() {
            return rMinSpeed;
        }

        public double getrPelletMass() {
            return rPelletMass;
        }

        public double getrInitialMaxSpeed() {
            return rInitialMaxSpeed;
        }

        public double getrCoefficientAttenuation() {
            return rCoefficientAttenuation;
        }

        public long getrMinimumSplit() {
            return rMinimumSplit;
        }

        public double getrMassBuffMultiplier() {
            return rMassBuffMultiplier;
        }

        public double getrMassNerfMultiplier() {
            return rMassNerfMultiplier;
        }

        public double getrSpeedBuffMultiplier() {
            return rSpeedBuffMultiplier;
        }

        public double getrSpeedNerfMultiplier() {
            return rSpeedNerfMultiplier;
        }

        public double getrBaseZoom() {
            return rBaseZoom;
        }
    }
}
