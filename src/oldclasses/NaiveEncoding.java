/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oldclasses;

import java.util.HashMap;
import java.util.LinkedList;
import satsolver.EncodingType;
import satsolver.SolverProperties;

/**
 *
 * @author Vláďa
 */
public class NaiveEncoding implements EncodingType {

    private String formule;
    int index = 0;
    private HashMap<String, Integer> mapovani = new HashMap<>();
    private int nNumber = 1;
    private int distroCount = 0;
    private boolean debugPrint = false;

    @Override
    public LinkedList<Integer[]> encode(String formule, SolverProperties prop) {
        this.formule = formule;
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashMap<String, Boolean> retroTransformation(HashMap<Integer, Boolean> assignment) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    enum FormulaType {

        OR, AND, LITERAL, NONE
    }

    private class SubFormula {

        SubFormula[] subformulas = new SubFormula[2];
        int literal = 0;
        FormulaType type;

        public SubFormula() {
        }

        public SubFormula(FormulaType type) {
            this.type = type;
        }

        public void negate() {
            if (type == FormulaType.AND) {
                type = FormulaType.OR;
            } else if (type == FormulaType.OR) {
                type = FormulaType.AND;
            } else if (type == FormulaType.LITERAL) {
                literal *= -1;
            }
            for (SubFormula subformula : subformulas) {
                if (subformula != null) {
                    subformula.negate();
                }
            }
        }
    }

    public NaiveEncoding() {
        this.formule = formule;
    }

    public int getNumberOfFormulas(String formule) {
        this.formule = formule;
        // decomposing formula = creating logic tree of subformulas
        SubFormula root = decomposeFormula();
        System.out.println("Literals count: " + countSubFormulas(root));
        //formWrite(root);
        while (checkCNF(root)) {
            root = distributeTrivial(root);
            if (debugPrint) {
                System.out.println("*************************************************************");
            }
        }
        System.out.println("Distribution processed: " + distroCount);
        //formWrite(root);
        return countSubFormulas(root);
    }

    private SubFormula decomposeFormula() {
        try {
            assert (formule.charAt(index) == '(');
            boolean isNeg = false;
            StringBuilder actualSymbol = new StringBuilder();
            SubFormula me = new SubFormula(FormulaType.NONE);
            int actSubFormula = 0;
            int sym;
            index++;
            while (formule.length() > index && formule.charAt(index) != ')') {
                char ch = formule.charAt(index);
                switch (ch) {
                    case '(':
                        SubFormula sf = decomposeFormula();
                        //formWrite(sf);
                        if (isNeg) {
                            sf.negate();
                        }
                        isNeg = false;
                        me.subformulas[actSubFormula] = sf;
                        actSubFormula++;
                        break;
                    case ' ':
                        break;
                    case '-':
                        isNeg = true;
                        break;
                    case '&':
                        if (me.type == FormulaType.NONE) {
                            me.type = FormulaType.AND;
                        } else {
                            // vyhod chybu
                        }
                        break;
                    case '|':
                        if (me.type == FormulaType.NONE) {
                            me.type = FormulaType.OR;
                        } else {
                            // vyhod chybu
                        }
                        break;
                    default:
                        actualSymbol.append(ch);
                        break;
                }
                if (ch == ' ' || ch == '|' || ch == '&') {
                    sym = retSymbol(actualSymbol, isNeg);
                    if (sym != 0) {
                        SubFormula sf = new SubFormula(FormulaType.LITERAL);
                        sf.literal = sym;
                        actualSymbol.delete(0, actualSymbol.length());
                        isNeg = false;
                        me.subformulas[actSubFormula] = sf;
                        actSubFormula++;
                    }
                }
                if (formule.charAt(index) != ')') {
                    index++;
                }
            }
            if (formule.length() > index && formule.charAt(index) == ')') {
                sym = retSymbol(actualSymbol, isNeg);
                if (sym != 0) {
                    SubFormula sf = new SubFormula(FormulaType.LITERAL);
                    sf.literal = sym;
                    actualSymbol.delete(0, actualSymbol.length());
                    isNeg = false;
                    me.subformulas[actSubFormula] = sf;
                    actSubFormula++;
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
        if (mapovani.containsKey(variable)) {
            return mapovani.get(variable);
        } else {
            nNumber++;
            mapovani.put(variable, nNumber - 1);
            return nNumber - 1;
        }
    }

    private SubFormula distribute(SubFormula formula) {
        // (a and b) or (c and d) | a or (b and c) | (a and b) or c 
        if (formula.type == FormulaType.OR
                && formula.subformulas[0] != null
                && formula.subformulas[1] != null
                && (formula.subformulas[0].type == FormulaType.AND || formula.subformulas[0].type == FormulaType.LITERAL)
                && (formula.subformulas[1].type == FormulaType.AND || formula.subformulas[1].type == FormulaType.LITERAL)
                && !(formula.subformulas[0].type == FormulaType.LITERAL || formula.subformulas[1].type == FormulaType.LITERAL)) {
            if (debugPrint) {
                System.out.print("Origin formula: ");
                formWrite(formula);
                System.out.print("Left formula: ");
                formWrite(formula.subformulas[0]);
                System.out.print("Right formula: ");
                formWrite(formula.subformulas[1]);
            }
            distroCount++;
            if (formula.subformulas[0].type == FormulaType.AND && formula.subformulas[1].type == FormulaType.AND) {
                if (debugPrint) {
                    System.out.println("And transformation");
                }
                SubFormula numberOne = getNewSubformula(formula.subformulas[0].subformulas[0], formula.subformulas[1].subformulas[0], FormulaType.OR);
                SubFormula numberTwo = getNewSubformula(formula.subformulas[0].subformulas[0], formula.subformulas[1].subformulas[1], FormulaType.OR);
                SubFormula numberThree = getNewSubformula(formula.subformulas[0].subformulas[1], formula.subformulas[1].subformulas[0], FormulaType.OR);
                SubFormula numberFour = getNewSubformula(formula.subformulas[0].subformulas[1], formula.subformulas[1].subformulas[1], FormulaType.OR);
                SubFormula numberFive = getNewSubformula(numberOne, numberTwo, FormulaType.AND);
                SubFormula numberSix = getNewSubformula(numberThree, numberFour, FormulaType.AND);
                formula = getNewSubformula(numberFive, numberSix, FormulaType.AND);
            } else if ((formula.subformulas[0].type == FormulaType.AND && formula.subformulas[1].type == FormulaType.LITERAL)
                    || (formula.subformulas[0].type == FormulaType.LITERAL && formula.subformulas[1].type == FormulaType.AND)) {
                if (debugPrint) {
                    System.out.println("Basic transformation");
                }
                int andFormula = 0;
                int litFormula = 1;
                if (formula.subformulas[0].type == FormulaType.LITERAL && formula.subformulas[1].type == FormulaType.AND) {
                    andFormula = 1;
                    litFormula = 0;
                }
                SubFormula numberOne = getNewSubformula(formula.subformulas[andFormula].subformulas[0], formula.subformulas[litFormula], FormulaType.OR);
                SubFormula numberTwo = getNewSubformula(formula.subformulas[andFormula].subformulas[1], formula.subformulas[litFormula], FormulaType.OR);
                formula = getNewSubformula(numberOne, numberTwo, FormulaType.AND);

            }
            if (debugPrint) {
                System.out.print("New formula: ");
                formWrite(formula);
                System.out.print("New left formula: ");
                formWrite(formula.subformulas[0]);
                System.out.print("New right formula: ");
                formWrite(formula.subformulas[1]);
            }
        }
        if (formula.subformulas[0] != null) {
            formula.subformulas[0] = distribute(formula.subformulas[0]);
        }
        if (formula.subformulas[1] != null) {
            formula.subformulas[1] = distribute(formula.subformulas[1]);
        }
        return formula;
    }

    private SubFormula distributeTrivial(SubFormula formula) {
        // a or (b and c) | (a and b) or c 
        if (formula.type == FormulaType.OR
                && formula.subformulas[0] != null
                && formula.subformulas[1] != null
                && (formula.subformulas[0].type == FormulaType.AND || formula.subformulas[1].type == FormulaType.AND)) {
            if (debugPrint) {
                System.out.print("Origin formula: ");
                formWrite(formula);
                System.out.print("Left formula: ");
                formWrite(formula.subformulas[0]);
                System.out.print("Right formula: ");
                formWrite(formula.subformulas[1]);
            }
            distroCount++;
            int andFormula = 0;
            int anotherFormula = 1;
            if (formula.subformulas[1].type == FormulaType.AND) {
                andFormula = 1;
                anotherFormula = 0;
            }
            SubFormula numberOne = getNewSubformula(formula.subformulas[andFormula].subformulas[0], formula.subformulas[anotherFormula], FormulaType.OR);
            SubFormula numberTwo = getNewSubformula(formula.subformulas[andFormula].subformulas[1], formula.subformulas[anotherFormula], FormulaType.OR);
            formula = getNewSubformula(numberOne, numberTwo, FormulaType.AND);
            if (debugPrint) {
                System.out.print("New formula: ");
                formWrite(formula);
                System.out.print("New left formula: ");
                formWrite(formula.subformulas[0]);
                System.out.print("New right formula: ");
                formWrite(formula.subformulas[1]);
            }
        }
        if (formula.subformulas[0] != null) {
            formula.subformulas[0] = distributeTrivial(formula.subformulas[0]);
        }
        if (formula.subformulas[1] != null) {
            formula.subformulas[1] = distributeTrivial(formula.subformulas[1]);
        }
        return formula;
    }

    private int countSubFormulas(SubFormula form) {
        int sum = 0;
        int sumLit = 0;
        if (form.subformulas[0] != null) {
            sumLit += countSubFormulas(form.subformulas[0]);
        }
        if (form.subformulas[1] != null) {
            sumLit += countSubFormulas(form.subformulas[1]);
        }
        if (form.type == FormulaType.OR) {
            sum++;
        }
        if (form.type == FormulaType.LITERAL) {
            sumLit++;
        }
        return sumLit;
    }

    private void formWrite(SubFormula form) {
        stringWrite(form);
        System.out.println("");
    }

    private void stringWrite(SubFormula form) {
        System.out.print("(");
        if (form.subformulas[0] != null) {
            if (form.subformulas[0].type == FormulaType.LITERAL) {
                System.out.print(form.subformulas[0].literal);
            } else {
                stringWrite(form.subformulas[0]);
            }
        }
        System.out.print(" " + form.type + " ");
        if (form.subformulas[1] != null) {
            if (form.subformulas[1].type == FormulaType.LITERAL) {
                System.out.print(form.subformulas[1].literal);
            } else {
                stringWrite(form.subformulas[1]);
            }
        }
        System.out.print(")");
    }

    private boolean checkCNF(SubFormula formula) {
        if (formula.type == FormulaType.OR
                && formula.subformulas[0] != null
                && formula.subformulas[1] != null
                && (formula.subformulas[0].type == FormulaType.AND || formula.subformulas[1].type == FormulaType.AND)) {
            return true;
        }
        if (formula.subformulas[0] != null && checkCNF(formula.subformulas[0])) {
            return true;
        }
        if (formula.subformulas[1] != null && checkCNF(formula.subformulas[1])) {
            return true;
        }
        return false;
    }

    private SubFormula getNewSubformula(SubFormula left, SubFormula right, FormulaType ft) {
        SubFormula sf = new SubFormula(ft);
        sf.subformulas[0] = left;
        sf.subformulas[1] = right;
        return sf;
    }
}
