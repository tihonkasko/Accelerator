package com.accelerator.cron;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FileRemoveCronJob {

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/London")
    public void removeUserFiles() throws IOException {
        FileUtils.cleanDirectory(new File("src/main/resources/user-files"));
        System.out.println("__________________________ Files removed _____________________________");
    }
}
