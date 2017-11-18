package com.tropicode.mediaspider.views;

import com.gluonhq.particle.annotation.ParticleView;
import com.gluonhq.particle.view.FXMLView;
import com.tropicode.mediaspider.controllers.JobController;

@ParticleView(name = "select", isDefault = false)
public class SelectView extends FXMLView {



    public SelectView() {
        super(SelectView.class.getResource("select.fxml"));
    }


    @Override
    public void start() {
        ((JobController) getController()).postInit();
    }


    @Override
    public void stop() {
        ((JobController) getController()).dispose();
    }

}