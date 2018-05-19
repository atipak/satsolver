/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Vláďa
 */
public class Vsids extends JeroslowWang implements DecisionHeuristic {

    /**
     * How often should scores be divided.
     */
    private int period;
    /**
     * The constant for division of scores.
     */
    private double constant;
    /**
     * How many times was method called.
     */
    private int callingCount;

    /**
     * Init for this class.
     *
     * @param clauses Reference to clauses structure.
     * @param variables Variables count.
     * @param period How often should scores be divided.
     * @param constant The constant for division of scores.
     */
    public Vsids(List<Integer[]> clauses, int variables, int period, int constant) {
        super(clauses, variables);
        this.constant = (double) constant;
        this.period = period;
        this.callingCount = 0;
    }

    /**
     * Divides all scores by stored constant.
     */
    private void divideScores() {
        for (int i = 0; i < litScores.length; i++) {
            litScores[i] /= constant;
        }
    }

    /**
     * Scales scores if they are to low.
     */
    private void scaleScores() {
        // find minimum and maximum and scale all values relative to them
        double minimum = Double.MAX_VALUE;
        double maximum = Double.MIN_VALUE;
        for (double literal : litScores) {
            minimum = Math.min(minimum, literal);
            maximum = Math.max(maximum, literal);
        }
        for (int i = 0; i < litScores.length; i++) {
            litScores[i] = scaleScore(minimum, maximum, litScores[i]);
        }
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
        callingCount++;
        if (callingCount % period == 0) {
            divideScores();
        }
        updateClausesScores();
        int maximum = 0;
        try {
            maximum = getMaximum(assignedVariables);
        } catch (ZeroException ze) {
            // there are too much options with low score -> scale scores
            scaleScores();
            try {
                maximum = getMaximum(assignedVariables);
            } catch (ZeroException ze1) {
                System.out.println("Error in selecting literal");
            }
        }
        return maximum;
    }

    /**
     * Scale given score into min and max range.
     * @param minimum
     * @param maximum
     * @param litScore
     * @return 
     */
    private double scaleScore(double minimum, double maximum, double litScore) {
        return (litScore - minimum) / (maximum - minimum);
    }

}
