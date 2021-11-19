package hr.fer.zemris.optimization;

import static java.lang.Math.*;

public class Main {

    public static void main(String[] args) {
        zad1();
        zad2();
        zad3();
        zad4();
        zad5();
    }

    private static void zad1() {
        printTaskInfo(1);

        System.out.println("-".repeat(30));
        System.out.println("1) Početna točka: x0 = 10");
        System.out.println("-".repeat(30));
        evaluateFirst(new double[]{10});

        System.out.println();
        System.out.println("-".repeat(30));
        System.out.println("2) Početna točka: x0 = 100");
        System.out.println("-".repeat(30));
        evaluateFirst(new double[]{100});

        System.out.println();
        System.out.println("-".repeat(30));
        System.out.println("2) Početna točka: x0 = 1000");
        System.out.println("-".repeat(30));
        evaluateFirst(new double[]{1000});
    }

    private static void zad2() {
        printTaskInfo(2);

        System.out.println("Funkcija f1");
        System.out.println("-".repeat(20));
        evaluateSecond(GoalFunctions.f1, new double[]{-1.9, 2});

        System.out.println("Funkcija f2");
        System.out.println("-".repeat(20));
        evaluateSecond(GoalFunctions.f2, new double[]{4, 2});

        System.out.println("Funkcija f3");
        System.out.println("-".repeat(20));
        evaluateSecond(GoalFunctions.f3, new double[]{0, 0, 0, 0, 0});

        System.out.println("Funkcija f4");
        System.out.println("-".repeat(20));
        evaluateSecond(GoalFunctions.f4, new double[]{5.1, 1.1});
    }

    private static void zad3() {
        printTaskInfo(3);

        System.out.println("Funkcija f4, x0 = (5, 5)");
        System.out.println("-".repeat(20));

        AbstractFunction f4 = GoalFunctions.f4;
        f4.reset();
        System.out.println("a) Simplex po Nedleru i Meadu:");
        evaluateSimplex(new double[]{5, 5}, f4);
        f4.reset();
        System.out.println("b) Hooke-Jeeves postupak:");
        evaluateHookeJeeves(new double[]{5, 5}, f4);
    }

    private static void zad4() {
        printTaskInfo(4);
        AbstractFunction f1 = GoalFunctions.f1;

        System.out.println("Početna točka: (0.5, 0.5)");
        System.out.println("-".repeat(20));
        for (int i = 1; i <= 20; i++) {
            f1.reset();
            System.out.println("Korak za generiranje simpleksa: " + i);
            evaluateSimplex(new double[]{0.5, 0.5}, f1, i);
        }

        System.out.println("Početna točka: (20, 20)");
        System.out.println("-".repeat(20));
        for (int i = 1; i <= 20; i++) {
            f1.reset();
            System.out.println("Korak za generiranje simpleksa: " + i);
            evaluateSimplex(new double[]{20, 20}, f1, i);
        }
    }

    private static void zad5() {
        printTaskInfo(5);

        AbstractFunction f6 = GoalFunctions.f6;
        System.out.println("Traženje optimuma pomoću simpleksa i postupka Hooke-Jeeves:");
        double optimumsSimplex = 0;
        double optimumsHookeJeeves = 0;
        int N = 10000;
        for (int i = 0; i < N; i++) {
            f6.reset();
            double[] x0 = {Math.random() * 100 - 50, Math.random() * 100 - 50};
            double[] x = OptimizationAlgorithms.simplexAlgorithm(x0, f6, false);
            double[] x1 = OptimizationAlgorithms.hookeJeevesAlgorithm(x0, f6, false);
            if (f6.valueAt(x) < 10e-4) optimumsSimplex++;
            if (f6.valueAt(x1) < 10e-4) optimumsHookeJeeves++;
        }

        System.out.println(String.format("Vjerojatnost da je simpleksom nađen globalni optimum: %.2f%%", (optimumsSimplex / N) * 100));
        System.out.println(String.format("Vjerojatnost da je postupkom Hooke-Jeeves nađen globalni optimum: %.2f%%", (optimumsHookeJeeves / N) * 100));
    }


    private static void printTaskInfo(int taskNumber) {
        System.out.println();
        System.out.println("=".repeat(15));
        System.out.println(taskNumber + ". zadatak");
        System.out.println("=".repeat(15));
        System.out.println();
    }

    private static void evaluateFirst(double[] x0) {
        AbstractFunction f3 = new AbstractFunction(x -> pow(x[0] - 3, 2));

        System.out.println("a) Pretraživanje zlatnim rezom:\n");
        double x1 = OptimizationAlgorithms.goldenRatioAlgorithm(x0[0], f3, false);
        System.out.println(String.format("Minimum: %.3f", x1));
        System.out.println("Broj evaluacija funkcije: " + f3.getCounter());

        f3.reset();
        System.out.println("b) Pretraživanje po koordinatnim osima:\n");
        evaluateCoordinateSearch(x0, f3);

        f3.reset();
        System.out.println("c) Simpleks postupak po Nedleru i Meadu:\n");
        evaluateSimplex(x0, f3);

        f3.reset();
        System.out.println("d) Postupak Hooke-Jeeves:\n");
        evaluateHookeJeeves(x0, f3);
    }

    private static void evaluateSecond(AbstractFunction f, double[] x0) {
        f.reset();
        System.out.println("a) Simplex po Nedleru i Meadu:");
        evaluateSimplex(x0, f);
        f.reset();
        System.out.println("b) Hooke-Jeeves postupak:");
        evaluateHookeJeeves(x0, f);
        f.reset();
        System.out.println("c) Pretraživanje po koordinatnim osima:");
        evaluateCoordinateSearch(x0, f);
    }

    private static void evaluateHookeJeeves(double[] x0, AbstractFunction f) {
        double[] x = OptimizationAlgorithms.hookeJeevesAlgorithm(x0, f, false);
        System.out.println("Minimum: (" + OptimizationAlgorithms.arrayString(x) + ")");
        System.out.println("Broj evaluacija funkcija: " + f.getCounter() + "\n");
    }

    private static void evaluateSimplex(double[] x0, AbstractFunction f) {
        double[] x = OptimizationAlgorithms.simplexAlgorithm(x0, f, false);
        System.out.println("Minimum: (" + OptimizationAlgorithms.arrayString(x) + ")");
        System.out.println("Broj evaluacija funkcija: " + f.getCounter() + "\n");
    }

    private static void evaluateSimplex(double[] x0, AbstractFunction f, double t) {
        double[] x = OptimizationAlgorithms.simplexAlgorithm(x0, t, f, false);
        System.out.println("Minimum: (" + OptimizationAlgorithms.arrayString(x) + ")");
        System.out.println("Broj evaluacija funkcija: " + f.getCounter() + "\n");
    }

    private static void evaluateCoordinateSearch(double[] x0, AbstractFunction f) {
        double[] x = OptimizationAlgorithms.coordinateSearchAlgorithm(x0, f);
        System.out.println("Minimum: (" + OptimizationAlgorithms.arrayString(x) + ")");
        System.out.println("Broj evaluacija funkcija: " + f.getCounter() + "\n");
    }
}
