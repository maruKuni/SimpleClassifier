package application.optimizer;

public class ConstantOptimizer implements Optimizer {
    final double eta;

    public ConstantOptimizer(double eta) {
        this.eta = eta;
    }

    @Override
    public double[] getStep(double grad[]) {
        double ret[] = new double[grad.length];
        for (int i = 0; i < ret.length; i++)
            ret[i] = eta * grad[i];
        return ret;
    }

}
