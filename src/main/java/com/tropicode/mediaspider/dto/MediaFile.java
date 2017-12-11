package com.tropicode.mediaspider.dto;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MediaFile {

    private Long crc;

    private FileTime earliestTime;

    private Map<Path, MediaPath> mediaPaths = new HashMap<>();

    private Path movedTo;

    private MediaType mediaType;


    public MediaFile(Long crc, BasicFileAttributes attrs, MediaType mediaType) {
        this.crc = crc;
        setEarliestTime(attrs);
        setMediaType(mediaType);
    }


    public Long getCrc() {
        return crc;
    }


    public void setCrc(Long crc) {
        this.crc = crc;
    }


    public Map<Path, MediaPath> getMediaPaths() {
        return mediaPaths;
    }


    public void setMediaPaths(Map<Path, MediaPath> mediaPaths) {
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


    public void registerPath(Path file, MediaPath mediaPath) {
        this.mediaPaths.put(file, mediaPath);
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

        return getCrc() == mediaFile.getCrc();
    }


    @Override
    public int hashCode() {
        return getCrc().hashCode();
    }


    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }


    public MediaType getMediaType() {
        return mediaType;
    }


    public boolean hasMediaPath(MediaPath mediaPath) {
        return mediaPaths.containsKey(mediaPath);
    }


    @Override
    public String toString() {
        Set<Path> printedFileNames = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        mediaPaths.keySet().forEach(path -> {
            Path fileName = path.getFileName();
            if (!printedFileNames.contains(fileName)) {
                if (builder.length() > 0) {
                    builder.append('=');
                }
                builder.append(fileName);
                printedFileNames.add(fileName);
            }
        });
        builder.insert(0, mediaType.toString() + ' ');
        builder.append(", crc").append(getCrc());
        return builder.toString();
    }
}
