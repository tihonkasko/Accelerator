package com.accelerator.convertors.impl;

import com.accelerator.convertors.AminoAcidConvertor;
import org.springframework.stereotype.Service;

@Service("aminoAcidConvertor")
public class AminoAcidConvertorImpl implements AminoAcidConvertor {

    @Override
    public String convertToShort(String longName) {
        if(longName.equalsIgnoreCase("ALA")) {
            return "A";
        } else if(longName.equalsIgnoreCase("ILE")) {
            return "I";
        } else if(longName.equalsIgnoreCase("LEU")) {
            return "L";
        } else if(longName.equalsIgnoreCase("MET") || longName.equalsIgnoreCase("MSE")) {
            return "M";
        } else if(longName.equalsIgnoreCase("VAL")) {
            return "V";
        } else if(longName.equalsIgnoreCase("PHE")) {
            return "F";
        } else if(longName.equalsIgnoreCase("TRP")) {
            return "W";
        } else if(longName.equalsIgnoreCase("TYR")) {
            return "Y";
        } else if(longName.equalsIgnoreCase("ASN")) {
            return "N";
        } else if(longName.equalsIgnoreCase("CYS") || longName.equalsIgnoreCase("CSO")) {
            return "C";
        } else if(longName.equalsIgnoreCase("GLN")) {
            return "Q";
        } else if(longName.equalsIgnoreCase("SER")) {
            return "S";
        } else if(longName.equalsIgnoreCase("THR")) {
            return "T";
        } else if(longName.equalsIgnoreCase("ASP")) {
            return "D";
        } else if(longName.equalsIgnoreCase("GLU")) {
            return "E";
        } else if(longName.equalsIgnoreCase("ARG")) {
            return "R";
        } else if(longName.equalsIgnoreCase("HIS")) {
            return "H";
        } else if(longName.equalsIgnoreCase("LYS")) {
            return "K";
        } else if(longName.equalsIgnoreCase("GLY")) {
            return "G";
        } else if(longName.equalsIgnoreCase("PRO")) {
            return "P";
        }
        return null;
    }

    @Override
    public String convertShortToDigit(String shortName) {
        if (shortName == null) {
            return "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
        }
        if(shortName.equalsIgnoreCase("A")) {
            return "1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("I")) {
            return "0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("L")) {
            return "0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("M")) {
            return "0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("V")) {
            return "0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("F")) {
            return "0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("W")) {
            return "0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("Y")) {
            return "0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("N")) {
            return "0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("C")) {
            return "0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("Q")) {
            return "0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("S")) {
            return "0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("T")) {
            return "0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("D")) {
            return "0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("E")) {
            return "0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("R")) {
            return "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0";
        } else if(shortName.equalsIgnoreCase("H")) {
            return "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0";
        } else if(shortName.equalsIgnoreCase("K")) {
            return "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0";
        } else if(shortName.equalsIgnoreCase("G")) {
            return "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0";
        } else if(shortName.equalsIgnoreCase("P")) {
            return "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1";
        }
        return null;
    }
}
