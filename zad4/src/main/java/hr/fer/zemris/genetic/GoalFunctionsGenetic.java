package hr.fer.zemris.genetic;

import hr.fer.zemris.optimization.AbstractFunction;

import static java.lang.Math.*;

public class GoalFunctionsGenetic {

    public static final AbstractFunction f1 = new AbstractFunction(x -> 100 * pow(x[1] - pow(x[0], 2), 2) + pow(1 - x[0], 2));

    public static final AbstractFunction f3 = new AbstractFunction(x -> {
        double sum = 0;
        for(int i = 0; i < x.length; i++) {
            sum += pow(x[i] - i - 1, 2);
        }
        return sum;
    });

    public static final AbstractFunction f6 = new AbstractFunction(x -> {
        double sum = 0;
        for (double v : x) {
            sum += pow(v, 2);
        }
        return 0.5 + (pow(sin(sqrt(sum)), 2) - 0.5) / pow(1 + 0.001 * sum, 2);
    });

    public static final AbstractFunction f7 = new AbstractFunction(x -> {
        double sum = 0;
        for (double v : x) {
            sum += pow(v, 2);
        }
        return pow(sum, 0.25) * (1 + pow(sin(50 * pow(sum, 0.1)), 2));
    });

}
