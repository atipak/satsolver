/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Vláďa
 */
public class Solver {

    /**
     * Solver properties from user.
     */
    SolverProperties args = null;
    /**
     * Solver type chosen by user -> CDCL, DPPL, GSAT.
     */
    SolverType solver = null;
    /**
     * Encoding type chosen by user.
     */
    EncodingType encoding = null;
    /**
     * The data are stored in DIMACS style.
     */
    DIMACSReader dimacsr = null;
    /**
     * The data are stored in interactive style.
     */
    Iterator<String> it = null;
    /**
     * The formulas are in postfix order.
     */
    PostfixToInfix infix = null;
    /**
     * Statistics of solving problem.
     */
    Statistics stats;
    /**
     * Count of formulas in the data.
     */
    long formulasCount = 0;
    /**
     * Count of solved problems.
     */
    long solved = 0;
    /**
     * Count of formulas with no solution.
     */
    long unsolved = 0;

    /**
     * Initialization and args processing.
     * @param args Solver properties chosen by user.
     */
    public Solver(SolverProperties args) {
        this.args = args;
        stats = new Statistics();
        processArgs();
    }

    /**
     * Main method for solving formulas. Returns solution if such solution exists.
     * @return Solution if exists or null (nothing to solve)
     * @throws SolverException if something bad in solver happens
     */
    public HashMap<String, Boolean> solveNext() throws SolverException {
        HashMap<String, Boolean> result = null;
        if (hasNextFormula()) {
            formulasCount++;
            Statistics stats = solver.solve(nextFormula(), args);
            if (stats.getAssignment() == null) {
                if (stats.isSuccess()) {
                    unsolved++;
                    System.out.println("Error");
                    serveStatistics(stats);
                    throw new SolverException("Error");
                } else {
                    unsolved++;
                    System.out.println("No result");
                    serveStatistics(stats);
                    return backTransformation(stats.getAssignment());
                }
            } else {
                solved++;
                serveStatistics(stats);
                return backTransformation(stats.getAssignment());
            }
        } else {
            return null;
        }
    }

    /**
     * Set up solver properties defined by user.
     */
    private void processArgs() {
        switch (args.getEncodingType()) {
            //case NAIVE:
            //    encoding = new NaiveEncoding();
            //    break;
            case TSETIN:
                encoding = new TsetinEncoding();
                break;
        }

        switch (args.getSolverType()) {
            case CDCL:
                solver = new CDCL();
                break;
            case DPLL:
                solver = new CDCL();
                break;
            case GSAT:
                solver = new Gsat();
                break;
        }

        switch (args.getInputType()) {
            case CLASSIC:
                it = args.getFormulas().iterator();
                break;
            case DIMACS:
                dimacsr = new DIMACSReader(args.getFilesPath(), args.isPrintFileNames());
                break;
        }

        if (args.getFormuleNotation() == SolverProperties.FormuleNotation.PREFIX) {
            infix = new PostfixToInfix();
        }

    }

    /**
     * Checks if there is next formula for solving.
     * @return True if there exists another formula in qeueu.
     */
    private boolean hasNextFormula() {
        if (dimacsr != null) {
            return dimacsr.hasNextFormula();
        } else {
            return it.hasNext();
        }
    }

    /**
     * Return next formula. Transforms from postfix if it is needed.
     * @return Next formula.
     */
    private LinkedList<Integer[]> nextFormula() {
        if (dimacsr != null) {
            return dimacsr.nextFormula();
        } else {
            String formula = it.next();
            if (infix != null) {
                formula = infix.transformToInfix(formula, args);
            }
            return encoding.encode(formula, args);
        }
    }

    /**
     * Mapping from integers back to names of variables.
     * @param assignment Final assignment.
     * @return Mapped assignment.
     */
    private HashMap<String, Boolean> backTransformation(HashMap<Integer, Boolean> assignment) {
        if (dimacsr != null) {
            HashMap<String, Boolean> result = new HashMap<>();
            if (assignment != null) {
                for (Map.Entry<Integer, Boolean> entrySet : assignment.entrySet()) {
                    Integer key = entrySet.getKey();
                    Boolean value = entrySet.getValue();
                    result.put(String.valueOf(key), value);
                }
            }
            return result;
        } else {
            return encoding.retroTransformation(assignment);
        }
    }

    /**
     * If should print stats.
     * @param stats Solver statistics.
     */
    private void shouldPrintStats(Statistics stats) {
        if (args.isPrintAverageStatistics()) {
            stats.printStatistics();
        }
    }

    /**
     * Print average satistics if it it so set up.
     */
    public void printAverageStatistics() {
        stats.printAverageStatistics(formulasCount);
        System.out.println("Solved: " + solved);
        System.out.println("Unsolved: " + unsolved);
    }
    
    /**
     * Print average satistics in one row if it it so set up.
     */
    public void printAverageStatisticsInRow() {
        stats.printAverageStatisticsInRow(formulasCount);
        System.out.print(";" + solved);
        System.out.print(";" + unsolved);
    }

    /**
     * Adds new statistics to average statistics.
     * @param taskStats Statistics of actual task.
     */
    private void addToAverage(Statistics taskStats) {
        if (stats.getBeginTime() == 0) {
            stats.setBeginTime(System.currentTimeMillis());
        }
        stats.setAddedClauses(stats.getAddedClauses() + taskStats.getAddedClauses());
        stats.setCheckedClause(stats.getCheckedClause() + taskStats.getCheckedClause());
        stats.setConflicts(stats.getConflicts() + taskStats.getConflicts());
        stats.setDecisionCount(stats.getDecisionCount() + taskStats.getDecisionCount());
        stats.setSteps(stats.getSteps() + taskStats.getSteps());
        stats.setUnitPropagationCount(stats.getUnitPropagationCount() + taskStats.getUnitPropagationCount());
        stats.setEndTime(System.currentTimeMillis());
    }

    /**
     * Help method for printing statistics.
     * @param taskStats Actual task statistics.
     */
    private void serveStatistics(Statistics taskStats) {
        addToAverage(taskStats);
        shouldPrintStats(taskStats);
    }
}
