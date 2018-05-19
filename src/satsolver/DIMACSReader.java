/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 *
 * @author Vláďa
 */
public class DIMACSReader {

    String fileWithPaths;
    String[] files;
    int index = 0;
    private boolean printFileNames = false;

    public DIMACSReader(String fileWithPaths, boolean printFileNames) {
        this.fileWithPaths = fileWithPaths;
        this.printFileNames = printFileNames;
        LinkedList<String> paths = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileWithPaths)))) {
            String line;
            while ((line = br.readLine()) != null) {
                paths.add(line);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        files = paths.toArray(new String[0]);
    }

    public LinkedList<Integer[]> nextFormula() {
        LinkedList<Integer[]> formula = new LinkedList<>();
        int nbvar = 0;
        int nbclauses = 0;
        while (index < files.length && !Files.exists(Paths.get(files[index].trim()))) {
            index++;
        }
        if (index == files.length) {
            return null;
        }
        int ind = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(files[index])))) {
            if (printFileNames) {
                System.out.println("File name: " + files[index]);
            }
            String line;
            while ((line = br.readLine()) != null) {
                ind++;
                if (line.startsWith("c") || line.startsWith("%") || line.startsWith("0") || line.length() == 0) {
                    continue;
                } else if (line.startsWith("p")) {
                    String[] parts = line.split("[ ]+");
                    nbclauses = Integer.parseInt(parts[3]);
                    nbvar = Integer.parseInt(parts[2]);
                } else {
                    LinkedList<Integer> clause = new LinkedList<>();
                    String[] parts = line.split("[ ]+");
                    for (String part : parts) {
                        if (part.length() == 0) {
                            continue;
                        }
                        int literal = Integer.parseInt(part);
                        if (literal != 0) {
                            clause.add(literal);
                        }
                    }
                    formula.add(clause.toArray(new Integer[0]));
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            System.err.println(ind);
        }
        index++;
        return formula;
    }

    public boolean hasNextFormula() {
        return index < files.length;
    }

}
