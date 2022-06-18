package application.optimizer;

public class AdaGrad implements Optimizer {
    private double r[];
    private final double eta0;
    private double eta[];
    private final double epsilon = 1e-3;

    public AdaGrad(int order, double eta0) {
        r = new double[order + 1];
        eta = new double[order + 1];
        for (int i = 0; i < r.length; i++)
            r[i] = epsilon;
        this.eta0 = eta0;
    }

    @Override
    public double[] getStep(double[] grad) {
        double ret[] = new double[grad.length];
        for (int i = 0; i < grad.length; i++) {
            r[i] = r[i] * grad[i] * grad[i];
            eta[i] = eta0 / Math.sqrt(r[i]);
            ret[i] = eta[i] * grad[i];
        }
        return ret;
    }
}
