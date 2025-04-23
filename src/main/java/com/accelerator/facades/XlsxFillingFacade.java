package com.accelerator.facades;

import com.accelerator.dto.PentUNFOLDModel;

public interface XlsxFillingFacade {

    void fill1DFile(PentUNFOLDModel pentUNFOLDModel, String name) throws Exception;

    void fill2DFile(PentUNFOLDModel pentUNFOLDModel, String name) throws Exception;

    void fill3DFile(PentUNFOLDModel pentUNFOLDModel, String name) throws Exception;
}
