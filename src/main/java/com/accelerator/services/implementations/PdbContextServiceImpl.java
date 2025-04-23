package com.accelerator.services.implementations;

import com.accelerator.services.PdbContextService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("pdbContextService")
public class PdbContextServiceImpl implements PdbContextService {

    @Override
    public List<String> getPdbContext(MultipartFile pdbFile) throws IOException {
        String content = new String(pdbFile.getBytes(), StandardCharsets.UTF_8);
        int pdbContextStartIndex = content.indexOf("ATOM     ");
        String atomContent = content.substring(pdbContextStartIndex);
        return convertToList(atomContent);
    }

    private List<String> convertToList(String atomContent) {
        String[] arrayAtomContent;
        arrayAtomContent = atomContent.split("\\n");
        return Arrays.asList(arrayAtomContent);
    }
}
