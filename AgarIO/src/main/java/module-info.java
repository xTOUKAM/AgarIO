module iut.gon.agario {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;


    opens iut.gon.agario to javafx.fxml;
    exports iut.gon.agario;
}