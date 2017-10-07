package com.tropicode.mediaspider.dto;

import com.tropicode.mediaspider.controllers.Selectable;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class MediaPath implements Selectable {

    private final File path;

    private MediaPath parentPath;

    private Set<MediaPath> children = new HashSet<>();

    private boolean selected;

    private int hashCode;


    public MediaPath(File path) {
        this.path = path;
        this.hashCode = path.getAbsolutePath().hashCode();
    }


    public File getPath() {
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
