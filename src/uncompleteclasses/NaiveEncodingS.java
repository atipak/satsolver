/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uncompleteclasses;

import java.util.HashMap;
import java.util.LinkedList;
import satsolver.EncodingType;
import satsolver.SolverProperties;
import satsolver.Subformula;

/**
 *
 * @author Vláďa
 */
public class NaiveEncodingS implements EncodingType {

    private Subformula formule;
    int index = 0;
    private HashMap<String, Integer> mapovani = new HashMap<>();
    private int nNumber = 1;

    public NaiveEncodingS(Subformula formule) {
        this.formule = formule;
    }

    public NaiveEncodingS() {
    }

    @Override
    public LinkedList<Integer[]> encode(String formule, SolverProperties prop) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashMap<String, Boolean> retroTransformation(HashMap<Integer, Boolean> assignment) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getNumberOfFormulas() {
        while (checkCNF(formule)) {
            formule = distribute(formule);
        }
        return countSubFormulas(formule);
    }

    public Subformula getNaiveRepresentation(Subformula s) {
        this.formule = s;
        index = 0;
        mapovani = new HashMap<>();
        nNumber = 1;
        while (checkCNF(formule)) {
            formule = distribute(formule);
        }
        return formule;
    }

    private Subformula distribute(Subformula formula) {
        // (a and b) or (c and d) | a or (b and c) | (a and b) or c 
        if (formula.getType() == Subformula.FormulaType.OR
                && formula.getSubformulas().size() > 1
                && (formula.getSubformulas().get(0).getType() == Subformula.FormulaType.AND || formula.getSubformulas().get(0).getType() == Subformula.FormulaType.LITERAL)
                && (formula.getSubformulas().get(1).getType() == Subformula.FormulaType.AND || formula.getSubformulas().get(1).getType() == Subformula.FormulaType.LITERAL)
                && !(formula.getSubformulas().get(0).getType() == Subformula.FormulaType.LITERAL || formula.getSubformulas().get(1).getType() == Subformula.FormulaType.LITERAL)) {
            if (formula.getSubformulas().get(0).getType() == Subformula.FormulaType.AND && formula.getSubformulas().get(1).getType() == Subformula.FormulaType.AND) {

                Subformula numberOne = getNewSubformula(formula.getSubformulas().get(0).getSubformulas().get(0), formula.getSubformulas().get(1).getSubformulas().get(0), Subformula.FormulaType.OR);
                Subformula numberTwo = getNewSubformula(formula.getSubformulas().get(0).getSubformulas().get(0), formula.getSubformulas().get(1).getSubformulas().get(1), Subformula.FormulaType.OR);
                Subformula numberThree = getNewSubformula(formula.getSubformulas().get(0).getSubformulas().get(1), formula.getSubformulas().get(1).getSubformulas().get(0), Subformula.FormulaType.OR);
                Subformula numberFour = getNewSubformula(formula.getSubformulas().get(0).getSubformulas().get(1), formula.getSubformulas().get(1).getSubformulas().get(1), Subformula.FormulaType.OR);
                Subformula numberFive = getNewSubformula(numberOne, numberTwo, Subformula.FormulaType.AND);
                Subformula numberSix = getNewSubformula(numberThree, numberFour, Subformula.FormulaType.AND);
                formula = getNewSubformula(numberFive, numberSix, Subformula.FormulaType.AND);
            } else if ((formula.getSubformulas().get(0).getType() == Subformula.FormulaType.AND && formula.getSubformulas().get(1).getType() == Subformula.FormulaType.LITERAL)
                    || (formula.getSubformulas().get(0).getType() == Subformula.FormulaType.LITERAL && formula.getSubformulas().get(1).getType() == Subformula.FormulaType.AND)) {
                int andFormula = 0;
                int litFormula = 1;
                if (formula.getSubformulas().get(0).getType() == Subformula.FormulaType.LITERAL && formula.getSubformulas().get(1).getType() == Subformula.FormulaType.AND) {
                    andFormula = 1;
                    litFormula = 0;
                }
                Subformula numberOne = getNewSubformula(formula.getSubformulas().get(andFormula).getSubformulas().get(0), formula.getSubformulas().get(litFormula), Subformula.FormulaType.OR);
                Subformula numberTwo = getNewSubformula(formula.getSubformulas().get(andFormula).getSubformulas().get(1), formula.getSubformulas().get(litFormula), Subformula.FormulaType.OR);
                formula = getNewSubformula(numberOne, numberTwo, Subformula.FormulaType.AND);

            }

        }
        if (formula.getSubformulas().size() > 0) {
            formula.getSubformulas().remove(0);
            formula.getSubformulas().add(0, distribute(formula.getSubformulas().get(0)));
        }
        if (formula.getSubformulas().size() > 1) {
            formula.getSubformulas().remove(1);
            formula.getSubformulas().add(1, distribute(formula.getSubformulas().get(1)));
        }
        return formula;
    }

