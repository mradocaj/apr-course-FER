package hr.fer.zemris.optimization;

import hr.fer.zemris.linear.EquationSolver;
import hr.fer.zemris.linear.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

public class OptimizationAlgorithms {
    private static final double E = 10e-6;
    private static final double ALPHA = 1;
    private static final double BETA = 0.5;
    private static final double GAMMA = 2;
    private static final double SIGMA = 0.5;
    private static final double DX = 0.5;
    private static final int NUMBER_OF_ITERATIONS = 100;
    private static final double BOX_ALPHA = 1.3;
    private static final double T = 1;
    private static final double T_FACTOR = 10;

    public static double[] mixTransformation(double[] x0, AbstractFunction f, List<AbstractFunction> inequalityConstraints, List<AbstractFunction> equalityConstraints) {
        return mixTransformation(x0, E, T, f, inequalityConstraints, equalityConstraints);
    }

    public static double[] mixTransformation(double[] x0, double e, double t, AbstractFunction f, List<AbstractFunction> inequalityConstraints, List<AbstractFunction> equalityConstraints) {
        double[] x = Arrays.copyOf(x0, x0.length);

        if (notAllConstraintsSatisfied(x0, inequalityConstraints)) {
            x = findInnerPoint(x0, inequalityConstraints);
        }

        do {
            double[] xs = Arrays.copyOf(x, x.length);
            double finalT = t;

            AbstractFunction U = new AbstractFunction(xu -> {
                double inequalitySum = 0;
                double equalitySum = 0;

                for (AbstractFunction inequalityConstraint : inequalityConstraints) {
                    double value = inequalityConstraint.valueAt(xu);
                    if (value < 0) {
                        inequalitySum = Double.NEGATIVE_INFINITY;
                        break;
                    } else {
                        inequalitySum += log(value);
                    }
                }
                for (AbstractFunction equalityConstraint : equalityConstraints) {
                    double value = equalityConstraint.valueAt(xu);
                    equalitySum += pow(value, 2);
                }

                return f.valueAt(xu) - 1 / finalT * inequalitySum + finalT * equalitySum;
            });

            x = hookeJeevesAlgorithm(x, U, false);
            boolean end = true;
            for (int i = 0; i < x.length; i++) {
                if (abs(x[i] - xs[i]) > e) {
                    end = false;
                    break;
                }
            }
            if (end) break;
            t *= T_FACTOR;

        } while (true);

        return x;
    }

    public static double[] boxAlgorithm(double[] x0, AbstractFunction f, List<AbstractFunction> constraints, double xd, double xg) {
        return boxAlgorithm(x0, E, BOX_ALPHA, f, constraints, xd, xg);
    }

    public static double[] boxAlgorithm(double[] x0, double e, double alpha, AbstractFunction f, List<AbstractFunction> constraints, double xd, double xg) {
        double[] x = Arrays.copyOf(x0, x0.length);
        int iterations = 0;
        double minFunctionValue = f.valueAt(x);

        for (double v : x) {
            if (v < xd || v > xg) return x;
        }
        if (notAllConstraintsSatisfied(x0, constraints)) {
            x = findInnerPoint(x0, constraints);
        }

        double[][] points = new double[x.length * 2][x.length];
        double[] xc = Arrays.copyOf(x, x.length);
        for (int i = 0; i < 2 * x0.length; i++) {
            for (int j = 0; j < x0.length; j++) {
                points[i][j] = xd + Math.random() * (xg - xd);
            }
            while (notAllConstraintsSatisfied(points[i], constraints)) {
                points[i] = moveToCentroid(points[i], xc);
            }
            xc = getCentroid(points, -1);
        }

        do {
            int h = 0;
            int h2 = 1;
            double max = -Double.MAX_VALUE;
            for (int i = 0; i < points.length; i++) {
                double value = f.valueAt(points[i]);
                if (value > max) {
                    max = value;
                    h2 = h;
                    h = i;
                }
            }
            xc = getCentroid(points, h);
            double[] xr = reflexion(xc, points[h], alpha);
            for (int i = 0; i < xr.length; i++) {
                if (xr[i] < xd) xr[i] = xd;
                else if (xr[i] > xg) xr[i] = xg;
            }
            while (notAllConstraintsSatisfied(xr, constraints)) {
                xr = moveToCentroid(xr, xc);
            }
            if (f.valueAt(xr) > f.valueAt(points[h2])) {
                xr = moveToCentroid(xr, xc);
            }

            points[h] = xr;

            if (f.valueAt(x) < minFunctionValue) {
                minFunctionValue = f.valueAt(x);
                iterations = 0;
            } else {
                iterations++;
                if (iterations == NUMBER_OF_ITERATIONS * 10) {
                    System.out.println("Postupak divergira.");
                    break;
                }
            }
        } while (!(sqrt(mse(points, xc, f)) < e));

        return xc;
    }

