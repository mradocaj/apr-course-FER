package hr.fer.zemris.genetic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class Data {

    private List<List<Double>> data;

    public Data(String pathname) {
        this.data = new ArrayList<>();
        initializeData(pathname);
    }

    private void initializeData(String pathname) {
        try {
            List<List<Double>> dataList = new ArrayList<>();
            List<String> lines = Files.readAllLines(Paths.get(pathname));

            for (String line : lines) {
                String[] parts = line.split("\\s+");
                List<Double> dataPoint = new ArrayList<>();
                for (String part : parts) {
                    dataPoint.add(Double.parseDouble(part));
                }
                dataList.add(dataPoint);
            }

            this.data = dataList;

        } catch (IOException | NumberFormatException ex) {
            throw new RuntimeException("Pogreška kod učitavanja podataka.");
        }
    }

    public double getLossOnData(List<Double> beta) {
        double sum = 0;
        for (List<Double> dataPoint : data) {
            double calculatedResult = evaluateFunction(dataPoint.get(0), dataPoint.get(1), beta);
            sum += pow(dataPoint.get(2) - calculatedResult, 2);
        }

        return sum / data.size();
    }

    private double evaluateFunction(double x, double y, List<Double> beta) {
        return sin(beta.get(0) + beta.get(1) * x) + beta.get(2) * cos(x * (beta.get(3) + y)) * 1 / (1 + exp(pow(x - beta.get(4), 2)));
    }

}
