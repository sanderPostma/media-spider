package com.tropicode.mediaspider.views;

import com.gluonhq.particle.annotation.ParticleView;
import com.gluonhq.particle.view.FXMLView;
import com.tropicode.mediaspider.controllers.JobController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

@ParticleView(name = "job", isDefault = false)
public class JobView extends FXMLView {



    public JobView() {
        super(JobView.class.getResource("job.fxml"));
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