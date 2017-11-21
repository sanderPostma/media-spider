package com.tropicode.mediaspider.dto;

import com.tropicode.mediaspider.controllers.Selectable;
import javafx.scene.control.CheckBoxTreeItem;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MediaPath implements Selectable {

    private final Path path;

    private MediaPath parentPath;

    private Set<MediaPath> children = new HashSet<>();

    private boolean selected;

    private int hashCode;

    private Map<MediaType, AtomicInteger> fileCounters = new HashMap<>();
    private boolean root;


    public MediaPath(Path path) {
        this.path = path;
        this.hashCode = path.toAbsolutePath().toString().hashCode();
        for (MediaType value : MediaType.values()) {
            fileCounters.put(value, new AtomicInteger());
        }
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


    public void incrementFileCount(MediaType mediaType) {
        fileCounters.get(mediaType).incrementAndGet();
    }


    public int getFileCount(MediaType mediaType) {
        return fileCounters.get(mediaType).get();
    }


    public void setFileCount(MediaType mediaType, int fileCount) {
        fileCounters.get(mediaType).set(fileCount);
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


    public void setRoot(boolean root) {
        this.root = root;
    }


    public boolean isRoot() {
        return root;
    }


    public CheckBoxTreeItem<MediaPath> toTreeItem() {
        CheckBoxTreeItem<MediaPath> treeItem = new CheckBoxTreeItem(this);
        treeItem.setSelected(true);
        treeItem.setExpanded(true);
        treeItem.addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), event ->
                setSelected(event.getTreeItem().isSelected()));
        for (MediaPath childPath : children) {
            treeItem.getChildren().add(childPath.toTreeItem());
        }
        return treeItem;
    }


    @Override
    public String toString() {
        StringBuilder label = new StringBuilder();
        if (isRoot()) {
            label.append(path.toAbsolutePath().toString());
        } else {
            label.append(path.getFileName().toString());
        }
        StringBuilder counters = new StringBuilder();
        fileCounters.entrySet().forEach(mediaTypeAtomicIntegerEntry -> {
            AtomicInteger counter = mediaTypeAtomicIntegerEntry.getValue();
            if (counter.get() > 0) {
                if (counters.length() <= 0) {
                    counters.append("Found ");
                } else {
                    counters.append(" and ");
                }
                counters.append(String.format("%d %s item(s)", counter.get(), mediaTypeAtomicIntegerEntry.getKey()));
            }
        });
        if (counters.length() > 0) {
            label.append(" - ").append(counters).append('.');
        }
        return label.toString();
    }


    public void addChild(MediaPath mediaPath) {
        children.add(mediaPath);
    }
}