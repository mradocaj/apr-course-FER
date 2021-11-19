package hr.fer.zemris.integration;

import hr.fer.zemris.linear.Matrix;
import hr.fer.zemris.math.Function;
import hr.fer.zemris.math.FunctionMatrix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        initialize();
        zad1();
        zad2();
        zad3();
        zad4();
    }

    private static void initialize() {
        try {
            Files.deleteIfExists(Path.of("src/main/resources/zad1.csv"));
            Files.deleteIfExists(Path.of("src/main/resources/zad2.csv"));
            Files.deleteIfExists(Path.of("src/main/resources/zad3.csv"));
            Files.deleteIfExists(Path.of("src/main/resources/zad4.csv"));
            Files.createFile(Path.of("src/main/resources/zad1.csv"));
            Files.createFile(Path.of("src/main/resources/zad2.csv"));
            Files.createFile(Path.of("src/main/resources/zad3.csv"));
            Files.createFile(Path.of("src/main/resources/zad4.csv"));
        } catch (IOException ex) {
            System.out.println("Greška pri učitavanju datoteka.");
        }

    }
    private static void zad1() {
        printTaskInfo(1);
        Matrix A = Matrix.readFromFile("src/main/resources/A1.txt");
        Matrix B = Matrix.readFromFile("src/main/resources/B1.txt");
        Matrix x0 = Matrix.readFromFile("src/main/resources/X1.txt");
        FunctionMatrix rt = getIdentityRt();

        calculateAll(A, B, rt, x0, 0.01, 10, 5, "src/main/resources/zad1.csv", true);
    }

    private static void zad2() {
        printTaskInfo(2);
        Matrix A = Matrix.readFromFile("src/main/resources/A2.txt");
        Matrix B = Matrix.readFromFile("src/main/resources/B2.txt");
        Matrix x0 = Matrix.readFromFile("src/main/resources/X2.txt");
        FunctionMatrix rt = getIdentityRt();

        calculateAll(A, B, rt, x0, 0.1, 1, 1, "src/main/resources/zad2.csv", false);

        checkStability(A, B, rt, x0);
    }

    private static void zad3() {
        printTaskInfo(3);
        Matrix A = Matrix.readFromFile("src/main/resources/A3.txt");
        Matrix B = Matrix.readFromFile("src/main/resources/B3.txt");
        Matrix x0 = Matrix.readFromFile("src/main/resources/X3.txt");
        FunctionMatrix rt = getIdentityRt();

        calculateAll(A, B, rt, x0, 0.01, 10, 5, "src/main/resources/zad3.csv", false);
    }

    private static void zad4() {
        printTaskInfo(4);
        Matrix A = Matrix.readFromFile("src/main/resources/A4.txt");
        Matrix B = Matrix.readFromFile("src/main/resources/B4.txt");
        Matrix x0 = Matrix.readFromFile("src/main/resources/X4.txt");

        FunctionMatrix rt = getRt();

        calculateAll(A, B, rt, x0, 0.01, 1, 5, "src/main/resources/zad4.csv", false);
    }


    private static void calculateAll(Matrix A, Matrix B, FunctionMatrix rt, Matrix x0, double T, double tMax, int iterations, String path, boolean error) {
        Integrator euler = new Euler();
        System.out.println("\nEulerov postupak");
        System.out.println("=".repeat(30) + "\n");
        System.out.println("\n" + euler.integrate(A, B, rt, x0, T, tMax, iterations, path, error));

        Integrator reverseEuler = new ReverseEuler();
        System.out.println("\nObrnuti Eulerov postupak");
        System.out.println("=".repeat(30) + "\n");
        System.out.println("\n" + reverseEuler.integrate(A, B, rt, x0, T, tMax, iterations, path, error));

        Integrator trapezoid = new Trapezoid();
        System.out.println("\nTrapezni postupak");
        System.out.println("=".repeat(30) + "\n");
        System.out.println("\n" + trapezoid.integrate(A, B, rt, x0, T, tMax, iterations, path, error));

        Integrator rungeKutta = new RungeKutta();
        System.out.println("\nRunge-Kutta postupak");
        System.out.println("=".repeat(30) + "\n");
        System.out.println("\n" + rungeKutta.integrate(A, B, rt, x0, T, tMax, iterations, path, error));

        Integrator pece2 = new PredictorCorrector(new Euler(), new ReverseEuler(), 2);
        System.out.println("\nPrediktorsko-korektivni postupak (prediktor = Euler, korektor = obrnuti Euler)");
        System.out.println("=".repeat(30) + "\n");
        System.out.println("\n" + pece2.integrate(A, B, rt, x0, T, tMax, iterations, path, error));

        Integrator pece = new PredictorCorrector(new Euler(), new Trapezoid(), 1);
        System.out.println("\nPrediktorsko-korektivni postupak (prediktor = Euler, korektor = trapezni postupak)");
        System.out.println("=".repeat(30) + "\n");
        System.out.println("\n" + pece.integrate(A, B, rt, x0, T, tMax, iterations, path, error));
    }

    private static FunctionMatrix getIdentityRt() {
        List<Function> functions = new ArrayList<>();
        functions.add(t -> 1);
        functions.add(t -> 1);

        return new FunctionMatrix(functions.size(), 1, functions);
    }

    private static FunctionMatrix getRt() {
        List<Function> functions = new ArrayList<>();
        functions.add(t -> t[0]);
        functions.add(t -> t[0]);

        return new FunctionMatrix(functions.size(), 1, functions);
    }

    private static void checkStability(Matrix A, Matrix B, FunctionMatrix rt, Matrix x0) {
        System.out.println("Provjera stabilnosti za Runge-Kutta:");
        Integrator rungeKutta = new RungeKutta();
        for(double T = 0.01; T <= 0.08; T += 0.01) {
            System.out.println(String.format("T = %.2f", T));
            System.out.println("-".repeat(40));
            rungeKutta.integrate(A, B, rt, x0, T, 2, 10, "", false);
        }
    }
    private static void printTaskInfo(int taskNumber) {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println(taskNumber + ". zadatak");
        System.out.println("=".repeat(60));
        System.out.println();
    }

}
