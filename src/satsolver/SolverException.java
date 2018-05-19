/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

/**
 *
 * @author Vláďa
 */
public class SolverException extends Exception{
    
    public int state = 0;

    public SolverException() {
    }

    public SolverException(String message) {
        super(message);
    }
    
}
