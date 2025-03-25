module iut.gon.agario {
    requires javafx.controls;
    requires javafx.fxml;

    opens iut.gon.agario.main to javafx.graphics;
    exports iut.gon.agario.main;
}