package com.tropicode.mediaspider.dto;

import com.tropicode.mediaspider.controllers.Selectable;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class MediaPath implements Selectable {

    private File path;

    private MediaPath parentPath;

    private Set<MediaPath> children = new HashSet<>();

    private boolean selected;


    public File getPath() {
        return path;
    }


    public void setPath(File path) {
        this.path = path;
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
}
