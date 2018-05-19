/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Vláďa
 */
public class LogicTree {

    private String formule;
    private int index = 0;
    private HashMap<String, Integer> mapping = new HashMap<>();
    private int nNumber = 1;
    private Subformula subformula;

    public LogicTree() {
    }

    public Subformula getLogicTree(String formule, HashMap<String, String> conjuction) {
        index = 0;
        this.mapping = new HashMap<>();
        this.formule = formule;
        for (Map.Entry<String, String> entrySet : conjuction.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            this.formule = this.formule.replaceAll(key, value);
        }
        nNumber = 1;
        return decomposeFormula();
    }

    private Subformula decomposeFormula() {
        assert (formule.charAt(index) == '(');
        try {
            boolean isNeg = false;
            StringBuilder actualSymbol = new StringBuilder();
            Subformula me = new Subformula(Subformula.FormulaType.NONE);
            int sym;
            index++;
            while (formule.length() > index && formule.charAt(index) != ')') {
                char ch = formule.charAt(index);
                switch (ch) {
                    case '(':
                        Subformula sf = decomposeFormula();
                        //formWrite(sf);
                        if (isNeg) {
                            sf.negate();
                        }
                        isNeg = false;
                        me.add(sf);
                        break;
                    case ' ':
                        break;
                    case '-':
                        isNeg = true;
                        break;
                    case '&':
                        if (me.getType() == Subformula.FormulaType.NONE) {
                            me.setType(Subformula.FormulaType.AND);
                        } else {
                            // vyhod chybu
                        }
                        break;
                    case '|':
                        if (me.getType() == Subformula.FormulaType.NONE) {
                            me.setType(Subformula.FormulaType.OR);
                        } else {
                            // vyhod chybu
                        }
                        break;
                    case '>':
                        if (me.getType() == Subformula.FormulaType.NONE) {
                            me.setType(Subformula.FormulaType.IMP);
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
                        Subformula sf = new Subformula(Subformula.FormulaType.LITERAL);
                        sf.literal = sym;
                        actualSymbol.delete(0, actualSymbol.length());
                        isNeg = false;
                        me.add(sf);
                    }
                }
                if (formule.charAt(index) != ')') {
                    index++;
                }
            }
            if (formule.length() > index && formule.charAt(index) == ')') {
                sym = retSymbol(actualSymbol, isNeg);
                if (sym != 0) {
                    Subformula sf = new Subformula(Subformula.FormulaType.LITERAL);
                    sf.literal = sym;
                    actualSymbol.delete(0, actualSymbol.length());
                    isNeg = false;
                    me.add(sf);
                }
            }
            index++;
            return me;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
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

    private int getNumber(String variable) {
        if (mapping.containsKey(variable)) {
            return mapping.get(variable);
        } else {
            nNumber++;
            mapping.put(variable, nNumber - 1);
            return nNumber - 1;
        }
    }

    public Subformula disassembleFormula(Subformula s) {
        return null;
    }

    private Subformula disassemble(Subformula s) {
        return null;
    }

    public static void assembleFormula(Subformula s) {
        assemble(s);
    }

    private static void assemble(Subformula s) {
        LinkedList<Subformula> formulasToProceed = new LinkedList<>(s.getSubformulas());
        while (!formulasToProceed.isEmpty()) {
            Subformula subformula = formulasToProceed.removeFirst();
            if (s.getType() == subformula.getType()) {
                s.getSubformulas().remove(subformula);
                LinkedList<Subformula> childes = new LinkedList<>(subformula.getSubformulas());
                for (Subformula childe : childes) {
                    s.add(childe);
                    if (childe.getType() == s.getType()) {
                        formulasToProceed.add(childe);
                    }
                }
            }
        }
        for (Subformula sub : s.getSubformulas()) {
            assemble(sub);
        }
    }

    private HashMap<Integer, ArrayList<String>> layers;

    public void drawLogicTree(Subformula s) {
        layers = new HashMap<>();
        draw(s, 0);
        index = 1;
        while (true) {
            if (!layers.containsKey(index)) {
                break;
            }
            for (String item : layers.get(index)) {
                System.out.print(item + " ");
            }
            System.out.println("");
            index++;
        }
    }

    private void draw(Subformula s, int level) {
        level++;
        if (!layers.containsKey(level)) {
            layers.put(level, new ArrayList<>());
        }
        if (s.getType() != Subformula.FormulaType.LITERAL) {
            layers.get(level).add(s.getType().name());
        } else {
            layers.get(level).add(String.valueOf(s.literal));
        }
        for (Subformula sub : s.getSubformulas()) {
            draw(sub, level);
        }
    }
}