    private int countSubFormulas(Subformula form) {
        int sum = 0;

        if (form.getSubformulas().size() > 0 && form.getSubformulas().get(0) != null) {
            sum += countSubFormulas(form.getSubformulas().get(0));
        }
        if (form.getSubformulas().size() > 1 && form.getSubformulas().get(1) != null) {
            sum += countSubFormulas(form.getSubformulas().get(1));
        }
        if (form.getType() == Subformula.FormulaType.OR) {
            sum++;
        }
        return sum;
    }

    private void formWrite(Subformula form) {
        stringWrite(form);
        System.out.println("");
    }

    private void stringWrite(Subformula form) {
        System.out.print("(");
        for (int i = 0; i < 2; i++) {
            if (form.getSubformulas().size() > i) {
                if (form.getSubformulas().get(i).getType() == Subformula.FormulaType.LITERAL) {
                    System.out.print(form.getSubformulas().get(i).getLiteral());
                } else {
                    stringWrite(form.getSubformulas().get(i));
                }
            }
            if (i == 0) {
                System.out.print(" " + form.getType() + " ");
            }
        }
        System.out.print(")");
    }

    private boolean checkCNF(Subformula formula) {
        if (formula.getType() == Subformula.FormulaType.OR && formula.getSubformulas().size() > 1 && (formula.getSubformulas().get(0).getType() == Subformula.FormulaType.AND || formula.getSubformulas().get(0).getType() == Subformula.FormulaType.LITERAL) && (formula.getSubformulas().get(1).getType() == Subformula.FormulaType.AND || formula.getSubformulas().get(1).getType() == Subformula.FormulaType.LITERAL)) {
            if (formula.getSubformulas().get(0).getType() == Subformula.FormulaType.AND && formula.getSubformulas().get(1).getType() == Subformula.FormulaType.AND) {
                return true;
            } else if ((formula.getSubformulas().get(0).getType() == Subformula.FormulaType.AND && formula.getSubformulas().get(1).getType() == Subformula.FormulaType.LITERAL)
                    || (formula.getSubformulas().get(0).getType() == Subformula.FormulaType.LITERAL && formula.getSubformulas().get(1).getType() == Subformula.FormulaType.AND)) {
                return true;
            } else if ((formula.getSubformulas().get(0).getType() == Subformula.FormulaType.AND && formula.getSubformulas().get(1).getType() == Subformula.FormulaType.OR)
                    || (formula.getSubformulas().get(0).getType() == Subformula.FormulaType.OR && formula.getSubformulas().get(1).getType() == Subformula.FormulaType.AND)) {
                return true;
            }
        }
        for (int i = 0; i < 2; i++) {
            if (formula.getSubformulas().size() > i && checkCNF(formula.getSubformulas().get(i))) {
                return true;
            }
        }
        return false;
    }

    private Subformula getNewSubformula(Subformula left, Subformula right, Subformula.FormulaType ft) {
        Subformula sf = new Subformula(ft);
        if (left != null) {
            sf.getSubformulas().add(left);
        }
        if (right != null) {
            sf.getSubformulas().add(right);
        }
        return sf;
    }
}
