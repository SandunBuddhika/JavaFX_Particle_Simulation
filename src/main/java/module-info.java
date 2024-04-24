module com.sandun.app.javafx_particlesimulation {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sandun.app to javafx.fxml;
    exports com.sandun.app;
}