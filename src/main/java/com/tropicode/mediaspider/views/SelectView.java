package com.tropicode.mediaspider.views;

import com.gluonhq.particle.annotation.ParticleView;
import com.gluonhq.particle.view.FXMLView;
import com.tropicode.mediaspider.controllers.JobController;
import com.tropicode.mediaspider.controllers.SelectController;

@ParticleView(name = "select", isDefault = false)
public class SelectView extends FXMLView {



    public SelectView() {
        super(SelectView.class.getResource("select.fxml"));
    }


    @Override
    public void start() {
        ((SelectController) getController()).postInit();
    }


    @Override
    public void stop() {
        ((SelectController) getController()).dispose();
    }

}