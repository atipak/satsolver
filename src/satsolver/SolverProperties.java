/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Vláďa
 */
public class SolverProperties {

    public enum FormuleNotation {

        INFIX, PREFIX
    }

    public enum SolverType {

        GSAT, DPLL, CDCL
    }

    public enum InputType {

        DIMACS, CLASSIC
    }

    public enum EncodingType {

        TSETIN
    }

    public enum DecicionHeuristic {

        JAROSLOWWANG, VSIDS, RANDOM
    }

    public enum ClauseStructure {

        WATCHEDLITERALS, WATCHALL
    }

    public enum FormulasInserting {

        FILE, INTERACTIVE
    }

    private boolean debug;
    private SolverType solverType;
    private FormuleNotation formuleNotation;
    private HashMap<String, String> conjuction;
    private InputType inputType;
    private String filesPath;
    private EncodingType encodingType;
    private DecicionHeuristic decicionHeuristic;
    private ClauseStructure clauseStructure;
    private FormulasInserting clauseInserting;
    private LinkedList<String> formulas;
    private int maxFlips;
    private int maxTries;
    private boolean printAverageStatistics;
    private int printStatSteps;
    private boolean printFileNames;
    private boolean checkListType;
    private int checkFrequency;
    private int decHeuristicPeriodOfDecay;
    private int decHeuristicDecayRate;

    // TODO make naive encoding
    public SolverProperties() {
        debug = false;
        checkListType = true;
        printFileNames = false;
        printAverageStatistics = false;
        solverType = SolverType.CDCL;
        formuleNotation = FormuleNotation.PREFIX;
        conjuction = new HashMap<>();
        conjuction.put("and", "&");
        conjuction.put("or", "|");
        conjuction.put("not", "-");
        conjuction.put("imp", ">");
        inputType = InputType.CLASSIC;
        filesPath = ".";
        encodingType = EncodingType.TSETIN;
        decicionHeuristic = DecicionHeuristic.JAROSLOWWANG;
        clauseStructure = ClauseStructure.WATCHEDLITERALS;
        clauseInserting = FormulasInserting.INTERACTIVE;
        formulas = new LinkedList<>();
        maxFlips = 50;
        maxTries = 50;
        printStatSteps = 1000;
        checkFrequency = 500;
        decHeuristicDecayRate = 2;
        decHeuristicPeriodOfDecay = 50;
    }

    public int getCheckFrequency() {
        return checkFrequency;
    }

    public void setCheckFrequency(int checkFrequency) {
        this.checkFrequency = checkFrequency;
    }

    public boolean isCheckListType() {
        return checkListType;
    }

    public void setCheckListType(boolean checkListType) {
        this.checkListType = checkListType;
    }

    public boolean isPrintFileNames() {
        return printFileNames;
    }

    public void setPrintFileNames(boolean printFileNames) {
        this.printFileNames = printFileNames;
    }

    public int getMaxFlips() {
        return maxFlips;
    }

    public void setMaxFlips(int maxFlips) {
        this.maxFlips = maxFlips;
    }

    public int getMaxTries() {
        return maxTries;
    }

    public void setMaxTries(int maxTries) {
        this.maxTries = maxTries;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getDecHeuristicPeriodOfDecay() {
        return decHeuristicPeriodOfDecay;
    }

    public void setDecHeuristicPeriodOfDecay(int decHeuristicPeriodOfDecay) {
        this.decHeuristicPeriodOfDecay = decHeuristicPeriodOfDecay;
    }

    public int getDecHeuristicDecayRate() {
        return decHeuristicDecayRate;
    }

    public void setDecHeuristicDecayRate(int decHeuristicDecayRate) {
        if (decHeuristicDecayRate > 1) {
            this.decHeuristicDecayRate = decHeuristicDecayRate;
        }
    }

    public SolverType getSolverType() {
        return solverType;
    }

    public void setSolverType(SolverType solverType) {
        this.solverType = solverType;
    }

    public FormuleNotation getFormuleNotation() {
        return formuleNotation;
    }

    public void setFormuleNotation(FormuleNotation formuleNotation) {
        this.formuleNotation = formuleNotation;
    }

    public HashMap<String, String> getConjuction() {
        return conjuction;
    }

    public void setConjuction(HashMap<String, String> conjuction) {
        this.conjuction = conjuction;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    public EncodingType getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(EncodingType encodingType) {
        this.encodingType = encodingType;
    }

    public FormulasInserting getClauseInserting() {
        return clauseInserting;
    }

    public void setClauseInserting(FormulasInserting clauseInserting) {
        this.clauseInserting = clauseInserting;
    }

    public DecicionHeuristic getDecicionHeuristic() {
        return decicionHeuristic;
    }

    public void setDecicionHeuristic(DecicionHeuristic decicionHeuristic) {
        this.decicionHeuristic = decicionHeuristic;
    }

    public ClauseStructure getClauseStructure() {
        return clauseStructure;
    }

    public void setClauseStructure(ClauseStructure clauseStructure) {
        this.clauseStructure = clauseStructure;
    }

    public void addClause(String clause) {
        formulas.add(clause);
    }

    public void removeClause(int index) {
        try {
            formulas.remove(index);
        } catch (Exception e) {
            System.out.println("Wrong index");
        }
    }

    public void printAllClauses() {
        for (int i = 0; i < formulas.size(); i++) {
            System.out.println(i + ". " + formulas.get(i));
        }
    }

    public LinkedList<String> getFormulas() {
        return formulas;
    }

    public boolean isPrintAverageStatistics() {
        return printAverageStatistics;
    }

    public void setPrintAverageStatistics(boolean printAverageStatistics) {
        this.printAverageStatistics = printAverageStatistics;
    }

    public int getPrintStatSteps() {
        return printStatSteps;
    }

    public void setPrintStatSteps(int printStatSteps) {
        this.printStatSteps = printStatSteps;
    }

}
