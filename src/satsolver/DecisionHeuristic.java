/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.Set;

/**
 *
 * @author Vláďa
 */
public interface DecisionHeuristic {
    
    public int chooseLiteral(Set<Integer> assignedVariables);
    
}
