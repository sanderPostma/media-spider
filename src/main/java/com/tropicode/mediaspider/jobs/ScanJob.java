package com.tropicode.mediaspider.jobs;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.tropicode.mediaspider.controllers.UIMessageChannel;
import com.tropicode.mediaspider.dto.MediaType;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanJob extends Thread {

    private static final String ANALYSE_JOB_NAME = "Scanning files";
    private final UIMessageChannel uiMessageChannel;
    private final String searchPaths;
    private Pattern pictureFiletypes;
    private Pattern videoFileTypes;
    private final FileRepository fileRepository = new FileRepository();
    private final AtomicInteger queuedRegistrationTasks = new AtomicInteger();
    private final AtomicBoolean hasErrors = new AtomicBoolean();


    public ScanJob(UIMessageChannel uiMessageChannel, String searchPaths, String pictureFileTypes, String videoFileTypes) {
        this.uiMessageChannel = uiMessageChannel;
        this.searchPaths = searchPaths;

        StringBuilder builder = new StringBuilder();
        for (String ext : pictureFileTypes.toLowerCase().split(";")) {
            if (builder.length() > 0) {
                builder.append("|");
            }
            builder.append('(').append(ext.replace("*", "\\w*").replace("?", "\\w")).append('$').append(')');
        }
        this.pictureFiletypes = Pattern.compile("(?i)" + builder.toString());
        builder = new StringBuilder();
        for (String ext : videoFileTypes.toLowerCase().split(";")) {
            if (builder.length() > 0) {
                builder.append("|");
            }
            builder.append('(').append(ext.replace("*", "\\w*").replace("?", "\\w")).append('$').append(')');
        }
        this.videoFileTypes = Pattern.compile("(?i)" + builder.toString());
    }


    @Override
    public void run() {
        uiMessageChannel.jobStarted(ANALYSE_JOB_NAME);

        StringTokenizer tokenizer = new StringTokenizer(searchPaths, ";");
        while (tokenizer.hasMoreTokens()) {
            String rootPath = tokenizer.nextToken().trim();
            if (StringUtils.isEmpty(rootPath))
                continue;

            Path path = Paths.get(rootPath);
            if (!Files.exists(path)) {
                uiMessageChannel.logMessage("Path " + path + " does not exist, skipping...");
            } else {
                try {
                    scanPath(path);
                } catch (IOException e) {
                    uiMessageChannel.logMessage("An error occured while scanning path " + path + ":");
                    uiMessageChannel.logMessage(e.getMessage());
                    hasErrors.set(true);
                }
            }
        }

        while (queuedRegistrationTasks.get() > 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
        uiMessageChannel.jobDone(!hasErrors.get());
    }


    private void scanPath(Path path) throws IOException {
        fileRepository.registerRootPath(path);
        Files.walkFileTree(path, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }


            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                MediaType mediaType = scanMediaType(file);
                if (mediaType != MediaType.OTHER) {
                    ListenableFuture<Long> futureResult = fileRepository.registerFile(file, attrs, mediaType);
                    queuedRegistrationTasks.incrementAndGet();
                    Futures.addCallback(futureResult, new FutureCallback<Long>() {
                        @Override
                        public void onSuccess(@Nullable Long result) {
                            uiMessageChannel.logMessage("Registered " + mediaType + ' ' + file + ". crc32: " + result);
                            queuedRegistrationTasks.decrementAndGet();
                        }


                        @Override
                        public void onFailure(Throwable t) {
                            uiMessageChannel.logMessage("Could not register " + mediaType + ' ' + path + ": " + t.getMessage());
                            queuedRegistrationTasks.decrementAndGet();
                            hasErrors.set(true);
                        }
                    }, MoreExecutors.directExecutor());
                }
                return FileVisitResult.CONTINUE;
            }


            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }


            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }


    private MediaType scanMediaType(Path file) {
        Matcher matcher = pictureFiletypes.matcher(file.toString());
        if (matcher.find()) {
            return MediaType.PICTURE;
        }
        matcher = videoFileTypes.matcher(file.toString());
        if (matcher.find()) {
            return MediaType.VIDEO;
        }
        return MediaType.OTHER;
    }


    public FileRepository getFileRepository() {
        return fileRepository;
    }
}
