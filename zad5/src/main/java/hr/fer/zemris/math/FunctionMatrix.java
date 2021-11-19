package hr.fer.zemris.math;

import hr.fer.zemris.linear.Matrix;

import java.util.ArrayList;
import java.util.List;

public class FunctionMatrix extends Matrix {
    private List<Function> functions;
    private double t;

    public FunctionMatrix(int rows, int cols, List<Function> functions) {
        super(rows, cols);
        if(cols != 1) throw new IllegalArgumentException("Function matrix should contain one column");
        this.functions = functions;
    }

//    @Override
//    public double get(int row, int column) {
//        return functions.get(row).solve(t);
//    }
//
//    @Override
//    public Matrix set(int row, int column, double value) {
//        return this;
//    }

    public FunctionMatrix copy() {
        return new FunctionMatrix(getRowsCount(), getColsCount(), new ArrayList<>(functions));
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public double getT() {
        return t;
    }

    public FunctionMatrix setT(double t) {
        this.t = t;
        for(int i = 0; i < getRowsCount(); i++) {
            set(i, 0, functions.get(i).solve(t));
        }

        return this;
    }
}
