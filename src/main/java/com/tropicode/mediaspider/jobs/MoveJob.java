package com.tropicode.mediaspider.jobs;

import com.tropicode.mediaspider.controllers.UIMessageChannel;
import com.tropicode.mediaspider.dto.MediaFile;
import com.tropicode.mediaspider.dto.MediaPath;
import com.tropicode.mediaspider.dto.MediaType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MoveJob extends Thread {
    private static final String MOVE_JOB_NAME = "Moving files";
    private static final Map<MediaType, String> TARGET_DIRS = new HashMap<>();

    private final UIMessageChannel uiMessageChannel;
    private final FileRepository fileRepository;
    private final String configuredTargetDirectory;
    private final boolean separateVideos;
    private final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");


    static {
        for (MediaType mediaType : MediaType.values()) {
            switch (mediaType) {
                case OTHER:
                    TARGET_DIRS.put(mediaType, "");
                    break;
                case PICTURE:
                    TARGET_DIRS.put(mediaType, "pictures");
                    break;
                case VIDEO:
                    TARGET_DIRS.put(mediaType, "videos");
                    break;
            }
        }
    }

    public MoveJob(UIMessageChannel uiMessageChannel, FileRepository fileRepository, String targetDirectory, boolean separateVideos) {
        this.uiMessageChannel = uiMessageChannel;
        this.fileRepository = fileRepository;
        this.configuredTargetDirectory = targetDirectory;
        this.separateVideos = separateVideos;
    }


    @Override
    public void run() {
        uiMessageChannel.jobStarted(MOVE_JOB_NAME);
        fileRepository.getFileMap().entrySet().forEach(mediaFileEntry -> {
            MediaFile mediaFile = mediaFileEntry.getValue();
            mediaFile.getMediaPaths().entrySet().forEach(mediaPathPathEntry -> {
                Path sourcePath = mediaPathPathEntry.getKey();
                MediaPath mediaPath = mediaPathPathEntry.getValue();
                moveMediaFile(mediaFile, mediaPath, sourcePath);
            });
        });
    }


    private void moveMediaFile(MediaFile mediaFile, MediaPath mediaPath, Path sourcePath) {
        if (mediaPath.isSelected()) {
            if (sourcePath == null) {
                uiMessageChannel.logMessage(String.format("ERROR: %s was registered under the same or a different name in path %s but could no longer be found.",
                        mediaFile, mediaPath.getPath()));
            } else if (mediaFile.getMovedTo() != null) {
                if (Files.exists(mediaFile.getMovedTo())) {
                    deleteFile(mediaFile, sourcePath);
                } else {
                    uiMessageChannel.logMessage(String.format("WARNING: Duplicate %s should already be in the target folder, but could not be found. Moving again...",
                            mediaFile));
                    moveFile(mediaFile, sourcePath);
                }
            } else {
                moveFile(mediaFile, sourcePath);
            }
        } else {
            uiMessageChannel.logMessage(String.format("Skipping %s as directory %s was deselected.", mediaFile, mediaPath.getPath()));
        }
    }


    private void moveFile(MediaFile mediaFile, Path sourcePath) {
        try {
            Path targetPath = Paths.get(configuredTargetDirectory,
                    yearFormatter.format(LocalDateTime.ofInstant(mediaFile.getEarliestTime().toInstant(), ZoneId.systemDefault())));
            if (sourcePath.getParent() != null) {
                if (sourcePath.getParent().equals(targetPath)) {
                    uiMessageChannel.logMessage(String.format("Source & target path are the same, skipping %s.", sourcePath));
                    return;
                }
                Path parentDir = sourcePath.getParent().getFileName();
                targetPath = Paths.get(targetPath.toString(), parentDir.toString());
            }
            if (separateVideos) {
                targetPath = Paths.get(targetPath.toString(), TARGET_DIRS.get(mediaFile.getMediaType()));
            }

            targetPath = checkExistingFile(mediaFile, sourcePath, targetPath);
            if (targetPath == null) {
                deleteFile(mediaFile, sourcePath);
                return;
            }
            Path move = Files.move(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
            Files.setLastModifiedTime(move, mediaFile.getEarliestTime());
            mediaFile.setMovedTo(move);
            uiMessageChannel.logMessage(String.format("Moved %s %s to %s", mediaFile.getMediaType(), sourcePath, targetPath));
        } catch (Exception e) {
            uiMessageChannel.logMessage(String.format("ERROR: Could not move %s %s to %s", mediaFile.getMediaType(), sourcePath, configuredTargetDirectory));
            uiMessageChannel.logMessage("CAUSE: " + e.getMessage());
        }
    }

    private Path checkExistingFile(MediaFile mediaFile, Path sourcePath, Path targetPath) {
        Path targetFile = Paths.get(targetPath.toString(), sourcePath.getFileName().toString());
        if (Files.exists(targetFile)) {
            if (targetFile.toFile().length() != sourcePath.toFile().length()) {
                while (true) {
                    Path newFile = Paths.get(targetFile.toString().replaceFirst("\\.", String.valueOf('_' + new Random().nextInt() + '.')));
                    if (!Files.exists(newFile)) {
                        targetPath = newFile;
                        break;
                    }
                }
            } else {
                uiMessageChannel.logMessage(String.format("%s %s already exists in %s", mediaFile.getMediaType(), sourcePath, targetPath));
                return null;
            }
        }
        return targetPath;
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