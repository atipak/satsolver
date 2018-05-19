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
public class Statistics {

    private int steps;
    private int addedClauses;
    private int conflicts;
    private long beginTime;
    private long endTime;
    private HashMap<Integer, Boolean> assignment;
    private boolean success;
    private int decisionCount;
    private long checkedClause;
    private long unitPropagationCount;

    public Statistics() {
        steps = 0;
        unitPropagationCount = 0;
        addedClauses = 0;
        conflicts = 0;
        beginTime = 0;
        endTime = 0;
        assignment = null;
        success = false;
        decisionCount = 0;
        checkedClause = 0;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getAddedClauses() {
        return addedClauses;
    }

    public void setAddedClauses(int addedClauses) {
        this.addedClauses = addedClauses;
    }

    public int getConflicts() {
        return conflicts;
    }

    public void setConflicts(int conflicts) {
        this.conflicts = conflicts;
    }

    public void incrementSteps() {
        steps++;
    }

    public void incrementAddedClauses() {
        addedClauses++;
    }

    public void incrementConflicts() {
        conflicts++;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getRunTime() {
        long actEndTime;
        if (this.endTime == 0) {
            actEndTime = System.currentTimeMillis();
        } else {
            actEndTime = this.endTime;
        }
        return (actEndTime - this.beginTime) / 1000;
    }

    public String getFormatedRunTime() {
        long actEndTime;
        if (this.endTime == 0) {
            actEndTime = System.currentTimeMillis();
        } else {
            actEndTime = this.endTime;
        }
        long all = (actEndTime - this.beginTime) / 1000;
        long sec = all % 60;
        long secRest = all / 60;
        long min = secRest % 60;
        long minRest = secRest / 60;
        long hours = minRest % 60;
        return ("Hours: " + hours + ", minutes: " + min + ", seconds: " + sec);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public HashMap<Integer, Boolean> getAssignment() {
        return assignment;
    }

    public Statistics setAssignment(HashMap<Integer, Boolean> assignment) {
        this.assignment = assignment;
        return this;
    }

    public void printStatistics() {
        printAverageStatistics(1);
    }

    public void printAverageStatistics(double divisor) {
        System.out.println("-----------------------------------");
        System.out.println("Steps: " + this.getSteps() / divisor);
        System.out.println("Conflicts: " + this.getConflicts() / divisor);
        System.out.println("New clauses: " + this.getAddedClauses() / divisor);
        System.out.println("Unit propagation count: " + this.getUnitPropagationCount() / divisor);
        System.out.println("Decision count: " + this.getDecisionCount() / divisor);
        System.out.println("Checked clauses: " + this.getCheckedClause() / divisor);
        System.out.println("Run time: " + this.getFormatedRunTime());
        System.out.println("-----------------------------------");
    }
    
    public void printAverageStatisticsInRow(double divisor) {
        System.out.print(this.getSteps() / divisor);
        System.out.print(";" + this.getConflicts() / divisor);
        System.out.print(";" + this.getAddedClauses() / divisor);
        System.out.print(";" + this.getUnitPropagationCount() / divisor);
        System.out.print(";" + this.getDecisionCount() / divisor);
        System.out.print(";" + this.getCheckedClause() / divisor);
        System.out.print(";" + this.getRunTime());
    }
    
    public int getDecisionCount() {
        return decisionCount;
    }

    public void setDecisionCount(int decisionCount) {
        this.decisionCount = decisionCount;
    }

    public long getCheckedClause() {
        return checkedClause;
    }

    public void setCheckedClause(long checkedClause) {
        this.checkedClause = checkedClause;
    }

    public void incrementCheckedClauses() {
        checkedClause++;
    }

    public void incrementDecisionCount() {
        decisionCount++;
    }

    public long getUnitPropagationCount() {
        return unitPropagationCount;
    }

    public void setUnitPropagationCount(long unitPropagationCount) {
        this.unitPropagationCount = unitPropagationCount;
    }

    public void incrementUnitPropagationCount() {
        unitPropagationCount++;
    }
}
