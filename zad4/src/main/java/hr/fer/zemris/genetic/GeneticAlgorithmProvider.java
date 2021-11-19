package hr.fer.zemris.genetic;

import hr.fer.zemris.optimization.AbstractFunction;

import java.util.*;

import static java.lang.Math.*;

public class GeneticAlgorithmProvider {

    private static final double EPSILON = 10E-6;
    private Random random = new Random();
    private AbstractFunction function;
    private boolean binaryRepresentation;
    private int precision;
    private int populationSize;
    private double mutationProbability;
    private double minValue;
    private double maxValue;
    private int numberOfParameters;
    private int iterationsWithoutBest;
    private int maxNumberOfEvaluations;
    private int k;

    public GeneticAlgorithmProvider(AbstractFunction function, boolean binaryRepresentation,
                                    int decimalPrecision, int populationSize, double mutationProbability, double minValue,
                                    double maxValue, int numberOfParameters, int iterationsWithoutBest, int maxNumberOfEvaluations) {
        this.function = function;
        this.binaryRepresentation = binaryRepresentation;
        this.precision = (int) pow(10, decimalPrecision);
        this.populationSize = populationSize;
        this.mutationProbability = mutationProbability;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.numberOfParameters = numberOfParameters;
        this.iterationsWithoutBest = iterationsWithoutBest;
        this.maxNumberOfEvaluations = maxNumberOfEvaluations;
        this.k = 3;
    }


    public Individual steadyStateGeneticAlgorithm(boolean trace, int crossOver) {
        Set<Individual> population = generatePopulation();

        double minLoss = functionValue(tournamentSelection(populationSize, population, true));
        int iterationsCount = 0;

        do {
            Individual currentBestIndividual = tournamentSelection(populationSize, population, true);
            double lossValue = functionValue(currentBestIndividual);
            if (Double.compare(minLoss, lossValue) > 0) {
                minLoss = lossValue;
                iterationsCount = 0;
                printInfo(trace, currentBestIndividual, lossValue);
            } else {
                iterationsCount++;
            }

            List<Individual> randomSubPopulation = getSubPopulation(new ArrayList<>(population), k);
            randomSubPopulation.sort(Comparator.comparingDouble(this::functionValue));
            Individual parentOne = randomSubPopulation.get(0);
            Individual parentTwo = randomSubPopulation.get(1);
            Individual worstIndividual = randomSubPopulation.get(randomSubPopulation.size() - 1);
            Individual child = crossOver(crossOver, parentOne, parentTwo);
            mutate(child);

            if (Double.compare(functionValue(child), functionValue(worstIndividual)) < 0) {
                population.remove(worstIndividual);
                population.add(child);
            }
        } while (iterationsCount < iterationsWithoutBest && function.getCounter() < maxNumberOfEvaluations && minLoss > EPSILON);

        Individual best = tournamentSelection(populationSize, population, true);

        System.out.println("Broj evaluacija funkcije: " + function.getCounter());
        System.out.println("Broj iteracija bez promjena: " + iterationsCount);
        System.out.println("Kraj rada.");
        System.out.println("Rezultat:");
        printIndividual(best);
        System.out.println("Vrijednost funkcije: " + functionValue(best));
        return best;
    }

    public double functionValue(Individual individual) {
        if (binaryRepresentation) {
            Individual decimalIndividual = fromBitToDecimal(individual);
            return function.valueAt(decimalIndividual.getValues());
        } else {
            return function.valueAt(individual.getValues());
        }
    }