    private static double[] moveToCentroid(double[] point, double[] xc) {
        double[] newPoint = new double[point.length];
        for (int i = 0; i < point.length; i++) {
            newPoint[i] = 1.0 / 2 * (point[i] + xc[i]);
        }

        return newPoint;
    }

    private static double[] findInnerPoint(double[] x0, List<AbstractFunction> constraints) {
        AbstractFunction g = new AbstractFunction(x -> {
            double sum = 0;
            for (AbstractFunction constraint : constraints) {
                if (constraint.valueAt(x) < 0) {
                    sum -= constraint.valueAt(x);
                }
            }
            return sum;
        });

        return hookeJeevesAlgorithm(x0, g, false);
    }

    private static boolean notAllConstraintsSatisfied(double[] x0, List<AbstractFunction> constraints) {
        for (AbstractFunction constraint : constraints) {
            if (constraint.valueAt(x0) < 0) {
                return true;
            }
        }

        return false;
    }

    public static double[] gradientDescent(double[] x0, boolean goldenRatio, AbstractFunction f) {
        return gradientDescent(x0, E, goldenRatio, f);
    }

    public static double[] gradientDescent(double[] x0, double e, boolean goldenRatio, AbstractFunction f) {
        List<AbstractFunction> derivatives = new ArrayList<>();
        for (int i = 0; i < x0.length; i++) {
            derivatives.add(GoalFunctionsDerivations.getDerivative(i, f));
        }

        double[] x = Arrays.copyOf(x0, x0.length);
        double[] ei = new double[x.length];
        double[] gradient = new double[x0.length];
        int iterations = 0;
        double minFunctionValue = f.valueAt(x);

        do {
            for (int i = 0; i < x.length; i++) {
                gradient[i] = derivatives.get(i).valueAt(x);
            }

            if (goldenRatio) {
                for (int i = 0; i < x.length; i++) {
                    ei[i] = gradient[i];
                    double lambdaMin = goldenRatioAlgorithm(x[i], new AbstractFunction(value -> {
                        double[] copy = Arrays.copyOf(x, x.length);
                        for (int k = 0; k < x.length; k++) {
                            if (ei[k] != 0) {
                                copy[k] += ei[k] * value[0];
                                break;
                            }
                        }
                        return f.valueAt(copy);
                    }), false);
                    x[i] += ei[i] * lambdaMin;
                    ei[i] = 0;
                }
            } else {
                for (int i = 0; i < x.length; i++) {
                    x[i] -= gradient[i];
                }
            }

            if (f.valueAt(x) < minFunctionValue) {
                minFunctionValue = f.valueAt(x);
                iterations = 0;
            } else {
                iterations++;
                if (iterations == NUMBER_OF_ITERATIONS) {
                    System.out.println("Postupak divergira.");
                    break;
                }
            }

        } while (euclideanNorm(gradient) > e);

        return x;
    }

    public static double[] newtonRaphson(double[] x0, boolean goldenRatio, AbstractFunction f) {
        return newtonRaphson(x0, E, goldenRatio, f);
    }

