/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Vláďa
 */
public class CDCL implements SolverType {

    /**
     * Reference on clauses in formula.
     */
    private List<Integer[]> clauses;
    /**
     * Actual assignment of variables.
     */
    private HashMap<Integer, Boolean> assignment;
    /**
     * A structure for saving clauses. Contains information about unit clauses,
     * actual clauses and check if there is a contradiction in clauses.
     */
    private ClauseStructure clauseStructure;
    /**
     * Levels which variables were assigned in.
     */
    private Integer[] levels;
    /**
     * Antecedents of variables. Clauses which determine assignment of
     * variables.
     */
    private Integer[] antecedents;
    /**
     * List which is saving order which variables was assigned in.
     */
    private LinkedList<Integer> assignmentOrder;
    /**
     * First stack for DPLL algorithm.
     */
    private LinkedList<Integer> dpllVariableStack;
    /**
     * Second stack for DPLL algorithm.
     */
    private LinkedList<Integer> dpllSecondVariableStack;
    /**
     * Index of clause with assignment contradiction.
     */
    private int emptyClause;
    /**
     * Decision heuristic which is used for choosing varibles for assigning.
     */
    private DecisionHeuristic dh = null;
    /**
     * If there should be printed debug notice.
     */
    private boolean debugPrint = false;
    /**
     * Properties of solver given by user.
     */
    private SolverProperties prop;
    /**
     * Should be used DPLL or CDCL algorithm.
     */
    private boolean reductionToDpll = false;
    /**
     * Actual level.
     */
    private int level;
    /**
     * Actual statistics of solver execution.
     */
    private Statistics stats;

    public CDCL() {

    }

    /**
     * Solves satisfability of given clauses.
     *
     * @param clauses Clauses in formula
     * @param prop Solver properties
     * @return Assignment of variables in clauses.
     */
    @Override
    public Statistics solve(List<Integer[]> clauses, SolverProperties prop) {
        // init
        this.clauses = clauses;
        Set<Integer> variables = new HashSet<>();
        for (Integer[] clause : clauses) {
            for (Integer literal : clause) {
                if (!variables.contains(Math.abs(literal))) {
                    variables.add(Math.abs(literal));
                }
            }
        }
        assignmentOrder = new LinkedList<>();
        assignment = new HashMap<>();
        emptyClause = -1;
        levels = new Integer[variables.size()];
        antecedents = new Integer[variables.size()];
        // parsing solver properties and creating structer according to them 
        parseArguments(prop);
        if (reductionToDpll) {
            dpllVariableStack = new LinkedList<>();
            dpllSecondVariableStack = new LinkedList<>();
        }
        this.prop = prop;
        stats = new Statistics();
        stats.setBeginTime(System.currentTimeMillis());
        // formula solving
        if (solveFormula()) {
            stats.setEndTime(System.currentTimeMillis());
            stats.setCheckedClause(clauseStructure.getCheckedClauses());
            // checks if there is no contradiction in returned assignment
            if (verify()) {
                // everything OK
                stats.setSuccess(true);
                return stats.setAssignment(assignment);
            } else {
                // returned solution isnt correct -> fatal error happened in algorithm 
                System.err.println("Something went wrong");
                return stats;
            }
        }
        // formula is unsat
        stats.setCheckedClause(clauseStructure.getCheckedClauses());
        stats.setEndTime(System.currentTimeMillis());
        return stats;
    }

