/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Vláďa
 */
public class TsetinEncoding implements EncodingType {

    private HashMap<String, Integer> mapovani;
    private Subformula formule;
    private LinkedList<Integer[]> klauzule;
    private LogicTree tree;
    private boolean done;
    private int nNumber = 1;

    public TsetinEncoding() {
        mapovani = new HashMap<>();
        klauzule = new LinkedList<>();
        tree = new LogicTree();
    }

    @Override
    public LinkedList<Integer[]> encode(String formule, SolverProperties prop) {
        mapovani = new HashMap<>();
        klauzule = new LinkedList<>();
        nNumber = 1;
        done = false;
        this.formule = tree.getLogicTree(formule, prop.getConjuction());
        int root = decomposeFormula(this.formule);
        klauzule.add(new Integer[]{root});
        done = true;
        return klauzule;
    }

    @Override
    public HashMap<String, Boolean> retroTransformation(HashMap<Integer, Boolean> assignment) {
        HashMap<String, Boolean> result = new HashMap<>();
        HashMap<Integer, String> backMapping = new HashMap<>();
        for (Map.Entry<String, Integer> entrySet : mapovani.entrySet()) {
            String key = entrySet.getKey();
            Integer value = entrySet.getValue();
            try {
                Integer.parseInt(key);
            } catch (Exception e) {
                backMapping.put(value, key);
            }
        }
        for (Map.Entry<Integer, Boolean> entrySet : assignment.entrySet()) {
            Integer key = entrySet.getKey();
            Boolean value = entrySet.getValue();
            if (backMapping.containsKey(key)) {
                result.put(backMapping.get(key), value);
            }
        }
        return result;
    }

    public boolean runEncoding() {
        int root = decomposeFormula(formule);
        klauzule.add(new Integer[]{root});
        done = true;
        return true;
    }

    public LinkedList<Integer[]> getClauses() {
        if (done) {
            return klauzule;
        }
        return null;
    }

    private int decomposeFormula(Subformula s) {
        LinkedList<Integer> symbols = new LinkedList<>();
        for (Subformula childe : s.getSubformulas()) {
            if (childe.getType() == Subformula.FormulaType.LITERAL) {
                symbols.add(childe.getLiteral());
            } else {
                symbols.add(decomposeFormula(childe));
            }
        }
        switch (s.getType()) {
            case AND:
                return andEncoding(symbols.toArray(new Integer[0]));
            case OR:
                return orEncoding(symbols.toArray(new Integer[0]));
            case IMP:
                return implicationEncoding(symbols.toArray(new Integer[0]));
        }
        return 0;

    }

    private int andEncoding(Integer[] symbols) {
        int formuleNumber = nNumber;
        formuleNumber = getNumber(String.valueOf(formuleNumber));
        Integer[] clause = new Integer[symbols.length + 1];
        for (int i = 0; i < symbols.length; i++) {
            clause[i] = -1 * symbols[i];
        }
        clause[symbols.length] = formuleNumber;
        klauzule.add(clause);
        for (int symbol : symbols) {
            clause = new Integer[2];
            clause[0] = symbol;
            clause[1] = -1 * formuleNumber;
            klauzule.add(clause);
        }
        return formuleNumber;
    }

    private int orEncoding(Integer[] symbols) {
        int formuleNumber = nNumber;
        formuleNumber = getNumber(String.valueOf(formuleNumber));
        Integer[] clause = new Integer[symbols.length + 1];
        for (int i = 0; i < symbols.length; i++) {
            clause[i] = symbols[i];
        }
        clause[symbols.length] = -1 * formuleNumber;
        klauzule.add(clause);
        for (int symbol : symbols) {
            clause = new Integer[2];
            clause[0] = -1 * symbol;
            clause[1] = formuleNumber;
            klauzule.add(clause);
        }
        return formuleNumber;
    }

    private int implicationEncoding(Integer[] symbols) {
        if (symbols.length != 2) {
            return 0;
        } else {
            symbols[0] = -1 * symbols[0];
            return orEncoding(symbols);
        }
    }

    private int getNumber(String variable) {
        if (mapovani.containsKey(variable)) {
            return mapovani.get(variable);
        } else {
            nNumber++;
            mapovani.put(variable, nNumber - 1);
            return nNumber - 1;
        }
    }

}
