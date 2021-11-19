package hr.fer.zemris.optimization;

import static java.lang.Math.*;

public class GoalFunctionsDerivations {

    public static final AbstractFunction f1 = new AbstractFunction(x -> 100 * pow(x[1] - pow(x[0], 2), 2) + pow(1 - x[0], 2));

    public static final AbstractFunction f1_dx1 = new AbstractFunction(x -> 2 * (200 * pow(x[0], 3) - 200 * x[0] * x[1] + x[0] - 1));

    public static final AbstractFunction f1_dx1dx1 = new AbstractFunction(x -> 1200 * pow(x[0], 2) - 400 * x[1] + 2);

    public static final AbstractFunction f1_dx1dx2 = new AbstractFunction(x -> -400 * x[0]);

    public static final AbstractFunction f1_dx2 = new AbstractFunction(x -> 200 * (x[1] - pow(x[0], 2)));

    public static final AbstractFunction f1_dx2dx1 = new AbstractFunction(x -> -400 * x[0]);

    public static final AbstractFunction f1_dx2dx2 = new AbstractFunction(x -> 200);

    public static final AbstractFunction f2 = new AbstractFunction(x -> pow(x[0] - 4, 2) + 4 * pow(x[1] - 2, 2));

    public static final AbstractFunction f2_dx1 = new AbstractFunction(x -> 2 * (x[0] - 4));

    public static final AbstractFunction f2_dx1dx1 = new AbstractFunction(x -> 2);

    public static final AbstractFunction f2_dx1dx2 = new AbstractFunction(x -> 0);

    public static final AbstractFunction f2_dx2 = new AbstractFunction(x -> 8 * (x[1] - 2));

    public static final AbstractFunction f2_dx2dx1 = new AbstractFunction(x -> 0);

    public static final AbstractFunction f2_dx2dx2 = new AbstractFunction(x -> 8);

    public static final AbstractFunction f3 = new AbstractFunction(x -> pow(x[0] - 2, 2) + pow(x[1] + 3, 2));

    public static final AbstractFunction f3_dx1 = new AbstractFunction(x -> 2 * (x[0] - 2));

    public static final AbstractFunction f3_dx1dx1 = new AbstractFunction(x -> 2);

    public static final AbstractFunction f3_dx1dx2 = new AbstractFunction(x -> 0);

    public static final AbstractFunction f3_dx2 = new AbstractFunction(x -> 2 * (x[1] + 3));

    public static final AbstractFunction f3_dx2dx1 = new AbstractFunction(x -> 0);

    public static final AbstractFunction f3_dx2dx2 = new AbstractFunction(x -> 2);

    public static final AbstractFunction f4 = new AbstractFunction(x -> pow(x[0] - 3, 2) + pow(x[1], 2));

    public static final AbstractFunction f4_dx1 = new AbstractFunction(x -> 2 * (x[0] - 3));

    public static final AbstractFunction f4_dx1dx1 = new AbstractFunction(x -> 2);

    public static final AbstractFunction f4_dx1dx2 = new AbstractFunction(x -> 0);

    public static final AbstractFunction f4_dx2 = new AbstractFunction(x -> 2 * x[1]);

    public static final AbstractFunction f4_dx2dx1 = new AbstractFunction(x -> 0);

    public static final AbstractFunction f4_dx2dx2 = new AbstractFunction(x -> 2);

    public static AbstractFunction getDerivative(int variable, AbstractFunction f) {
        if (f1.equals(f)) {
            return variable == 0 ? f1_dx1 : f1_dx2;
        } else if (f2.equals(f)) {
            return variable == 0 ? f2_dx1 : f2_dx2;
        } else if (f3.equals(f)) {
            return variable == 0 ? f3_dx1 : f3_dx2;
        } else if (f4.equals(f)) {
            return variable == 0 ? f4_dx1 : f4_dx2;
        } else if (f1_dx1.equals(f)) {
            return variable == 0 ? f1_dx1dx1 : f1_dx1dx2;
        } else if (f2_dx1.equals(f)) {
            return variable == 0 ? f2_dx1dx1 : f2_dx1dx2;
        } else if (f3_dx1.equals(f)) {
            return variable == 0 ? f3_dx1dx1 : f3_dx1dx2;
        } else if (f4_dx1.equals(f)) {
            return variable == 0 ? f4_dx1dx1 : f4_dx1dx2;
        } else if (f1_dx2.equals(f)) {
            return variable == 0 ? f1_dx2dx1 : f1_dx2dx2;
        } else if (f2_dx2.equals(f)) {
            return variable == 0 ? f2_dx2dx1 : f2_dx2dx2;
        } else if (f3_dx2.equals(f)) {
            return variable == 0 ? f3_dx2dx1 : f3_dx2dx2;
        } else if (f4_dx2.equals(f)) {
            return variable == 0 ? f4_dx2dx1 : f4_dx2dx2;
        }
        return null;
    }

}
