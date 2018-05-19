/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Vláďa
 */
public class WatchAll extends AbstractWatchStructure implements ClauseStructure {

    /**
     * Constructor for this class.
     *
     * @param clauses Clauses in formula.
     */
    public WatchAll(List<Integer[]> clauses) {
        inicialize(clauses);
    }

    /**
     * Creates pointers from given clause. All literals in clause.
     *
     * @param clause New added clause
     * @param index Index of clause in list of clauses.
     */
    @Override
    protected void checkClause(Integer[] clause, int index) {
        for (int i = 0; i < clause.length; i++) {
            int literal = clause[i];
            int variable = Math.abs(literal);
            // add empty list for variables which are not contained in pointers
            if (!pointers.containsKey(variable)) {
                pointers.put(variable, new LinkedList<>());
            }
            Point pointer = new Point(index, i);
            pointers.get(variable).add(pointer);
            if (clause.length == 1) {
                addUnitClause(pointer);
            }
        }
    }

    /**
     * Launches update of inner variables of clause structure. Can find unit
     * clauses or empty clause.
     *
     * @param assignment Actual assignment
     * @param actualVariable Last assigned variable - without sign.
     * @param actualAssignment Assignment of last assigned variable.
     * @throws EmptyClause If there is contradiction by this assignment
     */
    @Override
    public void update(HashMap<Integer, Boolean> assignment, int actualVariable, boolean actualAssignment) throws EmptyClause {
        actualVariable = Math.abs(actualVariable);
        List<Point> cls = pointers.get(actualVariable);
        for (Iterator<Point> iterator = cls.iterator(); iterator.hasNext();) {
            visitedCount++;
            Point next = iterator.next();
            Integer[] clause = clauses.get(next.x);
            if ((!actualAssignment && clause[next.y] < 0)
                    || (actualAssignment && clause[next.y] > 0)) {
                // everything OK, do nothing, clause satisfied
            } else {
                // check if clause is unit clause
                int unitIndex = isUnitClause(clause, assignment);
                if (unitIndex == -1) {
                    throw new EmptyClause(String.valueOf(next.x));
                }
                else if (unitIndex == -2) {
                    // nothing to do too much unsatisfied literals (variables) in clause
                } else {
                    addUnitClause(new Point(next.x, unitIndex));
                }
            }
        }
    }

    /**
     * Goes through all literals in clause and checks if this clause is a unit clause. 
     * @param clause
     * @param assignment current assignment
     * @return 
     */
    private int isUnitClause(Integer[] clause, HashMap<Integer, Boolean> assignment) {
        boolean isSatisfied = false;
        int unassigned = 0;
        int unassignedIndex = -1;
        int index = 0;
        for (Integer literal : clause) {
            int variable = Math.abs(literal);
            if (!assignment.containsKey(variable)) {
                unassigned++;
                unassignedIndex = index;
                continue;
            }
            if ((Math.signum(literal) > 0 && assignment.get(variable)) || (Math.signum(literal) < 0 && !assignment.get(variable))) {
                isSatisfied = true;
                break;
            }
            index++;
        }
        if (unassigned == 1 && !isSatisfied) {
            return unassignedIndex;
        } else if (!isSatisfied && unassigned == 0) {
            return -1;
        }
        else {
            return -2;
        }
    }
}
