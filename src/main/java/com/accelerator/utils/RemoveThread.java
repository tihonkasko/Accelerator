package com.accelerator.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.String.format;

public class RemoveThread implements Runnable {

    static final Logger rootLogger = LogManager.getRootLogger();

    private static final String BEFORE_REMOVE_MESSAGE = "File %s will be removed in %s minutes";
    private static final String AFTER_REMOVE_MESSAGE = "File removed successfully with path: %s";
    private static final String ERROR_REMOVE_MESSAGE = "Failed to remove file %s, exception was thrown: ";

    private final String path;
    private final Integer min;

    public RemoveThread(String path, Integer min) {
        this.path = path;
        this.min = min;
    }

    @Override
    public void run() {
        try{
            rootLogger.info(format(BEFORE_REMOVE_MESSAGE, path, min));
            Thread.sleep(60000L * min);
            Files.deleteIfExists(Paths.get(path));
            rootLogger.info(format(AFTER_REMOVE_MESSAGE, path));
        }
        catch(Exception exception){
            rootLogger.error(format(ERROR_REMOVE_MESSAGE, path), exception);
        }
    }
}
