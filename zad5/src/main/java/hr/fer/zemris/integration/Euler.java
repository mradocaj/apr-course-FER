package hr.fer.zemris.integration;

import hr.fer.zemris.linear.Matrix;
import hr.fer.zemris.math.FunctionMatrix;

import static hr.fer.zemris.integration.IntegrationUtil.iterate;

public class Euler implements Integrator, Predictor {

    @Override
    public Matrix integrate(Matrix A, Matrix B, FunctionMatrix rt, Matrix x0, double T, double tMax, int iterations, String path, boolean error) {
        Matrix x = x0.copy();

        Matrix U = Matrix.identityMatrix(x.getRowsCount());
        Matrix P = U.nAdd(A.copy().scalarMultiply(T));
        Matrix Q = B.copy().scalarMultiply(T);

        x = iterate(tMax, T, P, Q, rt, x, x0, iterations, error, path);

        return x;
    }


    @Override
    public Matrix predict(Matrix A, Matrix B, FunctionMatrix rt, Matrix x0, double T) {
        Matrix x = x0.copy();

        Matrix U = Matrix.identityMatrix(x.getRowsCount());
        Matrix P = U.nAdd(A.copy().scalarMultiply(T));
        Matrix Q = B.copy().scalarMultiply(T);

        return P.nMultiply(x).nAdd(Q.nMultiply(rt));
    }

}
