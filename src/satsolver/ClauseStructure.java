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
public interface ClauseStructure {

    public void update(HashMap<Integer, Boolean> assignment, int actualVariable, boolean actualAssignment) throws EmptyClause;

    public void addNewClause(Integer[] clause, int index);

    public Point getUnitClauseVariable();

    public boolean hasUnitClause();

    public void reset();

    public boolean allSatisfied(Set<Integer> usedVariables);

    public void checkListType(int steps, int stepsFromLast, int newClauses, int newClausesFromLast);

    public List<Integer[]> getClauses();

    public long getCheckedClauses();

    public boolean checkUnitClause(Integer[] clause, int index, HashMap<Integer, Boolean> assignment, int ignoredLiteral);
}
