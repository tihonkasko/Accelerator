package com.accelerator.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DsspThirdPartyService {

    List<String> getDsspContext(MultipartFile pdbFile, boolean isFileNeeded) throws IOException, InterruptedException;
}
