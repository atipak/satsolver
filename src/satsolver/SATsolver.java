/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package satsolver;

import oldclasses.TsetinEncodingOld;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author patikv
 */
public class SATsolver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SATsolver solver = new SATsolver();
        solver.heuristicTest();
    }

    public void heuristicTest() {
        SolverProperties prop;
        SolverProperties.SolverType[] st = new SolverProperties.SolverType[]{SolverProperties.SolverType.CDCL};
        SolverProperties.DecicionHeuristic[] dh = new SolverProperties.DecicionHeuristic[]{SolverProperties.DecicionHeuristic.JAROSLOWWANG, SolverProperties.DecicionHeuristic.VSIDS};
        SolverProperties.ClauseStructure[] cs = new SolverProperties.ClauseStructure[]{SolverProperties.ClauseStructure.WATCHEDLITERALS};
        String[] fileNames = new String[]{
            /*"E:\\Verifikace\\Vstupy\\HeuristicTests\\CBS_k3_n100_m423_b90_names.txt",
             "E:\\Verifikace\\Vstupy\\HeuristicTests\\flat125-301_names.txt",
             "E:\\Verifikace\\Vstupy\\HeuristicTests\\logistics_names.txt",
             "E:\\Verifikace\\Vstupy\\HeuristicTests\\sw100-8-lp4-c5_names.txt",
             "E:\\Verifikace\\Vstupy\\HeuristicTests\\uf100-430_names.txt",*/
             "E:\\Verifikace\\Vstupy\\HeuristicTests\\ais_names.txt",
            /*"E:\\Verifikace\\Vstupy\\HeuristicTests\\blocksworld_names.txt"*/
        };
        for (String fileName : fileNames) {
            for (SolverProperties.SolverType s : st) {
                for (SolverProperties.ClauseStructure c : cs) {
                    for (SolverProperties.DecicionHeuristic d : dh) {
                        prop = new SolverProperties();
                        prop.setInputType(SolverProperties.InputType.DIMACS);
                        prop.setDebug(false);
                        prop.setPrintAverageStatistics(false);
                        prop.setPrintFileNames(true);
                        prop.setSolverType(s);
                        prop.setClauseStructure(c);
                        prop.setDecicionHeuristic(d);
                        prop.setFilesPath(fileName);
                        Solver solver = new Solver(prop);
                        try {
                            int i = 0;
                            while (true) {
                                i++;
                                HashMap<String, Boolean> result = solver.solveNext();
                                if (result == null) {
                                    break;
                                }
                            }
                            // name; solver; structure; heuristic
                            System.out.print(Paths.get(fileName).getFileName() + ";");
                            System.out.print(s.name() + ";");
                            System.out.print(c.name() + ";");
                            System.out.print(d.name() + ";");
                            solver.printAverageStatisticsInRow();
                            System.out.println("");
                        } catch (SolverException se) {
                            System.out.println(se.getMessage());
                        }
                    }
                }
            }
        }
    }

    public void test() {
        SolverProperties prop;
        SolverProperties.SolverType[] st = new SolverProperties.SolverType[]{SolverProperties.SolverType.CDCL,/* SolverProperties.SolverType.DPLL*/};
        SolverProperties.DecicionHeuristic[] dh = new SolverProperties.DecicionHeuristic[]{SolverProperties.DecicionHeuristic.JAROSLOWWANG, SolverProperties.DecicionHeuristic.VSIDS, SolverProperties.DecicionHeuristic.RANDOM};
        SolverProperties.ClauseStructure[] cs = new SolverProperties.ClauseStructure[]{SolverProperties.ClauseStructure.WATCHEDLITERALS, SolverProperties.ClauseStructure.WATCHALL};
        String[] fileNames = new String[]{
            "E:\\Verifikace\\Vstupy\\Tests\\uf20-91_names.txt",
            "E:\\Verifikace\\Vstupy\\Tests\\uf50-218_names.txt", /*"E:\\Verifikace\\Vstupy\\Tests\\uf75-325_names.txt", 
         "E:\\Verifikace\\Vstupy\\Tests\\uf100-430_names.txt", 
         "E:\\Verifikace\\Vstupy\\Tests\\uf125-538_names.txt", 
         "E:\\Verifikace\\Vstupy\\Tests\\uf150-645_names.txt", 
         "E:\\Verifikace\\Vstupy\\Tests\\uf175-753_names.txt", 
         "E:\\Verifikace\\Vstupy\\Tests\\uf200-860_names.txt", 
         "E:\\Verifikace\\Vstupy\\Tests\\uf225-960_names.txt"*/};
        for (String fileName : fileNames) {
            for (SolverProperties.SolverType s : st) {
                for (SolverProperties.ClauseStructure c : cs) {
                    for (SolverProperties.DecicionHeuristic d : dh) {
                        prop = new SolverProperties();
                        prop.setInputType(SolverProperties.InputType.DIMACS);
                        prop.setDebug(false);
                        prop.setPrintAverageStatistics(false);
                        prop.setPrintFileNames(false);
                        prop.setSolverType(s);
                        prop.setClauseStructure(c);
                        prop.setDecicionHeuristic(d);
                        prop.setFilesPath(fileName);
                        Solver solver = new Solver(prop);
                        try {
                            int i = 0;
                            while (true) {
                                i++;
                                HashMap<String, Boolean> result = solver.solveNext();
                                if (result == null) {
                                    break;
                                }
                            }
                            // name; solver; structure; heuristic
                            System.out.print(Paths.get(fileName).getFileName() + ";");
                            System.out.print(s.name() + ";");
                            System.out.print(c.name() + ";");
                            System.out.print(d.name() + ";");
                            solver.printAverageStatisticsInRow();
                            System.out.println("");
                        } catch (SolverException se) {
                            System.out.println(se.getMessage());
                        }
                    }
                }
            }
        }
    }

    public void example() {
        boolean printResult = true;
        SolverProperties prop = new SolverProperties();
        prop.addClause("(or a1 (and a2 (and a3 (and a4 a5))))");
        prop.addClause("(or a1 (and a2 (and a3 (and a4 (and a5 (and a6 (and a7 (and a8 (and a9 (and a10 (and a11 (and a12 (and a13 (and a14 (and a15 (and a16 (and a17 (and a18 (and a19 (and a20 (and a21 (and a22 (and a23 (and a24 (and a25 (and a26 (and a27 (and a28 (and a29 (and a30 (and a31 (and a32 (and a33 (and a34 (and a35 (and a36 (and a37 (and a38 (and a39 (and a40 (and a41 (and a42 (and a43 (and a44 (and a45 (and a46 (and a47 (and a48 (and a49 a50)))))))))))))))))))))))))))))))))))))))))))))))))");
        prop.addClause("(or a1 (and a2 (and a3 (and a4 (and a5 (and a6 (and a7 (and a8 (and a9 (and a10 (and a11 (and a12 (and a13 (and a14 (and a15 (and a16 (and a17 (and a18 (and a19 (and a20 (and a21 (and a22 (and a23 (and a24 (and a25 (and a26 (and a27 (and a28 (and a29 (and a30 (and a31 (and a32 (and a33 (and a34 (and a35 (and a36 (and a37 (and a38 (and a39 (and a40 (and a41 (and a42 (and a43 (and a44 (and a45 (and a46 (and a47 (and a48 (and a49 (and a50 (and a51 (and a52 (and a53 (and a54 (and a55 (and a56 (and a57 (and a58 (and a59 (and a60 (and a61 (and a62 (and a63 (and a64 (and a65 (and a66 (and a67 (and a68 (and a69 (and a70 (and a71 (and a72 (and a73 (and a74 (and a75 (and a76 (and a77 (and a78 (and a79 (and a80 (and a81 (and a82 (and a83 (and a84 (and a85 (and a86 (and a87 (and a88 (and a89 (and a90 (and a91 (and a92 (and a93 (and a94 (and a95 (and a96 (and a97 (and a98 (and a99 a100)))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
        prop.addClause("(and (or (and (or x1 (not x4)) (or x3 x9)) (and (or x7 x1) (or x4 x9))) (or (and (or x9 x9) (or (not x4) (not x5))) (and (or x3 (not x6)) (or (not x3) x6))))");
        prop.addClause("(and (or (and (or (and (or (and x1 (not x4)) (and x3 x9)) (or (and x7 x1) (and x4 x9))) (and (or (and x9 x9) (and (not x4) (not x5))) (or (and x3 (not x6)) (and (not x3) x6)))) (or (and (or (and x2 (not x2)) (and (not x6) (not x1))) (or (and (not x9) x7) (and x9 (not x6)))) (and (or (and x2 x4) (and (not x2) x2)) (or (and (not x5) (not x6)) (and x6 (not x4)))))) (and (or (and (or (and (not x2) x9) (and x3 (not x7))) (or (and (not x9) x6) (and x4 x6))) (and (or (and (not x5) x4) (and (not x4) (not x7))) (or (and (not x3) (not x3)) (and x9 (not x7))))) (or (and (or (and (not x6) x3) (and (not x2) x2)) (or (and x3 (not x2)) (and (not x7) (not x9)))) (and (or (and (not x9) x2) (and (not x6) x5)) (or (and (not x3) (not x1)) (and (not x9) x9)))))) (or (and (or (and (or (and x5 x3) (and (not x3) x6)) (or (and (not x1) x6) (and (not x4) x4))) (and (or (and x2 (not x2)) (and x3 (not x9))) (or (and x5 (not x4)) (and x5 (not x6))))) (or (and (or (and (not x9) (not x2)) (and x4 x6)) (or (and x9 x4) (and x2 x4))) (and (or (and x1 (not x2)) (and x5 (not x4))) (or (and x8 x8) (and (not x4) x2))))) (and (or (and (or (and (not x6) (not x7)) (and (not x1) x1)) (or (and (not x6) x4) (and x4 (not x3)))) (and (or (and (not x3) (not x8)) (and x2 (not x9))) (or (and x1 x2) (and x3 (not x8))))) (or (and (or (and (not x4) (not x1)) (and x7 x7)) (or (and (not x8) (not x7)) (and (not x3) x5))) (and (or (and x1 x6) (and x1 (not x9))) (or (and x1 x3) (and x2 x7)))))))");
        prop.addClause("(or (and (or (and (not x4) (not x1)) (and x7 x7)) (or (and (not x8) (not x7)) (and (not x3) x5))) (and (or (and x1 x6) (and x1 (not x9))) (or (and x1 x3) (and x2 x7))))");
        prop.addClause("(and (or (not x4) (and x1 (or x4 x9))) (or x1 (and x9 (not x5))))");

        prop = new SolverProperties();
        prop.setInputType(SolverProperties.InputType.DIMACS);
        //prop.setInputType(SolverProperties.InputType.CLASSIC);
        prop.setSolverType(SolverProperties.SolverType.CDCL);
        //prop.setSolverType(SolverProperties.SolverType.DPLL);
        prop.setDebug(false);
        prop.setPrintAverageStatistics(false);
        prop.setPrintFileNames(false);
        //prop.setClauseStructure(SolverProperties.ClauseStructure.WATCHALL);
        prop.setDecicionHeuristic(SolverProperties.DecicionHeuristic.JAROSLOWWANG);
        //prop.setDecicionHeuristic(SolverProperties.DecicionHeuristic.RANDOM);
        //prop.setFilesPath("E:\\Verifikace\\Vstupy\\hanoi_names.txt");
        prop.setFilesPath("E:\\Verifikace\\Vstupy\\uuf100-430_names.txt");
        Solver solver = new Solver(prop);
        try {
            int i = 0;
            while (true) {
                i++;
                //System.out.println("=============     " + i + "      =============");
                HashMap<String, Boolean> result = solver.solveNext();
                if (result == null) {
                    break;
                }
                if (!printResult) {
                    //System.out.println("======================================");
                    continue;
                }
                for (Map.Entry<String, Boolean> entrySet : result.entrySet()) {
                    String key = entrySet.getKey();
                    Boolean value = entrySet.getValue();
                    System.out.println(key + " : " + value);
                }
                System.out.println("======================================");
            }
            solver.printAverageStatistics();
        } catch (SolverException se) {
            System.out.println(se.getMessage());
        }
    }

    public void develop() {

    }
}
