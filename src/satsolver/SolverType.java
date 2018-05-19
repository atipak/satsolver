/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Vláďa
 */
public interface SolverType {
    public Statistics solve(List<Integer[]> clauses, SolverProperties prop);
}
