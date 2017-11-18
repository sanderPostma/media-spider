package com.tropicode.mediaspider.dto;

public enum MediaType {
    OTHER, PICTURE, VIDEO;


    @Override
    public String toString() {
        switch (this) {
            case OTHER:
                return "other";
            case PICTURE:
                return "picture";
            case VIDEO:
                return "video";
        }
        return "";
    }
}
