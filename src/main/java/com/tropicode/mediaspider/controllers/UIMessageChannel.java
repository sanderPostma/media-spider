package com.tropicode.mediaspider.controllers;

public interface UIMessageChannel {

    void jobStarted(String jobName);

    void logMessage(String message);

    void jobDone(boolean successful);

}