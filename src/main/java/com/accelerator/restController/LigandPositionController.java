package com.accelerator.restController;

import com.accelerator.dto.AminoAcid;
import com.accelerator.dto.Ligand;
import com.accelerator.json.util.RestResponse;
import com.accelerator.services.LigandPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chemistry")
public class LigandPositionController {

    private static final String  OK_MESSAGE = "You are on the right way for calculating the position of the ligand";

    @Autowired
    LigandPositionService ligandPositionService;

    @GetMapping(value = "/ligand-position")
    public RestResponse getCountLigandPosition() throws JsonParseException {
        return new RestResponse(HttpStatus.OK.value(), OK_MESSAGE);
    }

    @PostMapping(value = "/ligand-position",  consumes = "application/json")
    public List<AminoAcid> postCountLigandPosition(@RequestBody Ligand ligand) throws JsonParseException {
        List<AminoAcid> relatedAminoAcids = ligandPositionService.getRelatedAminoAcids(ligand);
        return relatedAminoAcids;
    }
}
