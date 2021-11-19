package hr.fer.zemris.genetic.examples;

import hr.fer.zemris.genetic.GeneticAlgorithmProvider;
import hr.fer.zemris.genetic.GoalFunctionsGenetic;
import hr.fer.zemris.genetic.Individual;
import hr.fer.zemris.optimization.AbstractFunction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SteadyStateAlgorithmMain {

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        zad1();
//        zad2();
//        zad3();
//        zad4();
//        zad5();
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        long minutes = TimeUnit.NANOSECONDS.toMinutes(totalTime);

        System.out.println("Kraj izvođenja programa.");
        System.out.println("Trajanje: " + minutes + " min");
    }

    private static void zad1() {
        printTaskInfo(1);
        GeneticAlgorithmProvider provider = new GeneticAlgorithmProvider(GoalFunctionsGenetic.f1,
                true,
                6,
                50,
                0.5,
                -50,
                150,
                2,
                10000,
                1000000);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f1 (binarni prikaz).");
        runAlgorithm(provider, GoalFunctionsGenetic.f1, true, 2);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f1 (decimalni prikaz).");
        runAlgorithm(provider, GoalFunctionsGenetic.f1, false, 2);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f3 (binarni prikaz).");
        provider.setNumberOfParameters(5);
        provider.setMutationProbability(0.1);
        runAlgorithm(provider, GoalFunctionsGenetic.f3, true, 2);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f3 (decimalni prikaz).");
        runAlgorithm(provider, GoalFunctionsGenetic.f3, false, 2);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f6 (binarni prikaz).");
        provider.setNumberOfParameters(2);
        runAlgorithm(provider, GoalFunctionsGenetic.f6, true, 2);
        GoalFunctionsGenetic.f6.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f6, true, 2);
        GoalFunctionsGenetic.f6.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f6, true, 2);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f6 (decimalni prikaz).");
        GoalFunctionsGenetic.f6.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f6, false, 2);
        GoalFunctionsGenetic.f6.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f6, false, 2);
        GoalFunctionsGenetic.f6.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f6, false, 2);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f7 (binarni prikaz).");
        runAlgorithm(provider, GoalFunctionsGenetic.f7, true, 2);
        GoalFunctionsGenetic.f7.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f7, true, 2);
        GoalFunctionsGenetic.f7.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f7, true, 2);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f7 (decimalni prikaz).");
        GoalFunctionsGenetic.f7.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f7, false, 2);
        GoalFunctionsGenetic.f7.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f7, false, 2);
        GoalFunctionsGenetic.f7.reset();
        runAlgorithm(provider, GoalFunctionsGenetic.f7, false, 2);
    }

    private static void zad2() {
        printTaskInfo(2);

        GeneticAlgorithmProvider provider = new GeneticAlgorithmProvider(GoalFunctionsGenetic.f6,
                false,
                4,
                60,
                0.1,
                -50,
                150,
                1,
                1000,
                100000);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f6 (decimalni prikaz).");

        printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 s jednom dimenzijom: ");

        provider.setNumberOfParameters(3);
        printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 s tri dimenzije: ");

        provider.setNumberOfParameters(6);
        printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 sa šest dimenzija: ");

        provider.setNumberOfParameters(10);
        printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 s deset dimenzija: ");

        System.out.println("-".repeat(50));
        provider.setNumberOfParameters(1);
        System.out.println("Eliminacijski algoritam nad funkcijom f7 (decimalni prikaz).");

        printResultInfo(provider, false, GoalFunctionsGenetic.f7, "Broj pogodaka za f7 s jednom dimenzijom: ");

        provider.setNumberOfParameters(3);
        printResultInfo(provider, false, GoalFunctionsGenetic.f7, "Broj pogodaka za f7 s tri dimenzije: ");

        provider.setNumberOfParameters(6);
        printResultInfo(provider, false, GoalFunctionsGenetic.f7, "Broj pogodaka za f7 sa šest dimenzija: ");

        provider.setNumberOfParameters(10);
        printResultInfo(provider, false, GoalFunctionsGenetic.f7, "Broj pogodaka za f7 s deset dimenzija: ");
    }

    private static void zad3() {
        printTaskInfo(3);
        String firstPath = "src/main/resources/boxplot3_f6.txt";
        String secondPath = "src/main/resources/boxplot3_f7.txt";
        initializeFiles(firstPath, secondPath);

        GeneticAlgorithmProvider provider = new GeneticAlgorithmProvider(GoalFunctionsGenetic.f6,
                false,
                4,
                30,
                0.1,
                -50,
                150,
                3,
                1000000,
                100000);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f6 (decimalni prikaz).");

        List<Individual> results;
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 s tri dimenzije: ");
        writeToFile(firstPath, results, provider);

        provider.setNumberOfParameters(6);
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 sa šest dimenzija: ");
        writeToFile(firstPath, results, provider);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f6 (binarni prikaz).");

        provider.setNumberOfParameters(3);
        results = printResultInfo(provider, true, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 s tri dimenzije: ");
        writeToFile(firstPath, results, provider);

        provider.setNumberOfParameters(6);
        results = printResultInfo(provider, true, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 sa šest dimenzija: ");
        writeToFile(firstPath, results, provider);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f7 (decimalni prikaz).");

        provider.setNumberOfParameters(3);
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f7, "Broj pogodaka za f7 s tri dimenzije: ");
        writeToFile(secondPath, results, provider);

        provider.setNumberOfParameters(6);
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f7, "Broj pogodaka za f7 sa šest dimenzija: ");
        writeToFile(secondPath, results, provider);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f7 (binarni prikaz).");

        provider.setNumberOfParameters(3);
        results = printResultInfo(provider, true, GoalFunctionsGenetic.f7, "Broj pogodaka za f7 s tri dimenzije: ");
        writeToFile(secondPath, results, provider);

        provider.setNumberOfParameters(6);
        results = printResultInfo(provider, true, GoalFunctionsGenetic.f7, "Broj pogodaka za f7 sa šest dimenzija: ");
        writeToFile(secondPath, results, provider);
    }

    private static void zad4() {
        printTaskInfo(4);
        String firstPath = "src/main/resources/boxplot4_f6_velpop.txt";
        String secondPath = "src/main/resources/boxplot4_f6_pm.txt";
        initializeFiles(firstPath, secondPath);

        GeneticAlgorithmProvider provider = new GeneticAlgorithmProvider(GoalFunctionsGenetic.f6,
                true,
                4,
                30,
                0.1,
                -50,
                150,
                2,
                1000000,
                100000);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f6 (decimalni prikaz). Optimiranje vjerojatnosti mutacije.");

        List<Individual> results;
        List<Double> means = new ArrayList<>();
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 s pM = 0.1: ");
        means.add(provider.functionValue(results.get(results.size() / 2)));
        writeToFile(secondPath, results, provider);

        provider.setMutationProbability(0.3);
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 s pM = 0.3: ");
        means.add(provider.functionValue(results.get(results.size() / 2)));
        writeToFile(secondPath, results, provider);

        provider.setMutationProbability(0.6);
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 s pM = 0.6: ");
        means.add(provider.functionValue(results.get(results.size() / 2)));
        writeToFile(secondPath, results, provider);

        provider.setMutationProbability(0.9);
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6, "Broj pogodaka za f6 s pM = 0.9: ");
        means.add(provider.functionValue(results.get(results.size() / 2)));
        writeToFile(secondPath, results, provider);

        System.out.println("-".repeat(50));
        setOptimalMutationProbability(means, provider);
        System.out.println("Eliminacijski algoritam nad funkcijom f6 (decimalni prikaz). Optimiranje veličine populacije.");

        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6,"Broj pogodaka za f6 s velPop = 30: ");
        writeToFile(firstPath, results, provider);

        provider.setPopulationSize(50);
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6,"Broj pogodaka za f6 s velPop = 50: ");
        writeToFile(firstPath, results, provider);

        provider.setPopulationSize(100);
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6,"Broj pogodaka za f6 s velPop = 100: ");
        writeToFile(firstPath, results, provider);

        provider.setPopulationSize(200);
        results = printResultInfo(provider, false, GoalFunctionsGenetic.f6,"Broj pogodaka za f6 s velPop = 200: ");
        writeToFile(firstPath, results, provider);
    }

    private static void zad5() {
        printTaskInfo(5);

        GeneticAlgorithmProvider provider = new GeneticAlgorithmProvider(GoalFunctionsGenetic.f7,
                false,
                4,
                30,
                0.5,
                -50,
                150,
                2,
                10000,
                100000);

        System.out.println("-".repeat(50));
        System.out.println("Eliminacijski algoritam nad funkcijom f7  (decimalni prikaz). Testiranje veličine turnira.");

        printResultInfo(provider, false, GoalFunctionsGenetic.f7,"Broj pogodaka za f7 s k = 3: ");

        provider.setK(10);
        printResultInfo(provider, false, GoalFunctionsGenetic.f7,"Broj pogodaka za f7 s k = 10: ");

        provider.setK(25);
        printResultInfo(provider, false, GoalFunctionsGenetic.f7,"Broj pogodaka za f7 s k = 25: ");
    }

    private static void setOptimalMutationProbability(List<Double> means, GeneticAlgorithmProvider provider) {
        int minIndex = 0;
        double minMean = Double.MAX_VALUE;
        for(int i = 0; i < means.size(); i++) {
            if(means.get(i) < minMean) {
                minIndex = i;
                minMean = means.get(i);
            }
        }

        switch (minIndex) {
            case 0:
                provider.setMutationProbability(0.1);
                break;
            case 1:
                provider.setMutationProbability(0.3);
                break;
            case 2:
                provider.setMutationProbability(0.6);
                break;
            case 3:
                provider.setMutationProbability(0.9);
                break;
        }

        System.out.println("Optimalna vjerojatnost mutacije: " + provider.getMutationProbability());
    }

    private static void initializeFiles(String... filenames) {
        try {
            for (String filename : filenames) {
                Path path = Paths.get(filename);
                if (Files.exists(path)) {
                    Files.write(path, new ArrayList<>());
                } else {
                    Files.createFile(path);
                }
            }
        } catch (IOException ex) {
            System.out.println("Greška kod pisanja u datoteke.");
            System.exit(1);
        }
    }

    private static void writeToFile(String filename, List<Individual> results, GeneticAlgorithmProvider provider) {
        Path path = Paths.get(filename);
        try {
            List<String> lines = Files.readAllLines(path);
            if (lines.size() == 0) {
                for (Individual result : results) {
                    lines.add(String.format("%.5f", provider.functionValue(result)));
                }
            } else {
                for (int i = 0; i < lines.size(); i++) {
                    lines.set(i, lines.get(i) + String.format(",%.5f", provider.functionValue(results.get(i))));
                }
            }
            Files.write(path, lines);
        } catch (IOException ex) {
            System.out.println("Pogreška pri čitanju.");
        }
    }

    private static List<Individual> printResultInfo(GeneticAlgorithmProvider provider, boolean binaryRepresentation, AbstractFunction f, String s) {
        List<Individual> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            results.add(runAlgorithm(provider, f, binaryRepresentation, 2));
        }
        System.out.println("=".repeat(50) + "\n");
        System.out.println(s + numberOfHits(results, provider));
        System.out.println("Medijan: " + provider.functionValue(results.get(results.size() / 2)));
        System.out.println("\n" + "=".repeat(50) + "\n");

        return results;
    }

    private static int numberOfHits(List<Individual> bestValues, GeneticAlgorithmProvider provider) {
        int hits = 0;
        bestValues.sort(Comparator.comparingDouble(provider::functionValue));
        for (int i = 0; i < bestValues.size(); i++) {
            if (provider.functionValue(bestValues.get(i)) < 10E-6) hits++;
        }

        return hits;
    }

    private static Individual runAlgorithm(GeneticAlgorithmProvider provider, AbstractFunction f, boolean binaryRepresentation, int crossOver) {
        System.out.println("-".repeat(50) + "\n");
        f.reset();
        provider.setFunction(f);
        provider.setBinaryRepresentation(binaryRepresentation);
        Individual result = provider.steadyStateGeneticAlgorithm(false, crossOver);
        System.out.println("-".repeat(50) + "\n");
        return result;
    }

    private static void printTaskInfo(int taskNumber) {
        System.out.println();
        System.out.println("=".repeat(100));
        System.out.println(taskNumber + ". zadatak");
        System.out.println("=".repeat(100));
        System.out.println();
    }

}
