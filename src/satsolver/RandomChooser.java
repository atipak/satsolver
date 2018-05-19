/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Vláďa
 */
public class RandomChooser implements DecisionHeuristic {

    private Random rnd;
    protected Set<Integer> allVariables;

    public RandomChooser(int variables) {
        rnd = new Random();
        allVariables = new HashSet<>();
        for (int i = 1; i <= variables; i++) {
            allVariables.add(i);
        }
    }

    public RandomChooser(int variables, long seed) {
        this(variables);
        rnd.setSeed(seed);
    }

    @Override
    public int chooseLiteral(Set<Integer> assignedVariables) {
        Set<Integer> unassignedVariables = new HashSet<>(allVariables);
        unassignedVariables.removeAll(assignedVariables);
        if (unassignedVariables.size() > 0) {
            int randomNumber = rnd.nextInt(unassignedVariables.size());
            return unassignedVariables.iterator().next();
        }
        else {
            return 0;
        }
    }

}
