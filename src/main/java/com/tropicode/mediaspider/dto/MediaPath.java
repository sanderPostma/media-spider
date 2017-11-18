package com.tropicode.mediaspider.dto;

import com.tropicode.mediaspider.controllers.Selectable;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class MediaPath implements Selectable {

    private final Path path;

    private MediaPath parentPath;

    private Set<MediaPath> children = new HashSet<>();

    private boolean selected;

    private int hashCode;

    private int fileCount;


    public MediaPath(Path path) {
        this.path = path;
        this.hashCode = path.toAbsolutePath().toAbsolutePath().hashCode();
    }


    public Path getPath() {
        return path;
    }


    public MediaPath getParentPath() {
        return parentPath;
    }


    public void setParentPath(MediaPath parentPath) {
        this.parentPath = parentPath;
    }


    public Set<MediaPath> getChildren() {
        return children;
    }


    public void setChildren(Set<MediaPath> children) {
        this.children = children;
    }


    public void incrementFileCount() {
        this.fileCount = 0;
    }


    public int getFileCount() {
        return fileCount;
    }


    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
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
        if (!(o instanceof MediaPath)) {
            return false;
        }

        MediaPath mediaPath = (MediaPath) o;

        return hashCode == mediaPath.hashCode;
    }


    @Override
    public int hashCode() {
        return hashCode;
    }
}
