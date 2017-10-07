package com.tropicode.mediaspider.jobs;

import com.tropicode.mediaspider.controllers.UIMessageChannel;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.StringTokenizer;

public class ScanJob extends Thread {

    private static final String ANALYSE_JOB_NAME = "Scanning files";
    private final UIMessageChannel uiMessageChannel;
    private final String searchPaths;
    private final String pictureFiletypes;
    private final String videoFileTypes;
    private final String targetFolder;
    private final boolean separateVideo;
    private final FileRepository fileRepository = new FileRepository();


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

        StringTokenizer tokenizer = new StringTokenizer(searchPaths, ";");
        while (tokenizer.hasMoreTokens()) {
            Path path = Paths.get(tokenizer.nextToken());
            if (!Files.exists(path)) {
                uiMessageChannel.logMessage("Path " + path + " does not exist, skipping...");
            } else {
                try {
                    scanPath(path);
                } catch (IOException e) {
                    uiMessageChannel.logMessage("An error occured while scanning path " + path + ":");
                    uiMessageChannel.logMessage(e.getMessage());
                }
            }
        }
    }


    private void scanPath(Path path) throws IOException {
        Files.walkFileTree(path, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return null;
            }


            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                return fileRepository.registerFile(file, attrs);
            }


            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return null;
            }


            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return null;
            }
        });
    }
}
