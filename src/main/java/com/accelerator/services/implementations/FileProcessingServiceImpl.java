package com.accelerator.services.implementations;

import com.accelerator.services.FileProcessingService;
import com.accelerator.utils.RemoveThread;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Service("fileProcessingService")
public class FileProcessingServiceImpl implements FileProcessingService {

    private static final String NEW_FILE_PATH = "src/main/resources/user-files/%s.%s";
    private static final Integer MINUTES_TO_REMOVE = 2;

    @Override
    public void copyFile(String newFileName, String path) throws IOException {
        synchronized (this) {
            Path motherFile = Paths.get(path);
            Path copiedFile = Paths.get(format(NEW_FILE_PATH, newFileName, getExtension(path)));
            Files.copy(motherFile, copiedFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void removeFile(String path) {
        RemoveThread removeThread = new RemoveThread(path, MINUTES_TO_REMOVE);
        Thread thread = new Thread(removeThread,"RemovingOldFile");
        thread.start();
    }
}
