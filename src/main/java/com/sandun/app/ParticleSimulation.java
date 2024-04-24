package com.sandun.app;

import com.kieferlam.javafxblur.Blur;
import com.sandun.app.model.ParticleEffect;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ParticleSimulation extends Application {


    @Override
    public void start(Stage stage) throws Exception {
//        Blur.loadBlurLibrary();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Simulation.fxml"));
        Scene scene = new Scene(loader.load(), 1400, 800);

        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Particle Simulation");
        stage.show();

//        Blur.applyBlur(stage, Blur.BLUR_BEHIND);
        Canvas canvas = (Canvas) scene.lookup("#myCanvas");
        GraphicsContext gc = canvas.getGraphicsContext2D();

        ParticleEffect effect = new ParticleEffect(1150, 800, gc);
        ((ParticleController) loader.getController()).setEffect(effect);
        effect.initializeParticles();
        effect.render();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                try {
                    if (effect.isRunning()) {
                        synchronized (effect) {
                            effect.update();
                            effect.render();
                        }
                        Thread.sleep((long) (effect.getSpeed()));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        System.setProperty("prism.order", "d3d");
        launch(args);
    }


}
