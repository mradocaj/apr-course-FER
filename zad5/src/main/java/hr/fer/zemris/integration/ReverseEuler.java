package hr.fer.zemris.integration;

import hr.fer.zemris.linear.Matrix;
import hr.fer.zemris.math.FunctionMatrix;

import static hr.fer.zemris.integration.IntegrationUtil.*;

public class ReverseEuler implements Integrator, Corrector {

    @Override
    public Matrix integrate(Matrix A, Matrix B, FunctionMatrix rt, Matrix x0, double T, double tMax, int iterations, String path, boolean error) {
        Matrix x = x0.copy();

        Matrix U = Matrix.identityMatrix(x.getRowsCount());
        Matrix P = U.nSub(A.copy().scalarMultiply(T)).inverse();
        Matrix Q = U.nSub(A.copy().scalarMultiply(T)).inverse().nMultiply(B.copy().scalarMultiply(T));

        double errorSum = 0;
        int numberOfIterations = 0;
        StringBuilder sb = new StringBuilder();

        for (double t = T; t <= tMax; t += T) {
            rt.setT(t + T);
            Matrix xPrevious = x.copy();
            x = P.nMultiply(xPrevious).nAdd(Q.nMultiply(rt));

            if (numberOfIterations++ % iterations == 0) {
                printResult(x, sb, t);
            }
            if (error) {
                errorSum += calculateError(x, x0, t);
            }
        }

        if (error) {
            System.out.println("Kumulativna pogreška: " + errorSum);
        }
        writeToFile(path, sb);

        return x;
    }

    @Override
    public Matrix correct(Matrix A, Matrix B, Matrix rt, Matrix x, Matrix xNext, double T) {
        Matrix xNextDelta = A.nMultiply(xNext).nAdd(B.nMultiply(rt));

        return x.nAdd(xNextDelta.scalarMultiply(T));
    }

}
