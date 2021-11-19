package hr.fer.zemris.optimization;

import java.util.ArrayList;
import java.util.List;

public class Main2 {

    public static void main(String[] args) {
        zad1();
        zad2();
        zad3();
        zad4();
        zad5();
    }

    private static void zad1() {
        printTaskInfo(1);

        System.out.println("Pronalazak minimuma funkcije f3 pomoću gradijentnog spusta");
        System.out.println("Početna točka: x0 = (0, 0)");

        double[] x0 = new double[]{0, 0};

        System.out.println("-".repeat(30));
        System.out.println("1) bez određivanja optimalnog iznosa koraka: \n");
        gradientDescent(x0, false, GoalFunctionsDerivations.f3, GoalFunctionsDerivations.f3_dx1);
        System.out.println("-".repeat(30));
        System.out.println("2) sa određivanjem optimalnog iznosa koraka: \n");
        gradientDescent(x0, true, GoalFunctionsDerivations.f3, GoalFunctionsDerivations.f3_dx1);
    }

    private static void zad2() {
        printTaskInfo(2);

        double[] x0 = new double[]{-1.9, 2};
        System.out.println("Pronalazak minimuma funkcije f1 s određivanjem optimalnog iznosa koraka");
        System.out.println("Početna točka: x0 = (-1.9, 2)");
        System.out.println("-".repeat(30));
        System.out.println("1) Newton-Raphsonov postupak: \n");
        newtonRaphson(x0, true, GoalFunctionsDerivations.f1, GoalFunctionsDerivations.f1_dx1, GoalFunctionsDerivations.f1_dx1dx1);
        System.out.println("\n2) gradijentni spust: \n");
        gradientDescent(x0, true, GoalFunctionsDerivations.f1, GoalFunctionsDerivations.f1_dx1);

        System.out.println();
        System.out.println("-".repeat(30));
        x0 = new double[]{0.1, 0.3};
        System.out.println("Pronalazak minimuma funkcije f2 s određivanjem optimalnog iznosa koraka");
        System.out.println("Početna točka: x0 = (0.1, 0.3)");
        System.out.println("-".repeat(30));
        System.out.println("1) Newton-Raphsonov postupak: \n");
        newtonRaphson(x0, true, GoalFunctionsDerivations.f2, GoalFunctionsDerivations.f2_dx1, GoalFunctionsDerivations.f2_dx1dx1);
        System.out.println("\n2) gradijentni spust: \n");
        gradientDescent(x0, true, GoalFunctionsDerivations.f2, GoalFunctionsDerivations.f2_dx1);
    }

    private static void zad3() {
        printTaskInfo(3);
        List<AbstractFunction> constraints = new ArrayList<>();
        constraints.add(Constraints.c1);
        constraints.add(Constraints.c2);

        double[] x0 = new double[]{-1.9, 2};
        System.out.println("Pronalazak minimuma funkcije f1 pomoću Boxovog postupka");
        System.out.println("Početna točka: x0 = (-1.9, 2)");
        System.out.println();
        boxAlgorithm(x0, GoalFunctionsDerivations.f1, constraints, -100, 100);

        System.out.println();
        System.out.println("-".repeat(30));
        x0 = new double[]{0.1, 0.3};
        System.out.println("Pronalazak minimuma funkcije f2 pomoću Boxovog postupka");
        System.out.println("Početna točka: x0 = (0.1, 0.3)");
        System.out.println();
        boxAlgorithm(x0, GoalFunctionsDerivations.f2, constraints, -100, 100);
    }

