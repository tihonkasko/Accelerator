package com.accelerator.services.implementations;

import com.accelerator.dto.AminoAcid;
import com.accelerator.dto.Ligand;
import com.accelerator.services.LigandPositionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service("ligandPositionService")
public class LigandPositionServiceImpl implements LigandPositionService {

    private ArrayList<Double> distance = new ArrayList<>();
    private ArrayList<Double> coordinates = new ArrayList<>();
    private ArrayList<String> atom = new ArrayList<>();
    private ArrayList<String> aminoAcid = new ArrayList<>();
    private ArrayList<String> numberOfPDB = new ArrayList<>();
    
    @Override
    public List<AminoAcid> getRelatedAminoAcids(Ligand ligand){
        ArrayList<String> preparedPDB =  preparePDBContent(ligand.getPDBFile());
        addCoordinates(preparedPDB, ligand.getLigandName());
        countLigandPosition(preparedPDB);
        List<AminoAcid> relatedAminoAcids = countAminoAcidsResult();
        return relatedAminoAcids;
    }

    private ArrayList<String> preparePDBContent(String pdbFile) {
        String [] arrayFullPDB =pdbFile.trim().split("\\s");
        ArrayList<String> arrayWithoutSpace = new ArrayList<>();
        for (String elementFromFullPDB: arrayFullPDB){
            if (!Objects.equals(elementFromFullPDB, "")){
                arrayWithoutSpace.add(elementFromFullPDB);
            }
        }
        return arrayWithoutSpace;
    }

    private void addCoordinates(ArrayList<String> preparedPDB, String ligandName) {
        for (int i = 0; i < preparedPDB.size()-1; i++){
            if ((Objects.equals(preparedPDB.get(i), ligandName)) && (Objects.equals(preparedPDB.get(i-2), "HETATM"))){
                coordinates.add(Double.parseDouble(preparedPDB.get(i+4)));
                coordinates.add(Double.parseDouble(preparedPDB.get(i+5)));
                coordinates.add(Double.parseDouble(preparedPDB.get(i+6)));
            }
        }
    }

    private void countLigandPosition(ArrayList<String> preparedPDB) {
        for (int i = 0; i < preparedPDB.size()-1; i++){
            try {
                if ((Objects.equals(preparedPDB.get(i), "ATOM")) && (Double.parseDouble(preparedPDB.get(i + 1)) > 0)) {
                    double coordinatOfProteinX = Double.parseDouble(preparedPDB.get(i + 6));
                    double coordinatOfProteinY = Double.parseDouble(preparedPDB.get(i + 7));
                    double coordinatOfProteinZ = Double.parseDouble(preparedPDB.get(i + 8));

                    for (int xe = 0; xe <=coordinates.size() - 3; xe = xe + 3) {
                        double newdistance = Math.sqrt(Math.pow(coordinatOfProteinX - coordinates.get(xe), 2) +
                                Math.pow(coordinatOfProteinY - coordinates.get(xe + 1), 2) +
                                Math.pow(coordinatOfProteinZ - coordinates.get(xe + 2), 2));


                        for (int ok = 0; ok<coordinates.size()/3; ok++ ) {
                            if (!distance.contains(ok) && distance.size() < coordinates.size() / 3) {
                                distance.add(newdistance);
                                numberOfPDB.add(preparedPDB.get(i + 1));
                                atom.add(preparedPDB.get(i + 11));
                                aminoAcid.add(preparedPDB.get(i + 3));
                            }
                            if (distance.get(xe / 3) > newdistance) {
                                distance.set(xe / 3, newdistance);
                                numberOfPDB.set(xe / 3, preparedPDB.get(i + 1));
                                atom.set(xe / 3, preparedPDB.get(i + 11));
                                aminoAcid.set(xe / 3, preparedPDB.get(i + 3));
                            }
                        }
                    }
                }
            } catch (Exception exception){
                System.out.println("Count Ligand Position failed : " + exception.getMessage());
            }
        }
    }

    private List<AminoAcid> countAminoAcidsResult() {
        List<AminoAcid> relatedAminoAcids = new ArrayList<>();
        for(int numberOfIon = 0; numberOfIon < distance.size(); numberOfIon++) {
            AminoAcid relatedAminoAcid = new AminoAcid();
            relatedAminoAcid.setAminoAcidName(aminoAcid.get(numberOfIon));
            relatedAminoAcid.setAminoAcidAtom(atom.get(numberOfIon));
            relatedAminoAcid.setNearestAtomDistance(Double.parseDouble(String.valueOf(distance.get(numberOfIon))));
            relatedAminoAcid.setAminoAcidResiduePDBNumber(Long.valueOf(numberOfPDB.get(numberOfIon)));
            relatedAminoAcids.add(relatedAminoAcid);
        }
        return relatedAminoAcids;
    }
}
