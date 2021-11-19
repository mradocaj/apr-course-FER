package hr.fer.zemris.linear;

import hr.fer.zemris.linear.exceptions.NoResultException;

public class Main {

    public static void main(String[] args) {
        zad1();
        zad2();
        zad3();
        zad4();
        zad5();
        zad6();
        zad7();
        zad8();
        zad9();
        zad10();
    }

    private static void zad1() {
        System.out.println("=".repeat(20) + "\n1. zadatak\n" + "=".repeat(20) + "\n");
        Matrix A = Matrix.readFromFile("src/main/resources/A.txt");
        Matrix copy = A.copy();
        System.out.println("A = \n" + A);
        System.out.println("B = \n" + copy);
        System.out.println("B * (1/3) * 3");
        copy.scalarMultiply(1.0 / 3).scalarMultiply(3);

        System.out.println("A == B: " + A.equals(copy) + "\n");
    }

    private static void zad2() {
        System.out.println("=".repeat(20) + "\n2. zadatak\n" + "=".repeat(20) + "\n");
        Matrix B = Matrix.readFromFile("src/main/resources/B.txt");
        Matrix C = Matrix.readFromFile("src/main/resources/C.txt");
        EquationSolver.solve(B, C, false, true);
        System.out.println();
        EquationSolver.solve(B, C, true, true);
    }

    private static void zad3() {
        System.out.println("=".repeat(20) + "\n3. zadatak\n" + "=".repeat(20) + "\n");
        Matrix C = Matrix.readFromFile("src/main/resources/C.txt");
        Matrix D = Matrix.readFromFile("src/main/resources/D.txt");
        EquationSolver.solve(D, C, true, true);
        System.out.println();
        EquationSolver.solve(D, C, false, true);
        System.out.println();
    }

    private static void zad4() {
        System.out.println("=".repeat(20) + "\n4. zadatak\n" + "=".repeat(20) + "\n");
        Matrix E = Matrix.readFromFile("src/main/resources/E.txt");
        Matrix F = Matrix.readFromFile("src/main/resources/F.txt");
        EquationSolver.solve(E, F, true, true);
        System.out.println();
        EquationSolver.solve(E, F, false, true);
        System.out.println();
    }

    private static void zad5() {
        System.out.println("=".repeat(20) + "\n5. zadatak\n" + "=".repeat(20) + "\n");
        Matrix G = Matrix.readFromFile("src/main/resources/G.txt");
        Matrix H = Matrix.readFromFile("src/main/resources/H.txt");
        EquationSolver.solve(G, H, true, true);
        System.out.println();
        EquationSolver.solve(G, H, false, true);
        System.out.println();
    }

    private static void zad6() {
        System.out.println("=".repeat(20) + "\n6. zadatak\n" + "=".repeat(20) + "\n");
        Matrix I = Matrix.readFromFile("src/main/resources/I.txt");
        Matrix J = Matrix.readFromFile("src/main/resources/J.txt");
        EquationSolver.solve(I, J, true, true);
        System.out.println();
        EquationSolver.solve(I, J, false, true);
        System.out.println();
    }

    private static void zad7() {
        System.out.println("=".repeat(20) + "\n7. zadatak\n" + "=".repeat(20) + "\n");
        Matrix K = Matrix.readFromFile("src/main/resources/K.txt");
        System.out.println("Calculating inverse of K...\nK = \n" + K);
        try {
            Matrix inverse = K.inverse();
            System.out.println("Inverse of K: \n" + inverse);
        } catch (NoResultException ex) {
            System.out.println("Inverse of K doesn't exist.\n");
        }
    }

    private static void zad8() {
        System.out.println("=".repeat(20) + "\n8. zadatak\n" + "=".repeat(20) + "\n");
        Matrix L = Matrix.readFromFile("src/main/resources/L.txt");
        System.out.println("Calculating inverse of L...\nL = \n" + L);
        try {
            Matrix inverse = L.inverse();
            System.out.println("Inverse of L: \n" + inverse);
        } catch (NoResultException ex) {
            System.out.println("Inverse of L doesn't exist.\n");
        }
    }

    private static void zad9() {
        System.out.println("=".repeat(20) + "\n9. zadatak\n" + "=".repeat(20) + "\n");
        Matrix L = Matrix.readFromFile("src/main/resources/L.txt");
        System.out.println("Calculating determinant of L...\nL = \n" + L);
        double determinant = L.determinant();
        System.out.println("det(L) = " + determinant + "\n");
    }

    private static void zad10() {
        System.out.println("=".repeat(20) + "\n10. zadatak\n" + "=".repeat(20) + "\n");
        Matrix M = Matrix.readFromFile("src/main/resources/M.txt");
        System.out.println("Calculating determinant of M...\nM = \n" + M);
         double determinant = M.determinant();
        System.out.println("det(M) = " + determinant + "\n");
    }

}
