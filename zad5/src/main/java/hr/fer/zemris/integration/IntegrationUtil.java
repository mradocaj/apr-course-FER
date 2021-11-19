package hr.fer.zemris.integration;

import hr.fer.zemris.linear.Matrix;
import hr.fer.zemris.math.FunctionMatrix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

public class IntegrationUtil {

    public static Matrix iterate(double tMax, double T, Matrix P, Matrix Q, FunctionMatrix rt, Matrix x, Matrix x0, int iterations, boolean error, String path) {
        double errorSum = 0;
        int numberOfIterations = 0;
        StringBuilder sb = new StringBuilder();

        for (double t = T; t <= tMax; t += T) {
            rt.setT(t);
            Matrix xPrevious = x.copy();
            x = P.nMultiply(xPrevious).nAdd(Q.nMultiply(rt));

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

    public static double calculateError(Matrix x, Matrix x0, double t) {
        double x1 = x0.get(0, 0) * cos(t) + x0.get(1, 0) * sin(t);
        double x2 = x0.get(1, 0) * cos(t) - x0.get(0, 0) * sin(t);

        return abs(x.get(0, 0) - x1) + abs(x.get(1, 0) - x2);
    }

    public static void printResult(Matrix x, StringBuilder sb, double t) {
        Matrix transposed = x.transpose();
        System.out.print(transposed);
        sb.append(String.format("%.6f", t)).append(",")
                .append(String.format("%.6f", x.get(0, 0)))
                .append(String.format(",%.6f", x.get(1, 0)))
                .append("\n");
    }

    public static void writeToFile(String filename, StringBuilder sb) {
        if (filename.equals("")) return;

        Path path = Paths.get(filename);
        try {
            String[] parts = sb.toString().split("\n");
            List<String> lines = Files.readAllLines(path);
            if (lines.size() == 0) {
                lines = Arrays.asList(parts);
            } else {
                for (int i = 0; i < parts.length; i++) {
                    String[] x = parts[i].split(",");
                    String entry = x[1] + "," + x[2];
                    lines.set(i, lines.get(i) + "," + entry);
                }
            }
            Files.write(path, lines);
        } catch (IOException ex) {
            System.out.println("Pogreška pri pisanju.");
        }
    }
}
