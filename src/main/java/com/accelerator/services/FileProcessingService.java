package com.accelerator.services;

import java.io.IOException;

public interface FileProcessingService {

    void copyFile(String newFileName, String path) throws IOException;

    void removeFile(String path);
}