    public static double[] newtonRaphson(double[] x0, double e, boolean goldenRatio, AbstractFunction f) {
        List<AbstractFunction> derivatives = new ArrayList<>();
        for (int i = 0; i < x0.length; i++) {
            derivatives.add(GoalFunctionsDerivations.getDerivative(i, f));
        }
        List<AbstractFunction> secondDerivatives = new ArrayList<>();
        for (int i = 0; i < x0.length; i++) {
            for (int j = 0; j < x0.length; j++) {
                secondDerivatives.add(GoalFunctionsDerivations.getDerivative(j, derivatives.get(i)));
            }
        }

        double[] x = Arrays.copyOf(x0, x0.length);
        double[] ei = new double[x.length];
        double[][] gradient = new double[x0.length][1];
        double[][] h = new double[x.length][x.length];
        double[] deltaElements = new double[x.length];
        int iterations = 0;
        double minFunctionValue = f.valueAt(x);

        do {
            for (int i = 0; i < x.length; i++) {
                gradient[i][0] = derivatives.get(i).valueAt(x);
                for (int j = 0; j < x.length; j++) {
                    h[i][j] = secondDerivatives.get(i * 2 + j).valueAt(x);
                }
            }

            Matrix dF = new Matrix(gradient, x.length, 1, true);
            Matrix H = new Matrix(h, x.length, x.length, true);
            Matrix deltaX = EquationSolver.solve(H, dF.scalarMultiply(-1), true, false);
            for (int i = 0; i < x.length; i++) {
                deltaElements[i] = deltaX.get(i, 0);
            }

            if (goldenRatio) {
                for (int i = 0; i < x.length; i++) {
                    ei[i] = deltaElements[i];
                    double lambdaMin = goldenRatioAlgorithm(x[i], new AbstractFunction(value -> {
                        double[] copy = Arrays.copyOf(x, x.length);
                        for (int k = 0; k < x.length; k++) {
                            if (ei[k] != 0) {
                                copy[k] += ei[k] * value[0];
                                break;
                            }
                        }
                        return f.valueAt(copy);
                    }), false);
                    x[i] += ei[i] * lambdaMin;
                    ei[i] = 0;
                }
            } else {
                for (int i = 0; i < x.length; i++) {
                    x[i] += deltaElements[i];
                }
            }

            if (f.valueAt(x) < minFunctionValue) {
                minFunctionValue = f.valueAt(x);
                iterations = 0;
            } else {
                iterations++;
                if (iterations == NUMBER_OF_ITERATIONS) {
                    System.out.println("Postupak divergira.");
                    break;
                }
            }

        } while (euclideanNorm(deltaElements) > e);

        return x;
    }

    private static double euclideanNorm(double[] x) {
        double sum = 0;
        for (double element : x) {
            sum += element * element;
        }
        return sqrt(sum);
    }

    public static double[] unimodalInterval(double x0, double h, AbstractFunction f) {
        double l = x0 - h;
        double r = x0 + h;
        double m = x0;
        long step = 1;

        double fm = f.valueAt(x0);
        double fl = f.valueAt(l);
        double fr = f.valueAt(r);

        if (fm >= fr || fm >= fl) {
            if (fm > fr) {
                while (fm > fr) {
                    l = m;
                    m = r;
                    fm = fr;
                    r = x0 + h * (step *= 2);
                    fr = f.valueAt(r);
                }
            } else {
                while (fm > fl) {
                    r = m;
                    m = l;
                    fm = fl;
                    l = x0 - h * (step *= 2);
                    fl = f.valueAt(l);
                }
            }
        }

        return new double[]{l, r};
    }

    public static double goldenRatioAlgorithm(double a, double b, double e, AbstractFunction f, boolean trace) {
        double k = 0.5 * (Math.sqrt(5) - 1);

        double c = b - k * (b - a);
        double d = a + k * (b - a);
        double fc = f.valueAt(c);
        double fd = f.valueAt(d);

        if (trace) {
            printGoldenRatioInfo(a, b, c, d, f);
        }

        while ((b - a) > e) {
            if (fc < fd) {
                b = d;
                d = c;
                c = b - k * (b - a);
                fd = fc;
                fc = f.valueAt(c);
            } else {
                a = c;
                c = d;
                d = a + k * (b - a);
                fc = fd;
                fd = f.valueAt(d);
            }
            if (trace) {
                printGoldenRatioInfo(a, b, c, d, f);
            }
        }

        return (a + b) / 2;
    }

    private static void printGoldenRatioInfo(double a, double b, double c, double d, AbstractFunction f) {
        System.out.println(String.format("a = %.3f, c = %.3f, d = %.3f, b = %.3f", a, c, d, b));
        System.out.println(String.format("f(a) = %.3f, f(c) = %.3f, f(d) = %.3f, f(b) = %.3f", f.valueAt(a), f.valueAt(c), f.valueAt(d), f.valueAt(b)));
        System.out.println();
    }

    public static double goldenRatioAlgorithm(double x0, double h, AbstractFunction f, double e, boolean trace) {
        double[] unimodalInterval = unimodalInterval(x0, h, f);
        return goldenRatioAlgorithm(unimodalInterval[0], unimodalInterval[1], e, f, trace);
    }

    public static double goldenRatioAlgorithm(double x0, AbstractFunction f, boolean trace) {
        return goldenRatioAlgorithm(x0, 1, f, E, trace);
    }

