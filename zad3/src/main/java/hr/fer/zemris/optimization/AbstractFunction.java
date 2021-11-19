package hr.fer.zemris.optimization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AbstractFunction {
    private long counter;
    private Map<String, Double> valueMap;
    private Function function;

    public AbstractFunction(Function function) {
        this.function = function;
        this.valueMap = new HashMap<>();
    }

    public double valueAt(double... x) {
        if (valueMap.containsKey(Arrays.toString(x))) {
            return valueMap.get(Arrays.toString(x));
        } else {
            counter++;
            double result = function.solve(x);
            valueMap.put(Arrays.toString(x), result);
            return result;
        }
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public void reset() {
        setCounter(0);
        this.valueMap = new HashMap<>();
    }
}
