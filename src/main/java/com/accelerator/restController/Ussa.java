package com.accelerator.restController;

import com.accelerator.services.DsspService;
import com.accelerator.services.PdbContextService;
import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/chemistry")
public class Ussa {

    @Resource
    private PdbContextService pdbContextService;

    @Resource
    private DsspService dsspService;

    @PostMapping(value = "/ussa", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<String> calculateHydrogenAccuracy(@RequestParam MultipartFile pdbFile,
                                                  @RequestParam String chain,  @RequestParam Boolean ai) throws JsonParseException, IOException {
        List<String> pdbContext = pdbContextService.getPdbContext(pdbFile);
        List<String> response = dsspService.getDsspContext(pdbContext, chain, ai);
        return response;
    }
}
