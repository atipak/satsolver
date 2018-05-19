/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oldclasses;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import satsolver.EncodingType;
import satsolver.SolverProperties;

/**
 *
 * @author patikv
 */
public class TsetinEncodingOld implements EncodingType {

    private HashMap<String, Integer> mapovani;
    private HashMap<String, String> spojky;
    private String formule;
    private LinkedList<Integer[]> klauzule;
    private boolean done;
    private int index;
    private int nNumber = 1;

    private enum FormulaType {

        imp, and, or, none
    }

    public TsetinEncodingOld() {

    }

    @Override
    public LinkedList<Integer[]> encode(String formule, SolverProperties prop) {
        this.spojky = prop.getConjuction();
        this.formule = formule;
        mapovani = new HashMap<>();
        klauzule = new LinkedList<>();
        index = 0;
        nNumber = 1;
        if (spojky != null) {
            formule = formule.replaceAll(spojky.get("and"), "&").replaceAll(spojky.get("or"), "|").replaceAll(spojky.get("imp"), ">");
        }
        int root = decomposeFormula();
        klauzule.add(new Integer[]{root});
        done = true;
        return klauzule;
    }

    private int decomposeFormula() {
        assert (formule.charAt(index) == '(');
        FormulaType ft = FormulaType.none;
        LinkedList<Integer> symbols = new LinkedList<>();
        boolean isNeg = false;
        StringBuilder actualSymbol = new StringBuilder();
        int sym;
        index++;
        while (formule.length() > index && formule.charAt(index) != ')') {
            char ch = formule.charAt(index);
            switch (ch) {
                case '(':
                    int numberOfFormula = decomposeFormula();
                    if (numberOfFormula == 18) {
                        int stop = 1;
                    }
                    if (isNeg) {
                        symbols.add(-1 * numberOfFormula);
                    } else {
                        symbols.add(numberOfFormula);
                    }
                    isNeg = false;
                    break;
                case ' ':
                    break;
                case '-':
                    isNeg = true;
                    break;
                case '>':
                    if (ft == FormulaType.none) {
                        ft = FormulaType.imp;
                    } else {
                        // vyhod chybu
                    }
                    break;
                case '&':
                    if (ft == FormulaType.none) {
                        ft = FormulaType.and;
                    } else {
                        // vyhod chybu
                    }
                    break;
                case '|':
                    if (ft == FormulaType.none) {
                        ft = FormulaType.or;
                    } else {
                        // vyhod chybu
                    }
                    break;
                default:
                    actualSymbol.append(ch);
                    break;
            }
            if (ch == ' ' || ch == '|' || ch == '>' || ch == '&') {
                sym = retSymbol(actualSymbol, isNeg);
                if (sym != 0) {
                    symbols.add(sym);
                    actualSymbol.delete(0, actualSymbol.length());
                    isNeg = false;
                }
            }
            if (formule.charAt(index) != ')') {
                index++;
            }
        }
        if (formule.length() > index && formule.charAt(index) == ')') {
            sym = retSymbol(actualSymbol, isNeg);
            if (sym != 0) {
                symbols.add(sym);
                actualSymbol.delete(0, actualSymbol.length());
                isNeg = false;
            }
        }
        index++;
        switch (ft) {
            case imp:
                return implicationEncoding(symbols.toArray(new Integer[0]));
            case and:
                return andEncoding(symbols.toArray(new Integer[0]));
            case or:
                return orEncoding(symbols.toArray(new Integer[0]));
            case none:
                return 0;
        }
        return 0;

    }

    private int retSymbol(StringBuilder actualSymbol, boolean isNeg) {
        if (actualSymbol.length() > 0) {
            if (isNeg) {
                return (-1 * getNumber(actualSymbol.toString().trim()));
            } else {
                return getNumber(actualSymbol.toString().trim());
            }
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
}
