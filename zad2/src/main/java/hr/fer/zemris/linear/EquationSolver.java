package hr.fer.zemris.linear;

import hr.fer.zemris.linear.exceptions.IllegalMatrixFormatException;
import hr.fer.zemris.linear.exceptions.NoResultException;

public class EquationSolver {

    public static Matrix solve(Matrix A, Matrix b, boolean pivoting, boolean info) {
        try {
            if(info) {
                System.out.println("Solving equation system A * x = b " + (pivoting ? "using" : "without using") + " pivoting.\n");
                System.out.println("A = \n" + A);
                System.out.println("b = \n" + b);
            }

            Matrix LU = A.copy();
            if(info) {
                System.out.println("Applying " + (pivoting ? "LUP" : "LU") + " decomposition...");
            }
            Matrix P = LU.LUDecomposition(pivoting);
            if(info) {
                System.out.println("LU = \n" + LU);
                if(pivoting) {
                    System.out.println("P = \n" + P);
                }
            }

            if(info) {
                System.out.println("Solving L * y = " + (pivoting ? "P * b..." : "b..."));
            }
            Matrix y = LU.forwardSubstitution(P.nMultiply(b));
            if(info) {
                System.out.println("y = \n" + y);
            }

            if(info) {
                System.out.println("Solving U * x = y...");
            }
            Matrix x = LU.backwardSubstitution(y);
            if(info) {
                System.out.println("x = \n" + x);
            }

            return x;
        } catch (NoResultException | IllegalMatrixFormatException ex) {
            if(info) {
                System.out.println(ex.getMessage());
            }
        }

        return null;
    }

}
