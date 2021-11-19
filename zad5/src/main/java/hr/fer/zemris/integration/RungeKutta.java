package hr.fer.zemris.integration;

import hr.fer.zemris.linear.Matrix;
import hr.fer.zemris.math.FunctionMatrix;

import static hr.fer.zemris.integration.IntegrationUtil.*;

public class RungeKutta implements Integrator {

    @Override
    public Matrix integrate(Matrix A, Matrix B, FunctionMatrix rt, Matrix x0, double T, double tMax, int iterations, String path, boolean error) {
        Matrix x = x0.copy();
        double errorSum = 0;
        int numberOfIterations = 0;
        StringBuilder sb = new StringBuilder();

        for (double t = T; t <= tMax; t += T) {
            rt.setT(t);
            x = calculateNextRungeKutta(x, A, B, rt, t, T);

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

    private Matrix calculateNextRungeKutta(Matrix x, Matrix A, Matrix B, FunctionMatrix rt, double t, double T) {
        Matrix m1 = A.nMultiply(x).nAdd(B.nMultiply(rt));
        Matrix m2 = A.nMultiply(x.nAdd(m1.copy().scalarMultiply(T/2))).nAdd(B.nMultiply(rt.copy().setT(t + T/2)));
        Matrix m3 = A.nMultiply(x.nAdd(m2.copy().scalarMultiply(T/2))).nAdd(B.nMultiply(rt.copy().setT(t + T/2)));
        Matrix m4 = A.nMultiply(x.nAdd(m3.copy().scalarMultiply(T))).nAdd(B.nMultiply(rt.copy().setT(t + T)));

        return x.copy().nAdd(m1.nAdd(m2.scalarMultiply(2).nAdd(m3.scalarMultiply(2)).nAdd(m4)).copy().scalarMultiply(T/6));
    }
}
