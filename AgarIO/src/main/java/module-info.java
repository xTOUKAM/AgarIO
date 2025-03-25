module iut.gon.agario {
    requires javafx.controls;
    requires javafx.fxml;


    opens iut.gon.agario to javafx.fxml;
    exports iut.gon.agario;
}