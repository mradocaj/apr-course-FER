package hr.fer.zemris.optimization;

import static java.lang.Math.*;

public class GoalFunctions {

    public static final AbstractFunction f1 = new AbstractFunction(x -> 100 * pow(x[1] - pow(x[0], 2), 2) + pow(1 - x[0], 2));

    public static final AbstractFunction f2 = new AbstractFunction(x -> pow(x[0] - 4, 2) + 4 * pow(x[1] - 2, 2));

    public static final AbstractFunction f3 = new AbstractFunction(x -> {
        double sum = 0;
        for(int i = 0; i < x.length; i++) {
            sum += pow(x[i] - i - 1, 2);
        }
        return sum;
    });

    public static final AbstractFunction f4 = new AbstractFunction(x -> abs((x[0] - x[1]) * (x[0] + x[1])) + sqrt(x[0] * x[0] + x[1] * x[1]));

    public static final AbstractFunction f6 = new AbstractFunction(x -> {
        double sum = 0;
        for (double v : x) {
            sum += pow(v, 2);
        }
        return 0.5 + (pow(sin(sqrt(sum)), 2) - 0.5) / pow(1 + 0.001 * sum, 2);
    });

}
