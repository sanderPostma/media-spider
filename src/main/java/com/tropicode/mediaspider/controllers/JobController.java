package com.tropicode.mediaspider.controllers;

import com.gluonhq.particle.application.ParticleApplication;
import com.gluonhq.particle.view.ViewManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionMap;
import org.controlsfx.control.action.ActionProxy;

import javax.inject.Inject;

public class JobController implements UIMessageChannel {

    @Inject
    ParticleApplication app;

    @Inject
    private ViewManager viewManager;

    @FXML
    private javafx.scene.control.Label labelJobDesc;

    @FXML
    private TextArea textLog;

    private Action actionRestartJob;
    private Action actionPauseJob;
    private Action actionResumeJob;
    private Action actionCancelJob;


    public void initialize() {
        ActionMap.register(this);
        actionRestartJob = ActionMap.action("restartJob");
        actionRestartJob.disabledProperty().setValue(true);
        actionPauseJob = ActionMap.action("pauseJob");
        actionResumeJob = ActionMap.action("resumeJob");
        actionResumeJob.disabledProperty().setValue(true);
        actionCancelJob = ActionMap.action("cancelJob");
    }


    public void postInit() {
        app.getParticle().getToolBarActions().add(0, actionCancelJob);
        app.getParticle().getToolBarActions().add(0, actionRestartJob);
        app.getParticle().getToolBarActions().add(0, actionResumeJob);
        app.getParticle().getToolBarActions().add(0, actionPauseJob);
    }


    public void dispose() {
        app.getParticle().getToolBarActions().remove(actionRestartJob);
    }


    @ActionProxy(text = "Restart job")
    private void restartJob() {
    }


    @ActionProxy(text = "Pause job")
    private void pauseJob() {
        actionPauseJob.disabledProperty().setValue(true);
        actionResumeJob.disabledProperty().setValue(false);
    }


    @ActionProxy(text = "Resume job")
    private void resumeJob() {
        actionPauseJob.disabledProperty().setValue(false);
        actionResumeJob.disabledProperty().setValue(true);
    }


    @ActionProxy(text = "Cancel job")
    private void cancelJob() {
        viewManager.switchView("configure");
    }


    @Override
    public void jobStart(String jobName) {
        actionPauseJob.disabledProperty().setValue(false);
        actionResumeJob.disabledProperty().setValue(true);
        actionCancelJob.disabledProperty().setValue(false);
        actionRestartJob.disabledProperty().setValue(false);
        Platform.runLater(() -> {
            labelJobDesc.setText(jobName);
            textLog.setText("");
        });
    }


    @Override
    public void logMessage(String message) {
        Platform.runLater(() -> {
            textLog.appendText("\r\n" + message);
        });
    }


    @Override
    public void jobDone(boolean successful) {
        actionPauseJob.disabledProperty().setValue(true);
        actionResumeJob.disabledProperty().setValue(true);
        actionCancelJob.disabledProperty().setValue(true);
        actionRestartJob.disabledProperty().setValue(false);
        if (successful) {
            viewManager.switchView("select");
        }
    }
}