/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.LinkedList;

/**
 *
 * @author Vláďa
 */
public class Subformula {

    // TODO implment imp
    
    public enum FormulaType {

        OR, AND, IMP, LITERAL, NONE
    }
    
    final private LinkedList<Subformula> subformulas = new LinkedList<>();
    int literal = 0;
    private FormulaType type;

    public Subformula() {
    }

    public Subformula(FormulaType type) {
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
        else if (type == FormulaType.IMP) {
           if (subformulas.size() > 1) {
               subformulas.get(1).negate();
           }
        }
        for (Subformula subformula : subformulas) {
            if (subformula != null) {
                subformula.negate();
            }
        }
    }
    
    public void add(Subformula s) {
        subformulas.add(s);
    }
    
    public LinkedList<Subformula> getSubformulas() {
        return subformulas;
    }

    public int getLiteral() {
        return literal;
    }

    public void setLiteral(int literal) {
        this.literal = literal;
    }

    public FormulaType getType() {
        return type;
    }

    public void setType(FormulaType type) {
        this.type = type;
    }
    
    
}
