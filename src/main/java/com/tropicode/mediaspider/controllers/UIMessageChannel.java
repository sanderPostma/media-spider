package com.tropicode.mediaspider.controllers;

public interface UIMessageChannel {

    void jobStart(String jobName);

    void logMessage(String message);

    void jobDone(boolean successful);

}