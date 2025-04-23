package com.accelerator.facades.implementations;

import com.accelerator.dto.PentUNFOLDModel;
import com.accelerator.facades.PentUNFOLDFacade;
import com.accelerator.services.DsspService;
import com.accelerator.services.DsspThirdPartyService;
import com.accelerator.services.PdbContextService;
import com.accelerator.services.PentUNFOLDFilterService;
import com.accelerator.services.PentUNFOLDUsageCounterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

@Service("pentUNFOLDFacade")
public class PentUNFOLDFacadeImpl implements PentUNFOLDFacade {

    private static final Logger rootLogger = LogManager.getRootLogger();

    @Resource
    DsspThirdPartyService dsspThirdPartyService;
    @Resource
    DsspService dsspService;
    @Resource
    PdbContextService pdbContextService;
    @Resource
    PentUNFOLDFilterService pentUNFOLDFilterService;
    @Resource
    PentUNFOLDUsageCounterService pentUNFOLDUsageCounterService;

    @Override
    public PentUNFOLDModel fillXlsxData(MultipartFile pdbFile, ArrayList<String> picResult,
                                        String chain, boolean include2d, boolean include3d,
                                        boolean isFileNeeded, boolean isCustomDsspNeeded) throws IOException {
        try {
            pentUNFOLDUsageCounterService.incrementCounter();
            List<String> pdbContext = pdbContextService.getPdbContext(pdbFile);
            List<String> dsspContext;
            if(isCustomDsspNeeded) {
                try {
                    dsspContext = include2d || include3d ? dsspService.getDsspContext(pdbContext, chain, true) : null;
                } catch (Exception e) {
                    dsspContext = dsspService.getDsspContext(pdbContext, chain, false);
                }
            } else {
                dsspContext = include2d || include3d ? dsspThirdPartyService.getDsspContext(pdbFile, isFileNeeded) : new ArrayList<>();
            }
            return preparePentUNFOLDModel(dsspContext, pdbContext, picResult, chain, isFileNeeded, isCustomDsspNeeded);
        } catch (RuntimeException | InterruptedException e){
            rootLogger.error("Failed to fetch Secondary structure content by" +
                    (isFileNeeded && !isCustomDsspNeeded? "DSSP file" : "DSSP file name") +
                    (isCustomDsspNeeded ? "Custom second structure analyzer." : ""));
            return null;
        }
    }

    @Override
    public PentUNFOLDModel fill1dSequenceData(String sequence) {
        PentUNFOLDModel pentUNFOLDModel = new PentUNFOLDModel();
        pentUNFOLDModel.setSequence(pentUNFOLDFilterService.filterSequence(sequence));
        return pentUNFOLDModel;
    }

    private PentUNFOLDModel preparePentUNFOLDModel(List<String> dsspContext,
                                                   List<String> pdbContext,
                                                   List<String> picContext,
                                                   String chainContext,
                                                   boolean isFileNeeded, boolean isCustomDsspNeeded) {
        PentUNFOLDModel pentUNFOLDModel = new PentUNFOLDModel();
        pentUNFOLDModel.setDssp(isCustomDsspNeeded ? dsspContext : pentUNFOLDFilterService.filterDssp(dsspContext, chainContext, isFileNeeded));
        pentUNFOLDModel.setPdb(pentUNFOLDFilterService.filterPdb(pdbContext, chainContext));
        pentUNFOLDModel.setSequence(pentUNFOLDFilterService.getSequence());
        pentUNFOLDModel.setPic(picContext);
        pentUNFOLDModel.setChain(chainContext);
        return pentUNFOLDModel;
    }
}
