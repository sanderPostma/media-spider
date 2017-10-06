package com.tropicode.mediaspider.jobs;

import com.tropicode.mediaspider.controllers.UIMessageChannel;

public class ScanJob extends Thread {

    private static final String ANALYSE_JOB_NAME = "Scanning files";
    private final UIMessageChannel uiMessageChannel;
    private final String searchPaths;
    private final String pictureFiletypes;
    private final String videoFileTypes;
    private final String targetFolder;
    private final boolean separateVideo;


    public ScanJob(UIMessageChannel uiMessageChannel, String searchPaths, String pictureFileTypes, String videoFileTypes, String targetFolder, boolean separateVideo) {
        this.uiMessageChannel = uiMessageChannel;
        this.searchPaths = searchPaths;
        this.pictureFiletypes = pictureFileTypes;
        this.videoFileTypes = videoFileTypes;
        this.targetFolder = targetFolder;
        this.separateVideo = separateVideo;
    }


    @Override
    public void run() {
        uiMessageChannel.jobStart(ANALYSE_JOB_NAME);


    }
}
