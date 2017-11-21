package com.tropicode.mediaspider.controllers;

import com.gluonhq.particle.application.ParticleApplication;
import com.gluonhq.particle.view.ViewManager;
import com.tropicode.mediaspider.jobs.ScanJob;
import com.tropicode.mediaspider.views.SelectView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private Label labelJobDesc;

    @FXML
    private TextArea textLog;

    private Action actionRestartJob;
    private Action actionPauseJob;
    private Action actionResumeJob;
    private Action actionCancelJob;
    private ScanJob scanJob;


    public void initialize() {
        ActionMap.register(this);
        actionRestartJob = ActionMap.action("restartJob");
        actionRestartJob.disabledProperty().setValue(true);
        actionPauseJob = ActionMap.action("pauseJob");
        actionResumeJob = ActionMap.action("resumeJob");
        actionResumeJob.disabledProperty().setValue(true);
        actionCancelJob = ActionMap.action("cancelJob");
    }


    public void startScanJob(String searchPath, String pictureFilePatterns, String videoFilePatterns, String targetFolder, boolean separatePicsAndVideos) {
        scanJob = new ScanJob(this, searchPath, pictureFilePatterns, videoFilePatterns, targetFolder, separatePicsAndVideos);
        scanJob.start();
    }


    public void postInit() {
        app.getParticle().getToolBarActions().add(0, actionCancelJob);
        app.getParticle().getToolBarActions().add(0, actionRestartJob);
        app.getParticle().getToolBarActions().add(0, actionResumeJob);
        app.getParticle().getToolBarActions().add(0, actionPauseJob);
    }


    public void dispose() {
        app.getParticle().getToolBarActions().remove(actionCancelJob);
        app.getParticle().getToolBarActions().remove(actionRestartJob);
        app.getParticle().getToolBarActions().remove(actionResumeJob);
        app.getParticle().getToolBarActions().remove(actionPauseJob);
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
    public void jobStarted(String jobName) {
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
        actionCancelJob.disabledProperty().setValue(false);
        actionRestartJob.disabledProperty().setValue(false);
        if (successful) {
            Platform.runLater(() -> {
                viewManager.switchView("select");
                SelectView selectView = (SelectView) viewManager.getCurrentView();
                SelectController selectController = (SelectController) selectView.getController();
                selectController.go(scanJob.getFileRepository());
            });
        }
    }
}