    public static double goldenRatioAlgorithm(double a, double b, AbstractFunction f, boolean trace) {
        return goldenRatioAlgorithm(a, b, E, f, trace);
    }

    public static double[] coordinateSearchAlgorithm(double[] x0, double[] e, AbstractFunction f) {
        double[] x = Arrays.copyOf(x0, x0.length);
        double[] ei = new double[x.length];

        while (true) {
            double[] xs = Arrays.copyOf(x, x.length);
            for (int i = 0; i < x.length; i++) {
                ei[i] = 1;
                double lambdaMin = goldenRatioAlgorithm(xs[i], new AbstractFunction(value -> {
                    double[] copy = Arrays.copyOf(x, x.length);
                    for (int k = 0; k < x.length; k++) {
                        if (ei[k] == 1) {
                            copy[k] += value[0];
                            break;
                        }
                    }
                    return f.valueAt(copy);
                }), false);
                x[i] += lambdaMin;
                ei[i] = 0;
            }

            boolean end = true;
            for (int i = 0; i < x.length; i++) {
                if (abs(x[i] - xs[i]) > e[i]) {
                    end = false;
                    break;
                }
            }
            if (end) break;
        }

        return x;
    }

    public static double[] coordinateSearchAlgorithm(double[] x0, AbstractFunction f) {
        double[] eps = new double[x0.length];
        Arrays.fill(eps, E);
        return coordinateSearchAlgorithm(x0, eps, f);
    }

    public static double[][] getStartingSimplex(double[] x0, double t) {
        double[][] simplex = new double[x0.length + 1][x0.length];
        for (int i = 0; i < x0.length; i++) {
            for (int j = 0; j < x0.length; j++) {
                if (i == j) {
                    simplex[i][j] = x0[j] + t;
                } else {
                    simplex[i][j] = x0[j];
                }
            }
        }
        simplex[x0.length] = x0;

        return simplex;
    }

    public static double[] getCentroid(double[][] x, int h) {
        double[] xc = new double[x[0].length];
        for (int i = 0; i < x.length; i++) {
            if (i == h) continue;
            for (int j = 0; j < xc.length; j++) {
                xc[j] += x[i][j];
            }
        }
        for (int i = 0; i < xc.length; i++) {
            if (h == -1) {
                xc[i] /= x.length;
            } else {
                xc[i] /= x.length - 1;
            }
        }

        return xc;
    }

    private static double[] reflexion(double[] xc, double[] xh, double alpha) {
        double[] xr = new double[xc.length];
        for (int i = 0; i < xr.length; i++) {
            xr[i] = (1 + alpha) * xc[i] - alpha * xh[i];
        }

        return xr;
    }

    private static double[] expansion(double[] xc, double[] xr, double gamma) {
        double[] xe = new double[xc.length];
        for (int i = 0; i < xe.length; i++) {
            xe[i] = (1 - gamma) * xc[i] + gamma * xr[i];
            //xe[i] = (1 - gamma) * xc[i] - gamma * xr[i];  //ovako smo na predavanjima napisali
        }

        return xe;
    }

    private static double[] contraction(double[] xc, double[] xh, double beta) {
        double[] xk = new double[xc.length];
        for (int i = 0; i < xk.length; i++) {
            xk[i] = (1 - beta) * xc[i] + beta * xh[i];
        }

        return xk;
    }

    private static double mse(double[][] x, double[] xc, AbstractFunction f) {
        double sum = 0;
        for (int j = 0; j < x.length; j++) {
            sum += pow(f.valueAt(x[j]) - f.valueAt(xc), 2);
        }

        return (1.0 / x.length) * sum;
    }

