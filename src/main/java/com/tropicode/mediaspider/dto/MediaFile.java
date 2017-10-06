package com.tropicode.mediaspider.dto;

import com.tropicode.mediaspider.controllers.Selectable;

import java.util.HashSet;
import java.util.Set;

public class MediaFile implements Selectable {

    private int hashCode;

    private String fileName;

    private Set<MediaPath> mediaPaths = new HashSet<>();

    private String movedTo;

    private boolean selected;


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
}
