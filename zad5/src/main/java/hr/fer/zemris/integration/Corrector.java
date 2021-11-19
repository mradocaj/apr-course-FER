package hr.fer.zemris.integration;

import hr.fer.zemris.linear.Matrix;

public interface Corrector {

    Matrix correct(Matrix A, Matrix B, Matrix rt, Matrix x, Matrix xNext, double T);

}
