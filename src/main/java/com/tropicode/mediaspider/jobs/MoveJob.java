package com.tropicode.mediaspider.jobs;

import com.tropicode.mediaspider.controllers.UIMessageChannel;
import com.tropicode.mediaspider.dto.MediaFile;
import com.tropicode.mediaspider.dto.MediaPath;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MoveJob extends Thread {
    private static final String MOVE_JOB_NAME = "Moving files";
    private final UIMessageChannel uiMessageChannel;
    private final FileRepository fileRepository;
    private final String targetFolder;
    private final boolean separateVideos;


    public MoveJob(UIMessageChannel uiMessageChannel, FileRepository fileRepository, String targetFolder, boolean separateVideos) {
        this.uiMessageChannel = uiMessageChannel;
        this.fileRepository = fileRepository;
        this.targetFolder = targetFolder;
        this.separateVideos = separateVideos;
    }


    @Override
    public void run() {
        uiMessageChannel.jobStarted(MOVE_JOB_NAME);
        fileRepository.getFileMap().values().forEach(mediaFile ->
                mediaFile.getMediaPaths().entrySet().forEach(mediaPathPathEntry -> {
                    MediaPath mediaPath = mediaPathPathEntry.getKey();
                    Path sourcePath = mediaPathPathEntry.getValue();
                    moveMediaFile(mediaFile, mediaPath, sourcePath);
                }));
    }


    private void moveMediaFile(MediaFile mediaFile, MediaPath mediaPath, Path sourcePath) {
        if (mediaPath.isSelected()) {
            if (sourcePath == null) {
                uiMessageChannel.logMessage(String.format("ERROR: %s file %s was registered under the same or a different name in path %s but could no longer be found.",
                        mediaFile.getMediaType(), mediaFile.getFilePath().getFileName(), mediaPath.getPath()));
            } else if (mediaFile.getMovedTo() != null) {
                if (Files.exists(mediaFile.getMovedTo())) {
                    deleteFile(mediaFile, sourcePath);
                } else {
                    uiMessageChannel.logMessage(String.format("WARNING: Duplicate %s %s should already be in the target folder, but could not be found. Moving again...",
                            mediaFile.getMediaType(), mediaFile.getFilePath()));
                    moveFile(mediaFile, sourcePath);
                }
            } else {
                moveFile(mediaFile, sourcePath);
            }
        } else {
            uiMessageChannel.logMessage(String.format("Skipping %s as directory %s was deselected.", mediaFile.getFilePath(), mediaPath.getPath()));
        }
    }


    private void moveFile(MediaFile mediaFile, Path sourcePath) {
        try {
            //Path move = Files.move(sourcePath, Paths.get(targetFolder), StandardCopyOption.COPY_ATTRIBUTES);
            Path move = Paths.get(targetFolder, sourcePath.getFileName().toString());
            mediaFile.setMovedTo(move);
            uiMessageChannel.logMessage(String.format("Moved %s %s to %s", mediaFile.getMediaType(), sourcePath, targetFolder));
        } catch (Exception e) {
            uiMessageChannel.logMessage(String.format("ERROR: Could not move %s %s to %s", mediaFile.getMediaType(), sourcePath, targetFolder));
            uiMessageChannel.logMessage("CAUSE: " + e.getMessage());
        }
    }


    private void deleteFile(MediaFile mediaFile, Path sourcePath) {
        try {
            //Files.delete(sourcePath);
            uiMessageChannel.logMessage(String.format("Deleted %s %s", mediaFile.getMediaType(), sourcePath));
        } catch (Exception e) {
            uiMessageChannel.logMessage(String.format("ERROR: Could not delete %s %s", mediaFile.getMediaType(), sourcePath));
            uiMessageChannel.logMessage("CAUSE: " + e.getMessage());
        }
    }
}