package hr.fer.zemris.integration;

import hr.fer.zemris.linear.Matrix;
import hr.fer.zemris.math.FunctionMatrix;

public interface Integrator {

    Matrix integrate(Matrix A, Matrix B, FunctionMatrix rt, Matrix x0, double T, double tMax, int iterations, String path, boolean error);

}