    public static double[] simplexAlgorithm(double[] x0, double alpha, double beta, double gamma, double sigma, double e, AbstractFunction f, double t, boolean trace) {
        double[][] x = getStartingSimplex(x0, t);
        double[] xc;

        do {
            int h = 0;
            int l = 0;
            double max = -Double.MAX_VALUE;
            double min = Double.MAX_VALUE;
            for (int i = 0; i < x.length; i++) {
                double value = f.valueAt(x[i]);
                if (value > max) {
                    max = value;
                    h = i;
                } else if (value < min) {
                    min = value;
                    l = i;
                }
            }
            xc = getCentroid(x, h);
            double[] xr = reflexion(xc, x[h], alpha);

            if (f.valueAt(xr) < f.valueAt(x[l])) {
                double[] xe = expansion(xc, xr, gamma);
                if (f.valueAt(xe) < f.valueAt(x[l])) {
                    x[h] = xe;
                } else {
                    x[h] = xr;
                }
            } else {
                boolean rLarger = true;
                for (int j = 0; j < x.length; j++) {
                    if (j == h) continue;
                    if (f.valueAt(xr) <= f.valueAt(x[j])) {
                        rLarger = false;
                        break;
                    }
                }
                if (rLarger) {
                    if (f.valueAt(xr) < f.valueAt(x[h])) {
                        x[h] = xr;
                    }
                    double[] xk = contraction(xc, x[h], beta);
                    if (f.valueAt(xk) < f.valueAt(x[h])) {
                        x[h] = xk;
                    } else {
                        //pomakni sve tocke prema x[l]
                        for (int i = 0; i < x.length; i++) {
                            if (i == l) continue;
                            for (int j = 0; j < x.length - 1; j++) {
                                x[i][j] = sigma * (x[l][j] + x[i][j]);
                            }
                        }
                    }
                } else {
                    x[h] = xr;
                }
            }
            if (trace) {
                System.out.println("xc = " + arrayString(xc));
                System.out.println("x = " + arrayString(x[0]) + " " + arrayString(x[1]));
                System.out.println(String.format("f(xc) = %.3f", f.valueAt(xc)));
                System.out.println();
            }
        } while (!(sqrt(mse(x, xc, f)) < e));

        return xc;
    }

    public static double[] simplexAlgorithm(double[] x0, AbstractFunction f, boolean trace) {
        return simplexAlgorithm(x0, ALPHA, BETA, GAMMA, SIGMA, E, f, 1, trace);
    }

    public static double[] simplexAlgorithm(double[] x0, double t, AbstractFunction f, boolean trace) {
        return simplexAlgorithm(x0, ALPHA, BETA, GAMMA, SIGMA, E, f, t, trace);
    }

    public static String arrayString(double[] array) {
        String[] elements = new String[array.length];

        for (int i = 0; i < array.length; i++) {
            elements[i] = String.format("%.3f", array[i]);
        }

        return String.join(", ", elements);
    }

    public static double[] hookeJeevesAlgorithm(double[] x0, double[] dx, double[] e, AbstractFunction f, boolean trace) {
        double[] xp = Arrays.copyOf(x0, x0.length);
        double[] xb = Arrays.copyOf(x0, x0.length);

        while (true) {
            double[] xn = search(xp, dx, f);
            if (trace) {
                System.out.println("Početna točka: xb = " + arrayString(xb));
                System.out.println("Početna točka istraživanja: xp = " + arrayString(xp));
                System.out.println("Točka dobivena istraživanjem: xn = " + arrayString(xn));
                System.out.println("f(xb) = " + f.valueAt(xb));
                System.out.println("f(xp) = " + f.valueAt(xp));
                System.out.println("f(xn) = " + f.valueAt(xn));
                System.out.println();
            }
            if (f.valueAt(xn) < f.valueAt(xb)) {
                for (int i = 0; i < x0.length; i++) {
                    xp[i] = 2 * xn[i] - xb[i];
                }
                xb = xn;
            } else {
                for (int i = 0; i < dx.length; i++) {
                    dx[i] /= 2;
                }
                xp = xb;
            }

            boolean end = true;
            for (int i = 0; i < dx.length; i++) {
                if (dx[i] > e[i]) {
                    end = false;
                    break;
                }
            }
            if (end) break;
        }

        return xb;
    }

    public static double[] hookeJeevesAlgorithm(double[] x0, AbstractFunction f, boolean trace) {
        double[] eps = new double[x0.length];
        Arrays.fill(eps, E);
        double[] dx = new double[x0.length];
        Arrays.fill(dx, DX);
        return hookeJeevesAlgorithm(x0, dx, eps, f, trace);
    }

    private static double[] search(double[] xp, double[] dx, AbstractFunction f) {
        double[] x = Arrays.copyOf(xp, xp.length);
        for (int i = 0; i < xp.length; i++) {
            double p = f.valueAt(x);
            x[i] += dx[i];
            double n = f.valueAt(x);
            if (n > p) {
                x[i] -= 2 * dx[i];
                n = f.valueAt(x);
                if (n > p) {
                    x[i] += dx[i];
                }
            }
        }

        return x;
    }
}
