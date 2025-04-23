package com.accelerator.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PdbContextService {

    List<String> getPdbContext(MultipartFile pdbFile) throws IOException;
}
