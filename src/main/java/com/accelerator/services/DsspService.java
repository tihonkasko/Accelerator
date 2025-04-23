package com.accelerator.services;

import java.util.List;

public interface DsspService {

    List<String> getDsspContext(List<String> pdbFile, String chain, Boolean ai);
}
