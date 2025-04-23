package com.accelerator.restController;

import com.accelerator.dto.HydrogenAccuracyResponse;
import com.accelerator.services.HydrogenAccuracyService;
import com.accelerator.services.PdbContextService;
import com.accelerator.services.PentUNFOLDFilterService;
import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
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
public class HydrogenAccuracyController {

    @Resource
    PdbContextService pdbContextService;
    @Resource
    PentUNFOLDFilterService pentUNFOLDFilterService;
    @Resource
    HydrogenAccuracyService hydrogenAccuracyService;

    @PostMapping(value = "/hydrogen-accuracy", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public HydrogenAccuracyResponse calculateHydrogenAccuracy(@RequestParam MultipartFile pdbFile, @RequestParam String chain,  @RequestParam Boolean ai) throws JsonParseException, IOException {

        List<String> pdbContext = pdbContextService.getPdbContext(pdbFile);
        SortedMap<Double, List<String[]>> pdbData = pentUNFOLDFilterService.filterPdbToDssp(pdbContext, chain);
        HydrogenAccuracyResponse response = hydrogenAccuracyService.calculateHydrogenAccuracyService(pdbData, ai);

        return response;
    }

}
