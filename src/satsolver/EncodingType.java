/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Vláďa
 */
public interface EncodingType {
    public LinkedList<Integer[]> encode(String formule, SolverProperties prop);
    public HashMap<String, Boolean> retroTransformation(HashMap<Integer, Boolean> assignment);
}