    private static void zad4() {
        printTaskInfo(4);
        List<AbstractFunction> constraints = new ArrayList<>();
        constraints.add(Constraints.c1);
        constraints.add(Constraints.c2);

        double[] x0 = new double[]{-1.9, 2};
        System.out.println("Pronalazak minimuma funkcije f1 pomoću transformacije na mješoviti način i Hooke-Jeeves postupka");
        System.out.println("Početna točka: x0 = (-1.9, 2)");
        System.out.println();
        mixTransformation(x0, GoalFunctionsDerivations.f1, constraints, new ArrayList<>());
        x0 = new double[]{1.9, 2};
        System.out.println("\nPočetna točka: x0 = (1.9, 2)");
        System.out.println();
        mixTransformation(x0, GoalFunctionsDerivations.f1, constraints, new ArrayList<>());

        System.out.println();
        System.out.println("-".repeat(30));
        x0 = new double[]{0.1, 0.3};
        System.out.println("Pronalazak minimuma funkcije f2 pomoću transformacije na mješoviti način i Hooke-Jeeves postupka");
        System.out.println("Početna točka: x0 = (0.1, 0.3)");
        System.out.println();
        mixTransformation(x0, GoalFunctionsDerivations.f2, constraints, new ArrayList<>());
    }

    private static void zad5() {
        printTaskInfo(5);
        List<AbstractFunction> inequalityConstraints = new ArrayList<>();
        List<AbstractFunction> equalityConstraints = new ArrayList<>();
        inequalityConstraints.add(Constraints.c3);
        inequalityConstraints.add(Constraints.c4);
        equalityConstraints.add(Constraints.c5);

        double[] x0 = new double[]{5, 5};
        System.out.println("Pronalazak minimuma funkcije f4 pomoću transformacije na mješoviti način i Hooke-Jeeves postupka");
        System.out.println("Početna točka: x0 = (5, 5)");
        System.out.println();
        mixTransformation(x0, GoalFunctionsDerivations.f4, inequalityConstraints, equalityConstraints);
    }

    private static void gradientDescent(double[] x0, boolean goldenRatio, AbstractFunction f, AbstractFunction derivations) {
        double[] x = OptimizationAlgorithms.gradientDescent(x0, goldenRatio, f);
        System.out.println("Minimum: (" + OptimizationAlgorithms.arrayString(x) + ")");
        System.out.println("Broj evaluacija funkcija: " + f.getCounter());
        System.out.println("Broj računanja gradijenta: " + derivations.getCounter());

        f.reset();
        derivations.reset();
    }

    private static void newtonRaphson(double[] x0, boolean goldenRatio, AbstractFunction f, AbstractFunction derivations, AbstractFunction hesse) {
        double[] x = OptimizationAlgorithms.newtonRaphson(x0, goldenRatio, f);
        System.out.println("Minimum: (" + OptimizationAlgorithms.arrayString(x) + ")");
        System.out.println("Broj evaluacija funkcija: " + f.getCounter());
        System.out.println("Broj računanja gradijenta: " + derivations.getCounter());
        System.out.println("Broj računanja Hesseove matrice: " + hesse.getCounter());

        f.reset();
        derivations.reset();
        hesse.reset();
    }

    private static void boxAlgorithm(double[] x0, AbstractFunction f, List<AbstractFunction> constraints, double xd, double xg) {
        double[] x = OptimizationAlgorithms.boxAlgorithm(x0, f, constraints, xd, xg);
        System.out.println("Minimum: (" + OptimizationAlgorithms.arrayString(x) + ")");
        System.out.println("Broj evaluacija funkcija: " + f.getCounter());

        f.reset();
    }

    private static void mixTransformation(double[] x0, AbstractFunction f, List<AbstractFunction> inequalityConstraints, List<AbstractFunction> equalityConstraints) {
        double[] x = OptimizationAlgorithms.mixTransformation(x0, f, inequalityConstraints, equalityConstraints);
        System.out.println("Minimum: (" + OptimizationAlgorithms.arrayString(x) + ")");
        System.out.println("Broj evaluacija funkcija: " + f.getCounter());

        f.reset();
    }

    private static void printTaskInfo(int taskNumber) {
        System.out.println();
        System.out.println("=".repeat(15));
        System.out.println(taskNumber + ". zadatak");
        System.out.println("=".repeat(15));
        System.out.println();
    }

}
