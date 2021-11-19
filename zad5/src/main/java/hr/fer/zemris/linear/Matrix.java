package hr.fer.zemris.linear;

import hr.fer.zemris.linear.exceptions.IllegalMatrixFormatException;
import hr.fer.zemris.linear.exceptions.NoResultException;
import hr.fer.zemris.linear.exceptions.ReadMatrixException;
import hr.fer.zemris.linear.exceptions.WriteMatrixException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Matrix {
    private double[][] elements;
    private int rows;
    private int cols;
    private static final double EPSILON = 1E-5;

    public Matrix(int rows, int cols) {
        this(new double[rows][cols], rows, cols, true);
    }

    public Matrix(double[][] elements, int rows, int cols, boolean free) {
        if (free) {
            this.elements = elements;
        } else {
            this.elements = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    this.elements[i][j] = elements[i][j];
                }
            }
        }
        this.rows = rows;
        this.cols = cols;
    }

    public int getRowsCount() {
        return rows;
    }

    public int getColsCount() {
        return cols;
    }

    public double get(int row, int column) {
        return elements[row][column];
    }

    public Matrix set(int row, int column, double value) {
        elements[row][column] = value;
        return this;
    }

    public Matrix copy() {
        return new Matrix(elements, rows, cols, false);
    }

    public Matrix newInstance(int rows, int columns) {
        return new Matrix(rows, columns);
    }

    public Matrix add(Matrix other) {
        checkMatrixFormat(other);
        for (int i = 0; i < this.getRowsCount(); i++) {
            for (int j = 0; j < this.getColsCount(); j++) {
                this.set(i, j, this.get(i, j) + other.get(i, j));
            }
        }

        return this;
    }

    public Matrix nAdd(Matrix other) {
        return this.copy().add(other);
    }

    public Matrix sub(Matrix other) {
        checkMatrixFormat(other);
        for (int i = 0; i < this.getRowsCount(); i++) {
            for (int j = 0; j < this.getColsCount(); j++) {
                this.set(i, j, this.get(i, j) - other.get(i, j));
            }
        }

        return this;
    }

    public Matrix nSub(Matrix other) {
        return this.copy().sub(other);
    }

    public Matrix scalarMultiply(double coefficient) {
        for (int i = 0; i < getRowsCount(); i++) {
            for (int j = 0; j < getColsCount(); j++) {
                this.set(i, j, this.get(i, j) * coefficient);
            }
        }

        return this;
    }

    public Matrix nMultiply(Matrix other) {
        if (this.getColsCount() != other.getRowsCount()) {
            throw new IllegalMatrixFormatException();
        }
        int num = this.getColsCount();
        Matrix result = this.newInstance(this.getRowsCount(), other.getColsCount());
        for (int i = 0; i < result.getRowsCount(); i++) {
            for (int j = 0; j < result.getColsCount(); j++) {
                double sum = 0;
                for (int k = 0; k < num; k++) {
                    sum += this.get(i, k) * other.get(k, j);
                }
                result.set(i, j, sum);
            }
        }

        return result;
    }

    public double determinant() {
        if (getRowsCount() != getColsCount()) {
            throw new IllegalMatrixFormatException("Matrix must be square to find determinant");
        }
        Matrix A = this.copy();
        Matrix P = A.LUDecomposition(true);

        double determinant = -1;
        for (int i = 0; i < getColsCount(); i++) {
            if (P.get(i, i) == 0) determinant *= -1;
            determinant *= A.get(i, i);
        }

        return determinant;
    }

    public Matrix transpose() {
        Matrix transposed = newInstance(this.getColsCount(), this.getRowsCount());
        for (int i = 0; i < this.getRowsCount(); i++) {
            for (int j = 0; j < this.getColsCount(); j++) {
                transposed.set(j, i, this.get(i, j));
            }
        }
        return transposed;
    }

    public static Matrix readFromFile(String filename) {
        Path path = Path.of(filename);
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new ReadMatrixException("Cannot read matrix from file " + filename);
        }

        if (lines.size() == 0) {
            throw new ReadMatrixException("Empty input file given");
        }

        return parseMatrix(lines);
    }

    public void swapRows(int firstRow, int secondRow) {
        if (firstRow < 0 || firstRow > getRowsCount() || secondRow < 0 || secondRow > getRowsCount()) {
            throw new IllegalArgumentException("Row number out of bounds");
        }
        Matrix copy = this.copy();
        for (int i = 0; i < getColsCount(); i++) {
            set(firstRow, i, copy.get(secondRow, i));
            set(secondRow, i, copy.get(firstRow, i));
        }
    }

    public static Matrix identityMatrix(int dimensions) {
        Matrix matrix = new Matrix(dimensions, dimensions);
        for (int i = 0; i < dimensions; i++) {
            matrix.set(i, i, 1);
        }

        return matrix;
    }

    public Matrix forwardSubstitution(Matrix b) {
        checkSubstitutionFormat(b);
        Matrix y = b.copy();

        for (int i = 0; i < getRowsCount() - 1; i++) {
            for (int j = i + 1; j < getRowsCount(); j++) {
                y.set(j, 0, y.get(j, 0) - get(j, i) * y.get(i, 0));
            }
        }

        return y;
    }

    public Matrix backwardSubstitution(Matrix y) {
        checkSubstitutionFormat(y);
        Matrix x = y.copy();

        for (int i = getRowsCount() - 1; i >= 0; i--) {
            if (Math.abs(get(i, i)) < EPSILON) {
                throw new NoResultException("Division by zero in backward substitution; equation system has no result (matrix is singular).");
            }
            x.set(i, 0, x.get(i, 0) / get(i, i));
            for (int j = 0; j < i; j++) {
                x.set(j, 0, x.get(j, 0) - get(j, i) * x.get(i, 0));
            }
        }

        return x;
    }

    public Matrix LUDecomposition(boolean pivoting) {
        if (getRowsCount() != getColsCount()) {
            throw new IllegalMatrixFormatException("Matrix should be square");
        }

        Matrix p = identityMatrix(getRowsCount());
        for (int i = 0; i < getRowsCount() - 1; i++) {
            if (pivoting) {
                int index = choosePivot(i);
                if (Math.abs(get(index, i)) < EPSILON) {
                    throw new NoResultException("Could not find result: no pivot element != 0, equation system cannot be solved.");
                }
                if (index != i) {
                    swapRows(i, index);
                    p.swapRows(i, index);
                }
            }
            decomposition(i, getRowsCount());
        }

        return p;
    }

    private void decomposition(int i, int n) {
        for (int j = i + 1; j < n; j++) {
            if (Math.abs(get(i, i)) < EPSILON) {
                throw new NoResultException("Could not find result: pivot element is zero.");
            }
            set(j, i, get(j, i) / get(i, i));
            for (int k = i + 1; k < n; k++) {
                set(j, k, get(j, k) - get(j, i) * get(i, k));
            }
        }
    }

    public Matrix inverse() {
        if (this.getColsCount() != this.getRowsCount()) {
            throw new IllegalMatrixFormatException("Matrix is not square and has no inverse");
        }
        Matrix A = this.copy();
        Matrix p = A.LUDecomposition(true);
        Matrix identity = Matrix.identityMatrix(getRowsCount());
        Matrix inverse = new Matrix(getRowsCount(), getColsCount());

        for (int i = 0; i < getRowsCount(); i++) {
            Matrix yi = A.forwardSubstitution(p.nMultiply(identity.getColumn(i)));
            Matrix xi = A.backwardSubstitution(yi);
            for (int j = 0; j < getRowsCount(); j++) {
                inverse.set(j, i, xi.get(j, 0));
            }
        }

        return inverse;
    }

    public Matrix getColumn(int index) {
        if (index < 0 || index >= getColsCount()) {
            throw new IllegalArgumentException("Index out of range");
        }
        Matrix matrix = new Matrix(getRowsCount(), 1);
        for (int i = 0; i < getRowsCount(); i++) {
            matrix.set(i, 0, get(i, index));
        }
        return matrix;
    }

    public double[][] toArray() {
        double[][] array = new double[this.getRowsCount()][this.getColsCount()];
        for (int i = 0; i < this.getRowsCount(); i++) {
            for (int j = 0; j < this.getColsCount(); j++) {
                array[i][j] = this.get(i, j);
            }
        }

        return array;
    }

    public void writeToFile(String filename) {
        Path path = Path.of(filename);
        try {
            Files.write(path, toString().getBytes());
        } catch (IOException ex) {
            throw new WriteMatrixException("Error while writing matrix to file");
        }
    }

    public String toString(int precision) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getRowsCount(); i++) {
            for (int j = 0; j < this.getColsCount(); j++) {
                if (Math.abs(get(i, j)) < EPSILON) {
                    sb.append(String.format("%." + precision + "f",
                            Math.abs(this.get(i, j))));
                } else {
                    sb.append(String.format("%." + precision + "f",
                            this.get(i, j)));
                }
                if (j != this.getColsCount() - 1) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return this.toString(6);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        if (rows != matrix.rows || cols != matrix.cols) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (Math.abs(elements[i][j] - matrix.get(i, j)) > EPSILON) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rows, cols);
        result = 31 * result + Arrays.hashCode(elements);
        return result;
    }

    private void checkMatrixFormat(Matrix other) {
        if (this.getRowsCount() != other.getRowsCount() ||
                this.getColsCount() != other.getColsCount()) {
            throw new IllegalMatrixFormatException();
        }
    }

    private void checkSubstitutionFormat(Matrix b) {
        if (getColsCount() != getRowsCount()) {
            throw new IllegalMatrixFormatException("First matrix should be square");
        }
        if (b.getRowsCount() != getRowsCount() || b.getColsCount() != 1) {
            throw new IllegalMatrixFormatException("Second matrix should have one column and the same number of rows as first matrix");
        }
    }

    private static Matrix parseMatrix(List<String> lines) {
        int cols = lines.get(0).trim().split("\\s+").length;
        Matrix matrix = new Matrix(lines.size(), cols);
        for (int i = 0; i < lines.size(); i++) {
            String[] elements = lines.get(i).split("\\s+");
            if (elements.length != cols) {
                throw new ReadMatrixException("Number of elements should be the same in each row");
            }
            for (int j = 0; j < elements.length; j++) {
                try {
                    matrix.set(i, j, Double.parseDouble(elements[j].trim()));
                } catch (NumberFormatException ex) {
                    throw new ReadMatrixException("Each element must be a number");
                }
            }
        }

        return matrix;
    }

    private int choosePivot(int i) {
        double pivot = get(i, i);
        int row = i;
        for (int j = i + 1; j < getColsCount(); j++) {
            if (Math.abs(get(j, i)) > Math.abs(pivot)) {
                pivot = get(j, i);
                row = j;
            }
        }

        return row;
    }

}
