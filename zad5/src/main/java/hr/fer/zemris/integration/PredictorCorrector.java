package hr.fer.zemris.integration;

import hr.fer.zemris.linear.Matrix;
import hr.fer.zemris.math.FunctionMatrix;

import static hr.fer.zemris.integration.IntegrationUtil.*;

public class PredictorCorrector implements Integrator {

    private Predictor predictor;
    private Corrector corrector;
    private int s;

    public PredictorCorrector(Predictor predictor, Corrector corrector, int s) {
        this.predictor = predictor;
        this.corrector = corrector;
        this.s = s;
    }


    @Override
    public Matrix integrate(Matrix A, Matrix B, FunctionMatrix rt, Matrix x0, double T, double tMax, int iterations, String path, boolean error) {
        Matrix x = x0.copy();
        double errorSum = 0;
        int numberOfIterations = 0;
        StringBuilder sb = new StringBuilder();

        for (double t = T; t <= tMax; t += T) {
            rt.setT(t);
            x = calculateNext(x, A, B, rt, T);

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

    private Matrix calculateNext(Matrix x, Matrix A, Matrix B, FunctionMatrix rt, double T) {
        Matrix xNext = predictor.predict(A, B, rt, x, T);
        for(int j = 0; j < s; j++) {
            xNext = corrector.correct(A, B, rt, x, xNext, T);
        }

        return xNext;
    }

}
