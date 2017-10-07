package com.tropicode.mediaspider.dto;

import com.tropicode.mediaspider.controllers.Selectable;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Set;

public class MediaFile implements Selectable {

    private int hashCode;

    private String fileName;

    private FileTime earliestTime;

    private Set<MediaPath> mediaPaths = new HashSet<>();

    private String movedTo;

    private int duplicates = 0;

    private boolean selected;


    public MediaFile(Path file, BasicFileAttributes attrs) {
        this.fileName = file.getFileName().toString();
        setEarliestTime(attrs);
    }


    public int getHashCode() {
        return hashCode;
    }


    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public Set<MediaPath> getMediaPaths() {
        return mediaPaths;
    }


    public void setMediaPaths(Set<MediaPath> mediaPaths) {
        this.mediaPaths = mediaPaths;
    }


    public String getMovedTo() {
        return movedTo;
    }


    public void setMovedTo(String movedTo) {
        this.movedTo = movedTo;
    }


    @Override
    public boolean isSelected() {
        return selected;
    }


    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
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


    public void setEarliestTime(BasicFileAttributes attrs) {
        FileTime earliestTime = attrs.creationTime().compareTo(attrs.lastModifiedTime()) < 0 ? attrs.creationTime() : attrs.lastModifiedTime();
        if (this.earliestTime == null || this.earliestTime.compareTo(earliestTime) >= 0) {
            this.earliestTime = earliestTime;
        }
    }


    public FileTime getEarliestTime() {
        return earliestTime;
    }


    public int getDuplicates() {
        return duplicates;
    }


    public void setDuplicates(int duplicates) {
        this.duplicates = duplicates;
    }


    public void addDuplicate() {
        duplicates++;
    }

}
