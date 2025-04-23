package com.accelerator.configuration;

import java.util.ArrayList;
import java.util.Objects;

public class AcceleratorConfig {

    private ArrayList<Double> distance000 = new ArrayList<>();
    private ArrayList<Double> coordinats = new ArrayList<>();

    private ArrayList<String> atom = new ArrayList<>();
    private ArrayList<String> aminoAcid = new ArrayList<>();
    private ArrayList<String> numberOfPDB = new ArrayList<>();

    public String processPDB(String fullPDB, String element, int maxCount){
        String [] arrayFullPDB =fullPDB.trim().split("\\s");
        ArrayList<String> arrayWithoutProbel = new ArrayList<>();
        for (String elementFromFullPDB: arrayFullPDB){
            if (!Objects.equals(elementFromFullPDB, "")){
                arrayWithoutProbel.add(elementFromFullPDB);
            }
        }

        for (int i = 0; i < arrayWithoutProbel.size()-1; i++){
            if ((Objects.equals(arrayWithoutProbel.get(i), element)) && (Objects.equals(arrayWithoutProbel.get(i-2), "HETATM"))){
                coordinats.add(Double.parseDouble(arrayWithoutProbel.get(i+4)));
                coordinats.add(Double.parseDouble(arrayWithoutProbel.get(i+5)));
                coordinats.add(Double.parseDouble(arrayWithoutProbel.get(i+6)));
            }
        }



        for (int i = 0; i < arrayWithoutProbel.size()-1; i++){
            try {
                if ((Objects.equals(arrayWithoutProbel.get(i), "ATOM")) && (Double.parseDouble(arrayWithoutProbel.get(i + 1)) > 0)) {
                    double coordinatOfProteinX = Double.parseDouble(arrayWithoutProbel.get(i + 6));
                    double coordinatOfProteinY = Double.parseDouble(arrayWithoutProbel.get(i + 7));
                    double coordinatOfProteinZ = Double.parseDouble(arrayWithoutProbel.get(i + 8));

                    for (int xe = 0; xe <=coordinats.size() - 3; xe = xe + 3) {
                        double newDistance000 = Math.sqrt(Math.pow(coordinatOfProteinX - coordinats.get(xe), 2) +
                            Math.pow(coordinatOfProteinY - coordinats.get(xe + 1), 2) +
                            Math.pow(coordinatOfProteinZ - coordinats.get(xe + 2), 2));


                        for (int ok = 0; ok<coordinats.size()/3; ok++ ) {
                            if (!distance000.contains(ok) && distance000.size() < coordinats.size() / 3) {
                                distance000.add(newDistance000);
                                numberOfPDB.add(arrayWithoutProbel.get(i + 1));
                                atom.add(arrayWithoutProbel.get(i + 11));
                                aminoAcid.add(arrayWithoutProbel.get(i + 3));
                            }
                            if (distance000.get(xe / 3) > newDistance000) {
                                distance000.set(xe / 3, newDistance000);
                                numberOfPDB.set(xe / 3, arrayWithoutProbel.get(i + 1));
                                atom.set(xe / 3, arrayWithoutProbel.get(i + 11));
                                aminoAcid.set(xe / 3, arrayWithoutProbel.get(i + 3));
                            }
                        }
                    }
                }
            }catch (Exception ignored){}
        }
        String s = "";
        for(int numberOfIon=0; numberOfIon< distance000.size(); numberOfIon++ ) {
            s = s+  (" " + numberOfPDB.get(numberOfIon))+
                (" " + atom.get(numberOfIon))+
                (" " + aminoAcid.get(numberOfIon))+
                (" " + Float.parseFloat(String.valueOf(distance000.get(numberOfIon))));
        }
        if(s=="")
            s="No information about the selected element!";
        return s;
    }
}
