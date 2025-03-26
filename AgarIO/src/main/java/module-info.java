module iut.gon.agario {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;

    opens iut.gon.agario.main to javafx.graphics;
    exports iut.gon.agario.main;
}