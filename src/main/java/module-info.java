module com.sandun.app.javafx_particlesimulation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafxblur;


    opens com.sandun.app to javafx.fxml;
    exports com.sandun.app;
}