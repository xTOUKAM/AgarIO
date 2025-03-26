package iut.gon.agario.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LauncherController {
    @FXML
    public Button btn_quit;
    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) btn_quit.getScene().getWindow();
        stage.close();
    }

}
