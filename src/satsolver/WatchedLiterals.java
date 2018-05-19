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
public class WatchedLiterals extends AbstractWatchStructure implements ClauseStructure {

    /**
     * Constructor for this class.
     *
     * @param clauses Clauses in formula.
     */
    public WatchedLiterals(List<Integer[]> clauses) {
        inicialize(clauses);
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
        List<Point> copyToCls = new LinkedList<>();
        // iterates over all references (pointers) from "actualVariable"
        for (Iterator<Point> iterator = cls.iterator(); iterator.hasNext();) {
            Point next = iterator.next();
            visitedCount++;
            Integer[] clause = clauses.get(next.x);
            if ((!actualAssignment && clause[next.y] < 0)
                    || (actualAssignment && clause[next.y] > 0)) {
                // everything OK, do nothing, clause satisfied, appropriate assignment
            } else {
                int ind = findUnassignedVariable(clause, next.x, assignment);
                // this clause is empty, all variables have bad sign
                if (ind == -1) {
                    throw new EmptyClause(String.valueOf(next.x));
                }
                // this clause is already satisfied by another variable
                if (ind == -2) {
                    continue;
                }
                int var = Math.abs(clause[ind]);
                // returned pointer alredy denote pointer in pointers -> only if both pointers in clause denote the same literal -> unit clause
                if (pointers.get(var).contains(new Point(next.x, ind))) {
                    addUnitClause(new Point(next.x, ind));
                } else {
                    // removed actual pointer, this literal cannot be satisfied and therefore it is not more watched
                    if (var == actualVariable) {
                        copyToCls.add(new Point(next.x, ind));
                    } else {
                        pointers.get(var).add(new Point(next.x, ind));
                    }
                    iterator.remove();
                }

            }
        }
        cls.addAll(copyToCls);
    }

    /**
     * Creates pointers from given clause. Last and first literal in clause.
     *
     * @param clause New added clause
     * @param index Index of clause in list of clauses.
     */
    @Override
    protected void checkClause(Integer[] clause, int index) {
        for (int i = 0; i < clause.length; i++) {
            // add empty list for variables which are not contained in pointers
            if (!pointers.containsKey(Math.abs(clause[i]))) {
                pointers.put(Math.abs(clause[i]), new LinkedList<>());
            }
            if (i == 0 || i == clause.length - 1) {
                Point pointer = new Point(index, i);
                pointers.get(Math.abs(clause[i])).add(pointer);
                if (clause.length == 1) {
                    addUnitClause(pointer);
                }
            }
        }
    }

    /**
     * If there is unassigned variable, returns its index.
     * @param clause 
     * @param indexOfClause index of clause in clauses
     * @param assignment current assignment
     * @return -2 .. satisfied clause, -1 .. empty clause, if satisfied clause then index of satisfied variable, one of indeces which is not in pointers yet
     */
    private int findUnassignedVariable(Integer[] clause, int indexOfClause, HashMap<Integer, Boolean> assignment) {
        boolean satisfied = false;
        int[] indices = new int[2];
        int variablesCount = 0;
        int satisfiedIndex = 0;
        for (int i = 0; i < clause.length; i++) {
            // check if variable on position i is assigned
            // if it's -> checks if has good value -> this clause is satisfied
            // if it isn't -> adds it to unassigned variables max 2 variables
            if (!assignment.containsKey(Math.abs(clause[i]))) {
                if (variablesCount == 2) {
                    continue;
                } else {
                    indices[variablesCount] = i;
                    variablesCount++;
                }
            } else {
                if ((clause[i] < 0 && !assignment.get(Math.abs(clause[i])))
                        || (clause[i] > 0 && assignment.get(Math.abs(clause[i])))) {
                    satisfied = true;
                    satisfiedIndex = i;
                }
            }
        }
        // there are at least two unassigned variables
        if (variablesCount == 2) {
            // returns index which isn't in pointers yet (attention: return alredy inserted index causes adding clause into unit clauses)
            if (!pointers.get(Math.abs(clause[indices[0]])).contains(new Point(indexOfClause, indices[0]))) {
                return indices[0];
            } else if (!pointers.get(Math.abs(clause[indices[1]])).contains(new Point(indexOfClause, indices[1]))) {
                return indices[1];
            } else {
                // this should be always true
                if (!assignment.keySet().contains(Math.abs(clause[indices[0]]))) {
                    return indices[0];
                } else if (!assignment.keySet().contains(Math.abs(clause[indices[1]]))) {
                    return indices[1];
                } else {
                    return 0;
                }
            }
        }
        // in pointers there alredy is founded unassigned variable -> return satisfied index (attention: return alredy inserted index causes adding clause into unit clauses)
        if (variablesCount == 1 && satisfied && pointers.get(Math.abs(clause[indices[0]])).contains(new Point(indexOfClause, indices[0]))) {
            return satisfiedIndex;
        }
        // unit clause
        if (variablesCount == 1) {
            return indices[0];
        }
        // no unassigned variables
        // either all satisfied
        if (satisfied) {
            return -2;
        } else {
            // empty clause
            return -1;
        }
    }

}
