package com.tropicode.mediaspider;

import com.gluonhq.particle.application.ParticleApplication;
import javafx.scene.Scene;

import static org.controlsfx.control.action.ActionMap.actions;

public class MediaSpiderApplication extends ParticleApplication {

    public MediaSpiderApplication() {
        super("Media Spider");
    }


    public static void main(String[] args) {
        MediaSpiderApplication.launch(args);
    }


    @Override
    public void postInit(Scene scene) {
        scene.getStylesheets().add(MediaSpiderApplication.class.getResource("style.css").toExternalForm());

        setTitle("Media Spider");

        //getParticle().buildMenu("File -> [exit]", "Help -> [about]");

        getParticle().getToolBarActions().addAll(actions("---", "exit"));
    }
}