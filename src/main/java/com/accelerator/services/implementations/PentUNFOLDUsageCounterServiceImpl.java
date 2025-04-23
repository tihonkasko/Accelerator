package com.accelerator.services.implementations;

import com.accelerator.services.PentUNFOLDUsageCounterService;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;

@Service("pentUNFOLDUsageCounterService")
public class PentUNFOLDUsageCounterServiceImpl implements PentUNFOLDUsageCounterService {

    private static final String PENT_UNFOLD_USES = "Last server restart: %s, Number of uses of the algorithm: %s";
    private static final LocalDateTime SERVER_DEPLOY_DATE = LocalDateTime.now();
    private static long usesPentUNFOLDAlgorithm = 0;

    public void incrementCounter() {
        usesPentUNFOLDAlgorithm++;
        System.out.println(format(PENT_UNFOLD_USES, getServerLastDeployDate(), usesPentUNFOLDAlgorithm));
    }

    private String getServerLastDeployDate() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return SERVER_DEPLOY_DATE.format(format);
    }
}
