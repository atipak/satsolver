/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Vláďa
 */
public class Gsat implements SolverType {

    private List<Integer[]> clauses;
    private HashMap<Integer, Boolean> assignment = new HashMap<>();
    private Set<Integer> variables = new HashSet<>();
    private HashMap<Integer, LinkedList<Integer>> containing = new HashMap<>();
    private Random rnd = new Random();
    private int maxFlips;
    private int maxTries;
    private Statistics stats;

    private class Benefit {

        private int variable;
        private int benefit;

        public Benefit(int variable, int benefit) {
            this.variable = variable;
            this.benefit = benefit;
        }

    }

    public Gsat() {

    }

    @Override
    public Statistics solve(List<Integer[]> clauses, SolverProperties prop) {
        this.clauses = clauses;
        this.maxFlips = prop.getMaxFlips();
        this.maxTries = prop.getMaxTries();
        stats = new Statistics();
        for (int i = 0; i < clauses.size(); i++) {
            Integer[] clause = clauses.get(i);
            for (Integer literal : clause) {
                int var = Math.abs(literal);
                if (!variables.contains(var)) {
                    variables.add(var);
                }
                if (!containing.containsKey(var)) {
                    containing.put(var, new LinkedList<>());
                } else {
                    containing.get(var).add(i);
                }
            }
        }
        for (int i = 0; i < maxTries; i++) {
            randomAssignment();
            int satisfied = computeSatisfied();
            for (int j = 0; j < maxFlips; j++) {
                Benefit ben = getBestVariable();
                if (assignment.get(ben.variable)) {
                    assignment.put(ben.variable, false);
                } else {
                    assignment.put(ben.variable, true);
                }
                satisfied += ben.benefit;
                if (satisfied == clauses.size()) {
                    return stats.setAssignment(assignment);
                }
            }
        }
        return null;
    }

    private void randomAssignment() {
        assignment.clear();
        for (Integer var : variables) {
            assignment.put(var, rnd.nextBoolean());
        }
    }

    private Benefit getBestVariable() {
        int variable = Integer.MIN_VALUE;
        int benefit = Integer.MIN_VALUE;
        for (Integer var : variables) {
            int ben = calculateBenefit(var);
            if (ben > benefit) {
                benefit = ben;
                variable = var;
            }
        }
        return new Benefit(variable, benefit);
    }

    private int calculateBenefit(int variable) {
        int benefit = 0;
        for (Integer index : containing.get(variable)) {
            boolean satisfied = false;
            int searchedLiteral = 0;
            for (Integer literal : clauses.get(index)) {
                int var = Math.abs(literal);
                if (var == variable) {
                    searchedLiteral = literal;
                }
                if (var != variable && (literal < 0 && !assignment.get(var))
                        || literal > 0 && assignment.get(var)) {
                    satisfied = true;
                    break;
                }
            }
            if (!satisfied) {
                if ((searchedLiteral < 0 && !assignment.get(variable))
                        || searchedLiteral > 0 && assignment.get(variable)) {
                    benefit--;
                } else {
                    benefit++;
                }
            }
        }
        return benefit;
    }

    private int computeSatisfied() {
        int satCount = 0;
        for (int i = 0; i < clauses.size(); i++) {
            Integer[] clause = clauses.get(i);
            for (Integer literal : clause) {
                int var = Math.abs(literal);
                if ((literal < 0 && !assignment.get(var))
                        || literal > 0 && assignment.get(var)) {
                    satCount++;
                    break;
                }
            }
        }
        return satCount;
    }

    public void setMaxFlips(int maxFlips) {
        this.maxFlips = maxFlips;
    }

    public void setMaxTries(int maxTries) {
        this.maxTries = maxTries;
    }

}
