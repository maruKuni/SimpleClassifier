package application.optimizer;

public interface Optimizer {
    double[] getStep(double grad[]);
}
