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
public class EmptyClause extends Exception {

    public EmptyClause() {
    }

    public EmptyClause(String message) {
        super(message);
    }
}
