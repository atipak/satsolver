/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Vláďa
 */
public class JeroslowWang implements DecisionHeuristic {

    protected class ZeroException extends Exception {

        public ZeroException() {
        }

        public ZeroException(String message) {
            super(message);
        }
        
    }
    
    /**
     * Reference to clauses.
     */
    protected List<Integer[]> clauses;
    /**
     * All literals in clauses.
     */
    protected Set<Integer> allLiterals;
    /**
     * All variables contined in clauses.
     */
    protected Set<Integer> allVariables;
    /**
     * Scores of literals.
     */
    protected double[] litScores;
    /**
     * Count of variables contained in clauses.
     */
    protected int variablesCount;
    /**
     * Was this class already initialized.
     */
    protected boolean initialized = false;
    /**
     * The count of clauses from last method call.
     */
    protected int lastClausesCount;

    /**
     * Init of class.
     * @param clauses reference to clauses.
     * @param variables Count of different variabels in clauses.
     */
    public JeroslowWang(List<Integer[]> clauses, int variables) {
        this.clauses = clauses;
        this.variablesCount = variables;
        allVariables = new HashSet<>();
        allLiterals = new HashSet<>(2 * variables);
        for (int i = 1; i <= variables; i++) {
            allLiterals.add(i);
            allLiterals.add(-1 * i);
        }
        for (int i = 1; i <= variables; i++) {
            allVariables.add(i);
        }
        litScores = new double[2 * variables];
        // scores start on 0
        Arrays.fill(litScores, 0);
        initialized = true;
        lastClausesCount = 0;
    }

    /**
     * Mapping literal -> index in array allLiterals
     * @param literal Required literal
     * @return Index in array
     */
    protected int getLiteralIndex(int literal) {
        if (literal < 0) {
            return variablesCount - 1 + Math.abs(literal);
        } else {
            return literal - 1;
        }
    }

    /**
     * Returns score of required literal.
     * @param literal required literal.
     * @return Score of literal.
     */
    protected double getLiteralScore(int literal) {
        int index = getLiteralIndex(literal);
        return litScores[index];
    }

    /**
     * Increase literal score determined by size of clause which literal is contained in.
     * @param literal required literal
     * @param size Size of clause with literal.
     */
    protected void increaseLiteralScore(int literal, int size) {
        int index = getLiteralIndex(literal);
        litScores[index] += Math.pow(2, -size);
    }

    /**
     * If threre is new clause in clauses, updates literal score.
     */
    protected void updateClausesScores() {
        // update if is needed
        if (lastClausesCount != clauses.size()) {
            for (int i = lastClausesCount; i < clauses.size(); i++) {
                Integer[] clause = clauses.get(i);
                for (Integer literal : clause) {
                    increaseLiteralScore(literal, clause.length);
                }
            }
        }
        lastClausesCount = clauses.size();
    }

    /**
     * Choose literal with maximum score.
     * @param assignedVariables Already assigned variables.
     * @return Literal with maximum score.
     * @throws satsolver.JeroslowWang.ZeroException 
     */
    protected int getMaximum(Set<Integer> assignedVariables) throws ZeroException{
        Set<Integer> unassignedVariables = new HashSet<>(allVariables);
        unassignedVariables.removeAll(assignedVariables);
        double maximum = Double.MIN_VALUE;
        int chosenLiteral = 0;
        if (unassignedVariables.isEmpty()) {
            return 0;
        }
        // iterate over all unassigned variables and find maximum
        for (Integer unassignedVariable : unassignedVariables) {
            if (maximum < getLiteralScore(unassignedVariable)) {
                chosenLiteral = unassignedVariable;
                maximum = getLiteralScore(unassignedVariable);
            }
            if (maximum < getLiteralScore(-unassignedVariable)) {
                chosenLiteral = -unassignedVariable;
                maximum = getLiteralScore(-unassignedVariable);
            }
        }   
        // there is no such literal
        if (chosenLiteral == 0) {
            throw new ZeroException();
        }
        return chosenLiteral;
    }

    /**
     * Returns most appropriate literal by rules from this heuristic. 
     * @param assignedVariables Already assigned variables.
     * @return Best literal for assignment.
     */
    @Override
    public int chooseLiteral(Set<Integer> assignedVariables) {
        if (!initialized) {
            return 0;
        }
        updateClausesScores();
        int maximum = 0;
        try {
            maximum = getMaximum(assignedVariables);
        }
        catch (ZeroException ze) {
            System.out.println("Error in selecting literal");
        }
        return maximum;
    }

}
