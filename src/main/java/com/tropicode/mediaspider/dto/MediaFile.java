package com.tropicode.mediaspider.dto;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;

public class MediaFile {

    private int hashCode;

    private Path filePath;

    private FileTime earliestTime;

    private Map<MediaPath, Path> mediaPaths = new HashMap<>();

    private Path movedTo;

    private MediaType mediaType;


    public MediaFile(Path filePath, BasicFileAttributes attrs, MediaType mediaType) {
        this.filePath = filePath;
        setEarliestTime(attrs);
        setMediaType(mediaType);
    }


    public int getHashCode() {
        return hashCode;
    }


    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }


    public Path getFilePath() {
        return filePath;
    }


    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }


    public Map<MediaPath, Path> getMediaPaths() {
        return mediaPaths;
    }


    public void setMediaPaths(Map<MediaPath, Path> mediaPaths) {
        this.mediaPaths = mediaPaths;
    }


    public Path getMovedTo() {
        return movedTo;
    }


    public void setMovedTo(Path movedTo) {
        this.movedTo = movedTo;
    }


    public void setEarliestTime(BasicFileAttributes attrs) {
        FileTime earliestTime = attrs.creationTime().compareTo(attrs.lastModifiedTime()) < 0 ? attrs.creationTime() : attrs.lastModifiedTime();
        if (this.earliestTime == null || this.earliestTime.compareTo(earliestTime) >= 0) {
            this.earliestTime = earliestTime;
        }
    }


    public FileTime getEarliestTime() {
        return earliestTime;
    }


    public void addMediaPath(MediaPath mediaPath, Path file) {
        this.mediaPaths.put(mediaPath, file);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MediaFile)) {
            return false;
        }

        MediaFile mediaFile = (MediaFile) o;

        return getHashCode() == mediaFile.getHashCode();
    }


    @Override
    public int hashCode() {
        return getHashCode();
    }


    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }


    public MediaType getMediaType() {
        return mediaType;
    }


}