    private Individual fromBitToDecimal(Individual individual) {
        double intervalSize = maxValue - minValue;
        int numberOfBits = (int) ceil(log(precision * intervalSize) / log(2));
        double[] decimalValues = new double[individual.getValues().length / numberOfBits];

        for (int i = 0; i < individual.getValues().length; i += numberOfBits) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < numberOfBits; j++) {
                sb.append((int) individual.getValues()[i + j]);
            }
            int value = Integer.parseInt(sb.toString(), 2);
            decimalValues[i / numberOfBits] = minValue + value / (pow(2, numberOfBits) - 1) * (maxValue - minValue);
        }

        return new Individual(decimalValues);
    }

    private void mutate(Individual child) {
        if (binaryRepresentation) {
            mutationUniformBinary(child);
        } else {
            mutationGaussianDecimal(child);
//            scrambleMutationDecimal(child);
        }
    }

    private Individual crossOver(int crossOver, Individual parentOne, Individual parentTwo) {
        if (binaryRepresentation) {
            if (crossOver == 1) {
                return crossOverBinaryUniform(parentOne, parentTwo);
            } else {
                return crossOverBinaryBreakPoint(parentOne, parentTwo);
            }
        } else {
            if (crossOver == 1) {
                return crossOverDecimalArithmetic(parentOne, parentTwo);
            } else if (crossOver == 2) {
                return crossOverBLXAlpha(parentOne, parentTwo, 0.5);
            } else {
                return crossOverDecimalHeuristic(parentOne, parentOne);
            }
        }
    }

    private void printInfo(boolean trace, Individual individual, double lossValue) {
        if (!trace) return;
        System.out.println("Novo najbolje rješenje:");
        printIndividual(individual);
        System.out.println("Broj evaluacija funkcije: " + function.getCounter());
        System.out.println("Vrijednost funkcije: " + lossValue + "\n");
    }

    public void printIndividual(Individual individual) {
        StringBuilder sb = new StringBuilder();
        if (binaryRepresentation) {
            Individual decimalIndividual = fromBitToDecimal(individual);
            for (double value : decimalIndividual.getValues()) {
                sb.append(String.format("%.5f ", value));
            }
        } else {
            for (double value : individual.getValues()) {
                sb.append(String.format("%.5f ", value));
            }
        }
        System.out.println(sb.toString());
    }

    private void mutationGaussianDecimal(Individual individual) {
        for (int i = 0; i < individual.getValues().length; i++) {
            if (random.nextDouble() < mutationProbability) {
                double mutationValue = random.nextGaussian() * 3;
                individual.getValues()[i] = individual.getValues()[i] + mutationValue;
            }
        }
    }

    private void scrambleMutationDecimal(Individual individual) {
        if (random.nextDouble() >= mutationProbability) return;

        double[] values = individual.getValues();
        int firstPoint = random.nextInt(values.length);
        int secondPoint = random.nextInt(values.length);
        int min = Integer.min(firstPoint, secondPoint);
        int max = Integer.max(firstPoint, secondPoint);

        for (int i = min; i <= max; i++) {
            int randomIndex = min + random.nextInt(max + 1 - min);
            double tmp = values[i];
            values[i] = values[randomIndex];
            values[randomIndex] = tmp;
        }
    }

    private void mutationUniformBinary(Individual child) {
        for (int i = 0; i < child.getValues().length; i++) {
            double randomNumber = random.nextDouble();
            if (randomNumber < mutationProbability) {
                child.getValues()[i] = random.nextInt(2);
            }
        }
    }

    private Individual crossOverDecimalArithmetic(Individual parentOne, Individual parentTwo) {
        double[] childValues = new double[parentOne.getValues().length];
        double randomNumber = random.nextDouble();
        for (int i = 0; i < parentOne.getValues().length; i++) {
            childValues[i] = randomNumber * parentOne.getValues()[i] + (1 - randomNumber) * parentTwo.getValues()[i];
        }

        return new Individual(childValues);
    }

    private Individual crossOverDecimalHeuristic(Individual betterParent, Individual worstParent) {
        double[] childValues = new double[betterParent.getValues().length];
        double randomNumber = random.nextDouble();
        for (int i = 0; i < betterParent.getValues().length; i++) {
            double value = randomNumber * (betterParent.getValues()[i] - worstParent.getValues()[i]) + betterParent.getValues()[i];
            if (value < minValue) {
                value = minValue;
            }
            if (value > maxValue) {
                value = maxValue;
            }
            childValues[i] = value;
        }

        return new Individual(childValues);
    }

    private Individual crossOverBLXAlpha(Individual parentOne, Individual parentTwo, double alpha) {
        double[] childValues = new double[parentOne.getValues().length];
        double[] firstValues = parentOne.getValues();
        double[] secondValues = parentTwo.getValues();

        for (int i = 0; i < firstValues.length; i++) {
            double di = abs(firstValues[i] - secondValues[i]);
            double min = Double.min(firstValues[i], secondValues[i]);
            double max = Double.max(firstValues[i], secondValues[i]);
            double value = min - alpha * di + (max + alpha * di - (min - alpha * di)) * random.nextDouble();
            if (value < minValue) {
                value = minValue + alpha * di;
            } else if (value > maxValue) {
                value = maxValue - alpha * di;
            }
            childValues[i] = value;
        }

        return new Individual(childValues);
    }

    private Individual crossOverBinaryBreakPoint(Individual parentOne, Individual parentTwo) {
        double[] childValues = new double[parentOne.getValues().length];
        int breakPoint = random.nextInt(parentOne.getValues().length);
        for (int i = 0; i < parentOne.getValues().length; i++) {
            if (i < breakPoint) {
                childValues[i] = parentOne.getValues()[i];
            } else {
                childValues[i] = parentTwo.getValues()[i];
            }
        }

        return new Individual(childValues);
    }

    private Individual crossOverBinaryUniform(Individual parentOne, Individual parentTwo) {
        double[] childValues = new double[parentOne.getValues().length];
        for (int i = 0; i < parentOne.getValues().length; i++) {
            int randomParent = random.nextInt(2);
            childValues[i] = randomParent == 0 ? parentOne.getValues()[i] : parentTwo.getValues()[i];
        }

        return new Individual(childValues);
    }


    private Individual tournamentSelection(int k, Set<Individual> population, boolean getBest) {
        List<Individual> helpPopulation = getSubPopulation(new ArrayList<>(population), k);
        double bestResult = functionValue(helpPopulation.get(0));
        int indexOfBest = 0;

        for (int i = 1; i < helpPopulation.size(); i++) {
            double lossResult = functionValue(helpPopulation.get(i));
            if (getBest) {
                if (lossResult < bestResult) {
                    bestResult = lossResult;
                    indexOfBest = i;
                }
            } else {
                if (lossResult > bestResult) {
                    bestResult = lossResult;
                    indexOfBest = i;
                }
            }
        }

        return helpPopulation.get(indexOfBest);
    }

    private List<Individual> getSubPopulation(List<Individual> population, int k) {
        List<Individual> subPopulation;
        if (k == population.size()) {
            //if k == population.size(), return the best/worst of entire population
            subPopulation = new ArrayList<>(population);
        } else {
            Random random = new Random();
            Set<Individual> subPopulationSet = new HashSet<>();
            do {
                subPopulationSet.add(population.get(random.nextInt(population.size())));
            } while (subPopulationSet.size() < k);
            subPopulation = new ArrayList<>(subPopulationSet);
        }

        return subPopulation;
    }

    private Set<Individual> generatePopulation() {
        Set<Individual> population = new HashSet<>();

        for (int i = 0; i < populationSize; i++) {
            double[] values = new double[numberOfParameters];
            for (int j = 0; j < numberOfParameters; j++) {
                values[j] = (minValue + (maxValue - minValue) * random.nextDouble());
            }
            population.add(new Individual(values));
        }

        if (binaryRepresentation) {
            return turnToBinaryRepresentation(population);
        } else {
            return population;
        }
    }

    private Set<Individual> turnToBinaryRepresentation(Set<Individual> population) {
        Set<Individual> transformedPopulation = new HashSet<>();
        double intervalSize = maxValue - minValue;
        int numberOfBits = (int) ceil(log(precision * intervalSize) / log(2));

        for (Individual individual : population) {
            double[] transformedValues = new double[numberOfBits * individual.getValues().length];
            int i = 0;
            for (double element : individual.getValues()) {
                int transformedElement = (int) ceil(((element - minValue) / intervalSize) * (pow(2, numberOfBits) - 1));
                char[] binaryTransformedElement = String.format("%" + numberOfBits + "s", Integer.toBinaryString(transformedElement))
                        .replace(' ', '0')
                        .toCharArray();
                for (char c : binaryTransformedElement) {
                    transformedValues[i] = Integer.parseInt(String.valueOf(c));
                    i++;
                }
            }
            transformedPopulation.add(new Individual(transformedValues));
        }

        return transformedPopulation;
    }

    public void setFunction(AbstractFunction function) {
        this.function = function;
    }

    public void setBinaryRepresentation(boolean binaryRepresentation) {
        this.binaryRepresentation = binaryRepresentation;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public void setNumberOfParameters(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters;
    }

    public void setIterationsWithoutBest(int iterationsWithoutBest) {
        this.iterationsWithoutBest = iterationsWithoutBest;
    }

    public void setMaxNumberOfEvaluations(int maxNumberOfEvaluations) {
        this.maxNumberOfEvaluations = maxNumberOfEvaluations;
    }

    public AbstractFunction getFunction() {
        return function;
    }

    public double getMutationProbability() {
        return mutationProbability;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getK() {
        return k;
    }
}
