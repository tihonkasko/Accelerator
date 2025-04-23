package com.accelerator.services;

import com.accelerator.services.implementations.DsspServiceImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DsspServiceTest {

    DsspServiceImpl dsspService = new DsspServiceImpl();

    private static final Double FIRST_AMINO_ACID_RESIDUE_KEY = 1.0;
    private static final Double THREE_TURN = 3.0;
    private static final Double FOUR_TURN = 4.0;
    private static final Double FIVE_TURN = 5.0;

    @Test
    public void findSecondAminoAcidResidueKey3Turn(){
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = prepareAminoAcidResidues();
        Double secondSecondAminoAcidResidueKey =
                dsspService.findSecondAminoAcidResidueKey(aminoAcidResidues, FIRST_AMINO_ACID_RESIDUE_KEY, THREE_TURN, 0);
        assertEquals(4.0, secondSecondAminoAcidResidueKey, 0.0);
    }

    @Test
    public void findSecondAminoAcidResidueKey4Turn(){
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = prepareAminoAcidResidues();
        Double secondSecondAminoAcidResidueKey =
                dsspService.findSecondAminoAcidResidueKey(aminoAcidResidues, FIRST_AMINO_ACID_RESIDUE_KEY, FOUR_TURN, 0);
        assertEquals(5.0, secondSecondAminoAcidResidueKey, 0.0);
    }

    @Test
    public void findSecondAminoAcidResidueKey5Turn(){
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = prepareAminoAcidResidues();
        Double secondSecondAminoAcidResidueKey =
                dsspService.findSecondAminoAcidResidueKey(aminoAcidResidues, FIRST_AMINO_ACID_RESIDUE_KEY, FIVE_TURN, 0);
        assertEquals(6.0, secondSecondAminoAcidResidueKey, 0.0);
    }

    @Test
    public void findSecondAminoAcidResidueKey1Variant(){
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = prepare1AminoAcidResidues();
        Double secondSecondAminoAcidResidueKey =
                dsspService.findSecondAminoAcidResidueKey(aminoAcidResidues, FIRST_AMINO_ACID_RESIDUE_KEY, THREE_TURN, 0);
        assertEquals(2.3, secondSecondAminoAcidResidueKey, 0.0);
    }

    @Test
    public void findSecondAminoAcidResidueKey2Variant(){
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = prepare2AminoAcidResidues();
        Double secondSecondAminoAcidResidueKey =
                dsspService.findSecondAminoAcidResidueKey(aminoAcidResidues, FIRST_AMINO_ACID_RESIDUE_KEY, THREE_TURN, 0);
        assertEquals(4.0, secondSecondAminoAcidResidueKey, 0.0);
    }

    @Test
    public void findSecondAminoAcidResidueKey3Variant(){
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = prepare3AminoAcidResidues();
        Double secondSecondAminoAcidResidueKey =
                dsspService.findSecondAminoAcidResidueKey(aminoAcidResidues, FIRST_AMINO_ACID_RESIDUE_KEY, THREE_TURN, 0);
        assertEquals(3.1, secondSecondAminoAcidResidueKey, 0.0);
    }

    @Test
    public void findSecondAminoAcidResidueKey4Variant(){
        List<Map.Entry<Double, List<String[]>>> aminoAcidResidues = prepare4AminoAcidResidues();
        Double secondSecondAminoAcidResidueKey =
                dsspService.findSecondAminoAcidResidueKey(aminoAcidResidues, FIRST_AMINO_ACID_RESIDUE_KEY, THREE_TURN, 0);
        assertNull(secondSecondAminoAcidResidueKey);
    }

    private List<Map.Entry<Double, List<String[]>>> prepareAminoAcidResidues() {
        SortedMap<Double, List<String[]>> pdbData = new TreeMap<>();
        pdbData.put(1.0, new ArrayList<>());
        pdbData.put(2.0, new ArrayList<>());
        pdbData.put(3.0, new ArrayList<>());
        pdbData.put(4.0, new ArrayList<>());
        pdbData.put(5.0, new ArrayList<>());
        pdbData.put(6.0, new ArrayList<>());
        return new ArrayList<>(pdbData.entrySet());
    }

    private List<Map.Entry<Double, List<String[]>>> prepare1AminoAcidResidues() {
        SortedMap<Double, List<String[]>> pdbData = new TreeMap<>();
        pdbData.put(1.0, new ArrayList<>());
        pdbData.put(2.0, new ArrayList<>());
        pdbData.put(2.1, new ArrayList<>());
        pdbData.put(2.3, new ArrayList<>());
        return new ArrayList<>(pdbData.entrySet());
    }

    private List<Map.Entry<Double, List<String[]>>> prepare2AminoAcidResidues() {
        SortedMap<Double, List<String[]>> pdbData = new TreeMap<>();
        pdbData.put(1.0, new ArrayList<>());
        pdbData.put(3.0, new ArrayList<>());
        pdbData.put(4.0, new ArrayList<>());
        return new ArrayList<>(pdbData.entrySet());
    }

    private List<Map.Entry<Double, List<String[]>>> prepare3AminoAcidResidues() {
        SortedMap<Double, List<String[]>> pdbData = new TreeMap<>();
        pdbData.put(1.0, new ArrayList<>());
        pdbData.put(3.0, new ArrayList<>());
        pdbData.put(3.1, new ArrayList<>());
        return new ArrayList<>(pdbData.entrySet());
    }

    private List<Map.Entry<Double, List<String[]>>> prepare4AminoAcidResidues() {
        SortedMap<Double, List<String[]>> pdbData = new TreeMap<>();
        pdbData.put(1.0, new ArrayList<>());
        pdbData.put(5.0, new ArrayList<>());
        return new ArrayList<>(pdbData.entrySet());
    }
}
