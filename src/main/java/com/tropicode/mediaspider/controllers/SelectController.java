package com.tropicode.mediaspider.controllers;

import com.gluonhq.particle.application.ParticleApplication;
import com.gluonhq.particle.view.ViewManager;
import com.tropicode.mediaspider.dto.MediaPath;
import com.tropicode.mediaspider.jobs.FileRepository;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionMap;
import org.controlsfx.control.action.ActionProxy;

import javax.inject.Inject;

public class SelectController {

    @Inject
    ParticleApplication app;

    @Inject
    private ViewManager viewManager;

    @FXML
    private TreeView<MediaPath> treeView;

    private Action proceedAction;
    private Action actionRestartJob;
    private Action actionCancelJob;


    public void initialize() {
        ActionMap.register(this);
        proceedAction = ActionMap.action("proceed");
        actionRestartJob = ActionMap.action("restartJob");
        actionCancelJob = ActionMap.action("cancelJob");
    }


    public void postInit() {
        treeView.setShowRoot(false);
        app.getParticle().getToolBarActions().add(0, actionCancelJob);
        app.getParticle().getToolBarActions().add(0, actionRestartJob);
        app.getParticle().getToolBarActions().add(0, proceedAction);
    }


    public void dispose() {
        app.getParticle().getToolBarActions().remove(proceedAction);
        app.getParticle().getToolBarActions().remove(actionRestartJob);
        app.getParticle().getToolBarActions().remove(actionCancelJob);
    }


    @ActionProxy(text = "Proceed move")
    private void proceed() {
    }


    @ActionProxy(text = "Restart job")
    private void restartJob() {
    }


    @ActionProxy(text = "Cancel job")
    private void cancelJob() {
        viewManager.switchView("configure");
    }


    public void go(FileRepository fileRepository) {
        TreeItem<MediaPath> root = new TreeItem();
        treeView.setRoot(root);
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
        fileRepository.getRootPaths().forEach(mediaPath -> {
            if (mediaPath != null) {
                root.getChildren().add(mediaPath.toTreeItem());
            }
        });
    }
}
