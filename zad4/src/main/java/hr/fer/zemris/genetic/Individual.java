package hr.fer.zemris.genetic;

public class Individual {
    private double[] values;

    public Individual(double[] values) {
        this.values = values;
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }
}