    /**
     * Main loop for selving clauses.
     *
     * @return True if correct solution is in assignment.
     */
    private boolean solveFormula() {
        level = 0;
        // contradition in formula
        if (!unitPropagation(level)) {
            return false;
        }
        while (true) {
            stats.incrementSteps();
            if (prop.getPrintStatSteps() != 0 && prop.isPrintAverageStatistics() && stats.getSteps() % prop.getPrintStatSteps() == 0) {
                stats.printStatistics();
            }
            // is there a need to changed the type of clauses list 
            if (prop.isCheckListType() && stats.getSteps() % prop.getCheckFrequency() == 0) {
                clauseStructure.checkListType(stats.getSteps(), stats.getSteps(), stats.getAddedClauses(), stats.getAddedClauses());
            }
            //System.out.println("level: " + level );
            // select variable to assign (return value is number with recommned sign)
            int selectedVariable = selectVariable();
            // there is no variable to assign -> all variables are assigned
            if (selectedVariable == 0) {
                return true;
            }
            // DPLL reduction 
            if (reductionToDpll) {
                // checking if chosen variable isn't in contradiction with DPLL algorithm and chosing appropriate literal
                selectedVariable = variableStackControl(selectedVariable);
                if (selectedVariable == 0) {
                    continue;
                }
            }
            // save changes
            if (debugPrint) {
                System.out.println("Solve: " + selectedVariable + " in level " + level);
            }
            stats.incrementDecisionCount();
            // add new assignment into assignment structures
            addNewAssignment(selectedVariable, level);
            // is unit propagation possible? Is there a contradiction?
            boolean result = unitPropagation(level);
            if (reductionToDpll) {
                // DPLL algortihm 
                if (!btAfterUnitProp(result)) {
                    return false;
                }
            } else {
                while (!result) {
                    stats.incrementConflicts();
                    // which level is good enough for backpropagation
                    level = analyzeConflicts(level);
                    if (level < 0) {
                        return false;
                    }
                    backtrack(level);
                    // backtracking until unit propagation succeded or contradiction arrive
                    result = unitPropagation(level);
                }
            }
            level++;
        }
    }

    /**
     * DPLL algortithm backtracks only one level back.
     *
     * @param result Result of unit propagation.
     * @return level after backtracking >= 0
     */
    private boolean btAfterUnitProp(boolean result) {
        if (!result) {
            backtrack(--level);
        }
        return level >= 0;
    }

    /**
     * Service funtion for DPLL algortithm.
     *
     * @param selectedVariable Signed variable (literal) which was chosen by
     * decision heuristic.
     * @return Correct literal for DPLL algorithm
     */
    private int variableStackControl(int selectedVariable) {
        // first call
        if (dpllVariableStack.size() == 0) {
            dpllVariableStack.add(selectedVariable);
            return selectedVariable;
        }
        if (Math.abs(selectedVariable) == Math.abs(dpllVariableStack.getLast())) {
            // selected variable was already once chosen
            resetClauseStructure();
            if (dpllSecondVariableStack.size() == 0 || Math.abs(selectedVariable) != Math.abs(dpllSecondVariableStack.getLast())) {
                // variable was chosen once, aply oposite sign

            } else {
                // variable was already chosen two times -> backtrack one level up 
                // return next-to-last

                while (dpllVariableStack.size() > 0 && dpllSecondVariableStack.size() > 0 && Math.abs(dpllVariableStack.getLast()) == Math.abs(dpllSecondVariableStack.getLast())) {
                    dpllVariableStack.removeLast();
                    dpllSecondVariableStack.removeLast();
                    primitiveBacktrack(--level - 1);
                }
                if (dpllVariableStack.size() == 0) {
                    return 0;
                } else {
                    selectedVariable = dpllVariableStack.getLast();
                }
            }
            boolean lastAssigmentType = Math.signum(selectedVariable) == 1;
            if (lastAssigmentType) {
                // origin sign was positive -> aply negative
                selectedVariable = Math.abs(selectedVariable);
                selectedVariable = -1 * selectedVariable;
            } else {
                // origin sign was negative -> aply positive
                selectedVariable = Math.abs(selectedVariable);
            }
            dpllSecondVariableStack.addLast(selectedVariable);
            return selectedVariable;
        } else {
            // this os another variable than was chosen last time 
            dpllVariableStack.addLast(selectedVariable);
            return selectedVariable;
        }
    }

