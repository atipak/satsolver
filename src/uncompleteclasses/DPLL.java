/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uncompleteclasses;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Vláďa
 */
public class DPLL {

    LinkedList<RemoveClause> removeClauses = new LinkedList<>();
    LinkedList<RemoveLiteral> removeLiterals = new LinkedList<>();
    LinkedList<Integer> assigments = new LinkedList<>();
    HashMap<Integer, AssigmentCounts> assigmentsCounts = new HashMap<>();
    HashMap<Integer, Boolean> assigmentsValues = new HashMap<>();
    LinkedList<LinkedList<Integer>> clauses;

    private class RemoveClause {

        LinkedList<Integer> clause;
        int index;

        public RemoveClause(LinkedList<Integer> clause, int index) {
            this.clause = clause;
            this.index = index;
        }
    }

    private class RemoveLiteral {

        int literal;
        int index;

        public RemoveLiteral(int literal, int index) {
            this.literal = literal;
            this.index = index;
        }
    }

    private class AssigmentCounts {

        int removedClauses;
        int removedLiterals;

        public AssigmentCounts(int removedClauses, int removedLiterals) {
            this.removedClauses = removedClauses;
            this.removedLiterals = removedLiterals;
        }

    }

    private class EmptyClause extends Exception {

        public EmptyClause() {
        }

        public EmptyClause(String message) {
            super(message);
        }

    }

    private class ListWrapper {
        LinkedList<RemoveClause> rClauses = new LinkedList<>();
        LinkedList<RemoveLiteral> rLiterals = new LinkedList<>();

        public ListWrapper() {
        }
        
        
    }

    private int unitPropagation() throws EmptyClause {
        LinkedList<RemoveClause> rClauses = new LinkedList<>();
        LinkedList<RemoveLiteral> rLiterals = new LinkedList<>();
        LinkedList<Integer> ass = new LinkedList<>();
        HashMap<Integer, AssigmentCounts> assCounts = new HashMap<>();
        HashMap<Integer, Boolean> assValues = new HashMap<>();
        try {
            while (true) {
                int literal = 0;
                int c = 0;
                int l = 0;
                for (LinkedList<Integer> clause : clauses) {
                    if (clause.size() == 1) {
                        literal = clause.getFirst();
                        if (literal > 0) {
                            assValues.put(literal, Boolean.TRUE);
                        } else {
                            assValues.put(literal, Boolean.FALSE);
                        }
                        ass.add(literal);
                    } else if (clause.size() == 0) {
                        throw new EmptyClause("Empty clause implied");
                    }
                }
                if (literal == 0) {
                    break;
                }
                ListWrapper listWrapper = getLists(literal);
                // removing 
                removingLiterals(listWrapper.rLiterals);
                removingClauses(listWrapper.rClauses);
                // adding to local lists
                assCounts.put(literal, new AssigmentCounts(c, l));
                rLiterals.addAll(listWrapper.rLiterals);
                rClauses.addAll(listWrapper.rClauses);
            }
        } catch (EmptyClause ec) {
            backUp(ass.size());
            throw new EmptyClause("Empty clause implied");
        }
        if (ass.size() > 0) {
            removeClauses.addAll(rClauses);
            removeLiterals.addAll(rLiterals);
            assigments.addAll(ass);
            assigmentsCounts.putAll(assCounts);
            assigmentsValues.putAll(assValues);
        }
        return ass.size();
    }

    private void removingClauses(LinkedList<RemoveClause> rClauses) {
        Collections.reverse(rClauses);
        for (RemoveClause rClause : rClauses) {
            clauses.remove(rClause.index);
        }
    }

    private void removingLiterals(LinkedList<RemoveLiteral> rLiterals) {
        Collections.reverse(rLiterals);
        for (RemoveLiteral rLiteral : rLiterals) {
            clauses.get(rLiteral.index).remove(rLiteral.literal);
        }
    }

    private ListWrapper getLists(int literal) {
        ListWrapper listWrapper = new ListWrapper();
        LinkedList<Integer> clause = null;
        for (int i = 0; i < clauses.size(); i++) {
            clause = clauses.get(i);
            if (clause.contains(-1 * literal)) {
                listWrapper.rLiterals.add(new RemoveLiteral(literal, i));
            }
            if (clause.contains(literal)) {
                listWrapper.rClauses.add(new RemoveClause(clause, i));
            }
        }
        return listWrapper;
    }
    
    private void backUp(int literalsCount) {
// TODO
    }

    public DPLL(LinkedList<LinkedList<Integer>> clauses) {
        this.clauses = clauses;
    }

    public HashMap<Integer, Boolean> solve() {
        runDpll();
        return assigmentsValues;
    }

    private boolean runDpll() {
        if (clauses.size() > 0) {
            return true;
        }
        if (clauses.size() > 0 && clauses.get(0).size() > 0) {
            return false;
        }
        int literal = clauses.get(0).get(0);
        // backtracking
        assigments.add(literal);
        assigmentsValues.put(literal, Boolean.TRUE);
        assigments.add(literal);
        assigmentsValues.put(literal, Boolean.FALSE);
        return true;
    }

}
