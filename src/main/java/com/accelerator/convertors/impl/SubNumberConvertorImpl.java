package com.accelerator.convertors.impl;

import com.accelerator.convertors.SubNumberConvertor;
import org.springframework.stereotype.Service;

@Service("subNumberConvertor")
public class SubNumberConvertorImpl implements SubNumberConvertor {

    @Override
    public String convertToPointDigit(String litterSubNumber) {
        if(litterSubNumber.equalsIgnoreCase("A")) {
            return ".1";
        } else if(litterSubNumber.equalsIgnoreCase("B")) {
            return ".2";
        } else if(litterSubNumber.equalsIgnoreCase("C")) {
            return ".3";
        } else if(litterSubNumber.equalsIgnoreCase("D")) {
            return ".4";
        } else if(litterSubNumber.equalsIgnoreCase("E")) {
            return ".5";
        } else if(litterSubNumber.equalsIgnoreCase("F")) {
            return ".6";
        } else if(litterSubNumber.equalsIgnoreCase("G")) {
            return ".7";
        } else if(litterSubNumber.equalsIgnoreCase("H")) {
            return ".8";
        } else if(litterSubNumber.equalsIgnoreCase("I")) {
            return ".9";
        } else if(litterSubNumber.equalsIgnoreCase("J")) {
            return ".11";
        } else if(litterSubNumber.equalsIgnoreCase("K")) {
            return ".12";
        } else if(litterSubNumber.equalsIgnoreCase("L")) {
            return ".13";
        } else if(litterSubNumber.equalsIgnoreCase("M")) {
            return ".14";
        } else if(litterSubNumber.equalsIgnoreCase("N")) {
            return ".15";
        } else if(litterSubNumber.equalsIgnoreCase("O")) {
            return ".16";
        } else if(litterSubNumber.equalsIgnoreCase("Q")) {
            return ".17";
        } else if(litterSubNumber.equalsIgnoreCase("R")) {
            return ".18";
        } else if(litterSubNumber.equalsIgnoreCase("S")) {
            return ".19";
        } else if(litterSubNumber.equalsIgnoreCase("T")) {
            return ".21";
        } else if(litterSubNumber.equalsIgnoreCase("U")) {
            return ".22";
        } else if(litterSubNumber.equalsIgnoreCase("V")) {
            return ".23";
        } else if(litterSubNumber.equalsIgnoreCase("W")) {
            return ".24";
        } else if(litterSubNumber.equalsIgnoreCase("X")) {
            return ".25";
        } else if(litterSubNumber.equalsIgnoreCase("Y")) {
            return ".26";
        } else if(litterSubNumber.equalsIgnoreCase("Z")) {
            return ".27";
        }
        return null;
    }
}