    /**
     * Unit propagation.
     *
     * @param level actual level
     * @return True if unit propagation succeeds and no contradiction is found
     */
    private boolean unitPropagation(int level) {
        if (debugPrint) {
            System.out.print("UnitProp: ");
        }
        emptyClause = -1;
        // at least one variable was already chosen for assignment
        if (assignmentOrder.size() > 0) {
            try {
                // updating pointers in clauses structure, getting new unit clauses, checking contradiction in actual assignment 
                // can throw empty clause if there is cotradiction
                clauseStructure.update(assignment, assignmentOrder.getLast(), assignment.get(assignmentOrder.getLast()));
            } catch (EmptyClause ec) {
                if (debugPrint) {
                    System.out.println("");
                }
                // get index of empty clause
                emptyClause = Integer.valueOf(ec.getMessage());
                if (debugPrint) {
                    System.out.println("Conflict in clause: " + emptyClause + "(" + printClause(emptyClause) + ")");
                }
                return false;
            }
        }

        while (clauseStructure.hasUnitClause()) {
            stats.incrementUnitPropagationCount();
            // x: index of clause, y: index of literal which determines unit clause
            Point clauseAndLiteral = clauseStructure.getUnitClauseVariable();
            try {
                int literal = clauseStructure.getClauses().get(clauseAndLiteral.x)[clauseAndLiteral.y];
                // variable already assigned
                if (assignment.containsKey(Math.abs(literal))) {
                    continue;
                }
                if (debugPrint) {
                    System.out.print(literal);
                    System.out.print("( " + printClause(clauseAndLiteral.x) + " )");
                    System.out.print(", ");
                }
                // add new assignment, ascendent for literal from unit clause
                addNewAssignment(literal, level);
                if (clauseStructure.getClauses().get(clauseAndLiteral.x).length > 1) {
                    setAscendent(Math.abs(literal), clauseAndLiteral.x);
                } else {
                    setAscendent(Math.abs(literal), null);
                }
                // checking if there is no new unit clause after adding new assignment
                clauseStructure.update(assignment, Math.abs(literal), Math.signum(literal) > 0);
            } catch (EmptyClause ec) {
                if (debugPrint) {
                    System.out.println("");
                }
                // get index of empty clause
                emptyClause = Integer.valueOf(ec.getMessage());
                if (debugPrint) {
                    System.out.println("Conflict in clause: " + emptyClause + "(" + printClause(emptyClause) + ")");
                }
                return false;
            }
        }
        if (debugPrint) {
            System.out.println("");
        }
        return true;
    }

    /**
     * Selects variable with recommened sign by decision heuristic.
     *
     * @return Signed variable.
     */
    private int selectVariable() {
        int var = dh.chooseLiteral(assignment.keySet());
        return var;
    }

    /**
     * Analyzis newly-emerged conflict from unit propagation and determines
     * which level to return to.
     *
     * @param level Actual level
     * @return Backtracking level
     */
    private int analyzeConflicts(int level) {
        if (debugPrint) {
            System.out.println("Conflict analyzing on  level " + level + " ...");
        }
        if (emptyClause == -1) {
            return -1;
        }
        // conflict clause getting by index stored in emptyClause variable
        Integer[] conflictClause = clauseStructure.getClauses().get(emptyClause);
        // there exists no solution becouse we are on top of "stack" and we want to backtrack to nonsense level
        if (conflictClause.length == 1 && assignmentOrder.size() == 1) {
            return -1;
        }
        // chooses adequate clause (by algorithm CDCL)
        conflictClause = chooseAdequateClauses(conflictClause, level);
        resetClauseStructure();
        int clauseIndex = clauseStructure.getClauses().indexOf(conflictClause);
        // learning
        if (clauseIndex == -1) {
            // here proceed only clauses between origin size and actual size
            if (debugPrint) {
                System.out.print("Add new clause: ");
                for (Integer literal : conflictClause) {
                    System.out.print(literal + ", ");
                }
                System.out.println("");
            }
            Integer[] index = getLiteralsFromLevel(conflictClause, level);
            stats.incrementAddedClauses();
            if (index.length == 1) {
                clauseStructure.addNewClause(conflictClause, index[0]);
            } else {
                clauseStructure.addNewClause(conflictClause, -1);
            }
        } // added else
        else {
            Integer[] index = getLiteralsFromLevel(conflictClause, level);
            // variable in index[0] has to be ignored becouse will be removed in backtracking
            clauseStructure.checkUnitClause(conflictClause, clauseIndex, assignment, Math.abs(conflictClause[index[0]]));
        }
        return getBTLevel(conflictClause);
    }

    /**
     * Make resolution until adequate clause for backtracking is found.
     * @param conflictClause Conflict clause.
     * @param level actual level
     * @return Adequate clause.
     */
    private Integer[] chooseAdequateClauses(Integer[] conflictClause, int level) {
        int backPropIndex = assignmentOrder.size() - 1;
        while (true) {
            int lastAssignVariableInClause;
            // find first variable which is in actual clause and was already assigned (skip other variables)
            while (true) {
                lastAssignVariableInClause = assignmentOrder.get(backPropIndex);
                backPropIndex--;
                boolean inClause = false;
                for (Integer element : conflictClause) {
                    if (Math.abs(element) == lastAssignVariableInClause) {
                        inClause = true;
                    }
                }
                if (inClause) {
                    break;
                }
            }
            Integer ascendantReference = getAscendent(lastAssignVariableInClause);
            if (ascendantReference != null) {
                if (debugPrint) {
                    System.out.print("Resolve (over " + lastAssignVariableInClause + " ): ");
                    for (Integer literal : conflictClause) {
                        System.out.print(literal + ",");
                    }
                    System.out.print(":");
                    for (Integer literal : clauseStructure.getClauses().get(ascendantReference)) {
                        System.out.print(literal + ",");
                    }
                    System.out.println("");
                }
                conflictClause = resolve(conflictClause, clauseStructure.getClauses().get(ascendantReference), lastAssignVariableInClause);
            } // added else
            else {
                if (debugPrint) {
                    System.out.println("No ascendant");
                }
            }
            if (isAdequate(conflictClause, level)) {
                break;
            }
        }
        return conflictClause;
    }

