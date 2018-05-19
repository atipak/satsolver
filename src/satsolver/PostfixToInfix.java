/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.HashMap;

/**
 *
 * @author Vláďa
 */
public class PostfixToInfix {

    private String formule;
    private HashMap<String, String> conversion = null;
    int index = 0;

    public PostfixToInfix() {
    }

    public PostfixToInfix(String formula) {
        this.formule = formula;
    }

    public PostfixToInfix(String formule, HashMap<String, String> conversion) {
        this.formule = formule;
        this.conversion = conversion;
    }

    public String transformToInfix(String formule, SolverProperties prop) {
        this.formule = formule;
        this.conversion = prop.getConjuction();
        index = 0;
        return getInfix();
    }

    private String getInfix() {
        StringBuilder actualFormula = new StringBuilder();
        StringBuilder actualSymbol = new StringBuilder();
        String connective = null;
        boolean connRead = false;
        boolean firstSubformula = true;
        index++;
        actualFormula.append("(");
        while (formule.length() > index && formule.charAt(index) != ')') {
            char ch = formule.charAt(index);
            switch (ch) {
                case '(':
                    actualSymbol.append(getInfix());
                    break;
                case ' ':
                    if (connRead) {
                        // read variable
                        firstSubformula = false;
                        actualFormula.append(actualSymbol).append(" ");
                        actualFormula.append(connective).append(" ");
                        actualSymbol.delete(0, actualSymbol.length());
                        connective = null;
                    } else {
                        // read connective
                        if (conversion != null && conversion.containsKey(actualSymbol.toString())) {
                            connective = conversion.get(actualSymbol.toString());
                        } else {
                            connective = actualSymbol.toString();
                        }
                        actualSymbol.delete(0, actualSymbol.length());
                        connRead = true;
                    }
                    break;
                default:
                    actualSymbol.append(ch);
                    break;
            }
            index++;
        }
        int le = formule.length();
        if (formule.length() > index && formule.charAt(index) == ')') {
            if (connective != null) {
                actualFormula.delete(0, actualFormula.length());
                actualFormula.append(connective).append(" ").append(actualSymbol);
            } else {
                actualFormula.append(actualSymbol).append(")");
            }
            actualSymbol.delete(0, actualSymbol.length());
        }
        return actualFormula.toString();
    }
}
