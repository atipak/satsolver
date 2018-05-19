/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Vláďa
 */
public abstract class AbstractWatchStructure implements ClauseStructure {

    /**
     * Count of clauses which have had to be checked after an assigment was made.
     */
    public long visitedCount = 0;
    /**
     * Hashmap for remembering location of variables in clauses. 
     * integer: variable label
     * Point: x: index of clause in clauses, y: index of variable in clause
     */
    protected HashMap<Integer, List<Point>> pointers = new HashMap<>();
    /**
     * List of clauses in formula.
     */
    protected List<Integer[]> clauses;
    /**
     * Actual unit clauses in formula for given assigment. 
     */
    protected LinkedList<Point> unitClauses = new LinkedList<>();
    /**
     * Ratio, which is used for decision, if "clauses" list should be arrayList (< ratio) or linkedList. 
     */
    protected final double ratio = 0.7;

    /**
     * Adds the new clause in data structure clauses and if the clause is a unit clause then adds it in unitClauses as well. 
     * @param clause Clause which should be added.
     * @param index If not -1, then is expected that index marks position of literal in clause, which is deteermines unit clause.
     */
    @Override
    public void addNewClause(Integer[] clause, int index) {
        clauses.add(clause);
        checkClause(clause, clauses.size() - 1);
        if (index > -1) {
            addUnitClause(new Point(clauses.size() - 1, index));
        }
    }
    
    /**
     * Checks if given clause is unit clause by actual assignment and if it is, then added it into unitClauses structure. 
     * @param clause Checked clause.
     * @param index Index of clause in clauses. 
     * @param assignment Actual assignment.
     * @param ignoredVariable Variable which should be ignored. Eg. the variable has an assignment, but this assign. will be removed later. 
     * @return true if clause is an unit clause
     */
    @Override
    public boolean checkUnitClause(Integer[] clause, int index, HashMap<Integer, Boolean> assignment, int ignoredVariable) {
        int countUnsatisfied = 0;
        int unitIndex = -1;
        for (int i = 0; i < clause.length; i++) {
            Integer var = Math.abs(clause[i]);
            // variable's assigment is ignored -> will be removed later
            if (var == ignoredVariable && assignment.containsKey(var)) {
                countUnsatisfied++;
                unitIndex = i;
                continue;
            }
            if (!assignment.containsKey(var)) {
                countUnsatisfied++;
                unitIndex = i;
            }
        }
        if (countUnsatisfied == 1) {
            addUnitClause(new Point(index, unitIndex));
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds new pointers on variables (by used structure definition) into pointers structure. 
     * @param clause Checked clause. 
     * @param index Index of clause in the structure clauses. 
     */
    protected abstract void checkClause(Integer[] clause, int index);

    /**
     * If the structure unitClauses doesn't alrady contain pointer on an unit clause, adds it there.
     * @param pointer Pointer which should be added. 
     */
    protected void addUnitClause(Point pointer) {
        if (!unitClauses.contains(pointer)) {
            unitClauses.add(pointer);
        }
    }

    /**
     * Returns first unit clause from the structure unitClauses.
     * @return Pointer on the unit clause.
     */
    @Override
    public Point getUnitClauseVariable() {
        return unitClauses.pollFirst();
    }

    /**
     * Chech if the structure unitClauses is empty or not.
     * @return true if size of unitClauses structure is bigger than 0.
     */
    @Override
    public boolean hasUnitClause() {
        return !unitClauses.isEmpty();
    }

    /**
     * Clears the unitClauses structure.
     */
    @Override
    public void reset() {
        unitClauses.clear();
    }

    /**
     * Checks if solution was found.
     * @param usedVariables Variables with assignment. 
     * @return 
     */
    @Override
    public boolean allSatisfied(Set<Integer> usedVariables) {
        return usedVariables.size() == pointers.size();
    }

    /**
     * Chooses more appropriate list type for clauses structure for saving clauses in formula.
     * @param steps Count of steps made until now
     * @param stepsFromLast Count of steps made from last method call until now
     * @param newClauses Count of clauses which were added into clauses structure unitl now
     * @param newClausesFromLast Count of clauses which were added into clauses structure from last merhod call unitl now
     */
    @Override
    public void checkListType(int steps, int stepsFromLast, int newClauses, int newClausesFromLast) {
        if (newClauses / (double) steps > ratio) {
            if (!clauses.getClass().equals(LinkedList.class)) {
                clauses = new LinkedList<>(clauses);
            }
        } else {
            if (!clauses.getClass().equals(ArrayList.class)) {
                clauses = new ArrayList<>(clauses);
            }
        }
    }

    /**
     * Returns reference on clauses strukture.
     * @return Actual reference
     */
    @Override
    public List<Integer[]> getClauses() {
        return clauses;
    }

    /**
     * Returns the count of clauses which the structure had to check after an assigment was made until now. Content of variable visitedCount. 
     * @return Count of checked clauses
     */
    @Override
    public long getCheckedClauses() {
        return visitedCount;
    }

    /**
     * Initializes variables.
     * @param clauses Clauses in formula.
     */
    protected void inicialize(List<Integer[]> clauses) {
        this.clauses = new ArrayList<>(clauses);
        for (int l = 0; l < clauses.size(); l++) {
            checkClause(clauses.get(l), l);
        }
    }

}
