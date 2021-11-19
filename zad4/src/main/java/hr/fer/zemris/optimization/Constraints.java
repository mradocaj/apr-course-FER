package hr.fer.zemris.optimization;

public class Constraints {

    public static AbstractFunction c1 = new AbstractFunction(x -> x[1] - x[0]);

    public static AbstractFunction c2 = new AbstractFunction(x -> 2 - x[0]);

    public static AbstractFunction c3 = new AbstractFunction(x -> 3 - x[0] - x[1]);

    public static AbstractFunction c4 = new AbstractFunction(x -> 3 + 1.5 * x[0] - x[1]);

    public static AbstractFunction c5 = new AbstractFunction(x -> x[1] - 1);

}