    /**
     * Returns all literals from given level
     *
     * @param clause Origin clause
     * @param level Target level
     * @return Array of indices in clause.
     */
    private Integer[] getLiteralsFromLevel(Integer[] clause, int level) {
        LinkedList<Integer> indeces = new LinkedList<>();
        for (int i = 0; i < clause.length; i++) {
            if (level == getLevel(Math.abs(clause[i]))) {
                indeces.add(i);
            }
        }
        return indeces.toArray(new Integer[0]);
    }

    /**
     * Resolution of given clauses.
     *
     * @param c1 First clause.
     * @param c2 Second clause.
     * @param x Variable which resolution is made over
     * @return Resoluted clause.
     */
    private Integer[] resolve(Integer[] c1, Integer[] c2, int x) {
        // there is no ascendant (from algorithm implementation)
        if (c2 == null) {
            return c1;
        }
        Set<Integer> clause = new HashSet<>();
        // returns all variables except ignored variable x
        for (Integer c : c1) {
            if (Math.abs(c) == x) {
            } else {
                clause.add(c);
            }
        }
        for (Integer c : c2) {
            if (Math.abs(c) == x) {
            } else {
                clause.add(c);
            }
        }
        return clause.toArray(new Integer[0]);
    }

    /**
     * Checks if there is only one literal from given level.
     *
     * @param c Clause for checking
     * @param level Targetr (actual) level
     * @return true if condition is true
     */
    private boolean isAdequate(Integer[] c, int level) {
        int count = 0;
        Set<Integer> set = new HashSet<>();
        for (Integer lit : c) {
            set.add(Math.abs(lit));
        }
        for (Integer var : set) {
            if (getLevel(var) == level) {
                count++;
            }
        }
        return count == 1;
    }

    /**
     * Craetes from given clause an asserting clause.
     *
     * @param cl Orogin clause.
     * @return Asserting clause.
     */
    private Integer[] getAsserting(Integer[] cl) {
        Integer[] c = Arrays.copyOf(cl, cl.length);
        if (c.length == 1 && getAscendent(Math.abs(c[0])) == null) {
            return c;
        }
        for (int i = 0; i < c.length; i++) {
            c[i] = -1 * c[i];
        }
        return c;
    }

    /**
     * Returns target level for backtracking.
     *
     * @param c Asserting clause.
     * @return Target level.
     */
    private int getBTLevel(Integer[] c) {
        int highest = -1;
        int secHighest = -1;
        // algorithm conditions 
        if (c.length == 1) {
            return 0;
        }
        if (c.length == 0) {
            return -1;
        }
        // second highest level is the appropriate
        for (Integer lit : c) {
            int lvl = getLevel(Math.abs(lit));
            if (lvl > highest) {
                secHighest = highest;
                highest = getLevel(Math.abs(lit));
            }
            if (lvl > secHighest && highest != lvl) {
                secHighest = getLevel(Math.abs(lit));
            }
        }
        return secHighest;
    }

    /**
     * Backtracking with condition on level. If target level is 0 then all
     * assignment has to be removed.
     *
     * @param level Target level
     */
    private void backtrack(int level) {
        if (level == 0) {
            level = -1;
        }
        primitiveBacktrack(level);
    }

