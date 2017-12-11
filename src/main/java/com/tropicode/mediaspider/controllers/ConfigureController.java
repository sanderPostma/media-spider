package com.tropicode.mediaspider.controllers;

import com.gluonhq.particle.application.ParticleApplication;
import com.gluonhq.particle.state.StateManager;
import com.gluonhq.particle.view.ViewManager;
import com.tropicode.mediaspider.views.JobView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionMap;
import org.controlsfx.control.action.ActionProxy;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ConfigureController {

    @Inject
    ParticleApplication app;

    @Inject
    private ViewManager viewManager;

    @Inject
    private StateManager stateManager;

    @FXML
    private Button buttonBrowseSearchPath;

    @FXML
    private Button buttonBrowseTargetFolder;

    @FXML
    private TextArea textSearchPath;

    @FXML
    private TextField textPictureFileTypes;

    @FXML
    private TextField textVideoFileTypes;

    @FXML
    private TextField textTargetFolder;

    @FXML
    private CheckBox checkboxSeparate;


    private boolean first = true;


    @FXML
    private ResourceBundle resources;

    private Action actionScan;


    public void initialize() {
        ActionMap.register(this);
        actionScan = ActionMap.action("scan");
        buttonBrowseSearchPath.setOnAction(event -> selectSearchPathFolder());
        buttonBrowseTargetFolder.setOnAction(event -> selectTargetFolder());
    }


    private void selectSearchPathFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(app.getPrimaryStage());
        if (!textSearchPath.getText().contains(selectedDirectory.getAbsolutePath())) {
            StringBuilder builder = new StringBuilder(textSearchPath.getText());
            if (builder.length() > 0) {
                builder.append(';').append(' ');
            }
            builder.append(selectedDirectory.getAbsolutePath());
            textSearchPath.setText(builder.toString());
        }
    }


    private void selectTargetFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(app.getPrimaryStage());
        if (!textTargetFolder.getText().equals(selectedDirectory.getAbsolutePath())) {
            textTargetFolder.setText(selectedDirectory.getAbsolutePath());
        }
    }


    public void postInit() {
        if (first) {
            stateManager.setPersistenceMode(StateManager.PersistenceMode.USER);
            first = false;
        }
        app.getParticle().getToolBarActions().add(0, actionScan);
    }


    public void dispose() {
        app.getParticle().getToolBarActions().remove(actionScan);
    }


    @ActionProxy(text = "Scan")
    private void scan() {
        viewManager.switchView("job");
        JobView jobView = (JobView) viewManager.getCurrentView();
        JobController jobController = (JobController) jobView.getController();
        jobController.startScanJob(textSearchPath.getText(), textPictureFileTypes.getText(), textVideoFileTypes.getText());
        jobController.prepareMoveJob(textTargetFolder.getText(), checkboxSeparate.isSelected());
    }
}