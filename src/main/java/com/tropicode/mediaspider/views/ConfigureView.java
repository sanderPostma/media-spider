package com.tropicode.mediaspider.views;

import com.gluonhq.particle.annotation.ParticleView;
import com.gluonhq.particle.view.FXMLView;
import com.tropicode.mediaspider.controllers.ConfigureController;

@ParticleView(name = "configure", isDefault = true)
public class ConfigureView extends FXMLView {
    
    public ConfigureView() {
        super(ConfigureView.class.getResource("configure.fxml"));
    }
    
    @Override
    public void start() {
        ((ConfigureController) getController()).postInit();
    }
    
    @Override
    public void stop() {
        ((ConfigureController) getController()).dispose();
    }
    
}