    /**
     * Basic backtracking. Removes all assignments until given level is reached
     * (>).
     *
     * @param level Target level.
     */
    private void primitiveBacktrack(int level) {
        if (debugPrint) {
            System.out.println("Backtrack to level: " + level);
            System.out.print("Deleted variables: ");
        }
        // iterates from old to new assignments
        Iterator<Integer> it = assignmentOrder.descendingIterator();
        while (it.hasNext()) {
            int literal = it.next();
            int l = getLevel(literal);
            if (l > level) {
                it.remove();
                setLevel(literal, null);
                if (getAscendent(literal) != null) {
                    setAscendent(literal, null);
                }
                if (debugPrint) {
                    System.out.print(literal + ",");
                }
                assignment.remove(literal);
            }
        }
        if (debugPrint) {
            System.out.println("");
        }
    }

    /**
     * Add assignment of variable to assignment.
     *
     * @param variable signed variable: + is true, - is false
     * @param level actual level
     */
    private void addNewAssignment(int variable, int level) {
        if (variable > 0) {
            assignment.put(variable, Boolean.TRUE);
        } else {
            assignment.put(Math.abs(variable), false);
        }
        setLevel(variable, level);
        assignmentOrder.add(Math.abs(variable));
    }

    /**
     * Sets given level to given variable.
     *
     * @param variable variable which level should be assign to
     * @param level assigned level
     */
    private void setLevel(int variable, Integer level) {
        levels[Math.abs(variable) - 1] = level;
    }

    /**
     * Returns level of given varibale
     *
     * @param variable required variable (can be with sign)
     * @return level which the variable was assigned in or 0 (fail)
     */
    private Integer getLevel(int variable) {
        try {
            return levels[Math.abs(variable) - 1];
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage() + " occured.");
        }
        return 0;
    }

    /**
     * Adds given ascendent to given variable.
     *
     * @param variable variable (can be with sign).
     * @param ascendent ascendent
     */
    private void setAscendent(int variable, Integer ascendent) {
        antecedents[Math.abs(variable) - 1] = ascendent;
    }

    /**
     * Retrun ascendent of given variable.
     *
     * @param variable required variable (can be with sign).
     * @return
     */
    private Integer getAscendent(int variable) {
        return antecedents[Math.abs(variable) - 1];
    }

    /**
     * Go through all clauses and verifys if all are satisfied.
     *
     * @return true if all clauses are satisfied
     */
    private boolean verify() {
        for (Integer[] clause : clauses) {
            boolean clauseOK = false;
            for (Integer literal : clause) {
                // there is at least one literal sat.
                if ((literal < 0 && !assignment.get(Math.abs(literal)))
                        || (literal > 0 && assignment.get(Math.abs(literal)))) {
                    clauseOK = true;
                    break;
                }
            }
            if (!clauseOK) {

                return false;
            }
        }
        return true;
    }

    /**
     * Parses properties and changed SAT solver by them.
     *
     * @param prop properties of SAT solver
     */
    private void parseArguments(SolverProperties prop) {
        debugPrint = prop.isDebug();
        // clauses structure
        switch (prop.getClauseStructure()) {
            case WATCHALL:
                clauseStructure = new WatchAll(clauses);
                break;
            case WATCHEDLITERALS:
                clauseStructure = new WatchedLiterals(clauses);
                break;
        }
        // decision heuristic
        switch (prop.getDecicionHeuristic()) {
            case JAROSLOWWANG:
                dh = new JeroslowWang(clauseStructure.getClauses(), levels.length);
                break;
            case RANDOM:
                dh = new RandomChooser(levels.length);
                break;
            case VSIDS:
                dh = new Vsids(clauseStructure.getClauses(), levels.length, prop.getDecHeuristicPeriodOfDecay(), prop.getDecHeuristicDecayRate());
        }
        // CDCL algorithm or DPLL
        switch (prop.getSolverType()) {
            case DPLL:
                reductionToDpll = true;
        }
    }

    /**
     * Changed properties of SAT solver.
     *
     * @param prop New properties.
     */
    public void changeProperties(SolverProperties prop) {
        parseArguments(prop);
    }

    /**
     * Reset clauses structure (eg. watched literals).
     */
    private void resetClauseStructure() {
        clauseStructure.reset();
        if (debugPrint) {
            System.out.println("Structure for clauses was reset");
        }
    }

    /**
     * Help functio for printing clauses in readable form.
     *
     * @param index index of clause which should be printed
     * @return
     */
    private String printClause(int index) {
        StringBuilder sb = new StringBuilder();
        Integer[] clause = clauseStructure.getClauses().get(index);
        for (int i = 0; i < clause.length; i++) {
            sb.append(clause[i]);
            if (i == clause.length - 1) {
                continue;
            }
            sb.append(" v ");

        }
        return sb.toString();
    }
}
