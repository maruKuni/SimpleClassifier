package application.optimizer;

public class AdaMax implements Optimizer {
    private final double alpha = 2e-3, beta1 = 0.9, beta2 = 0.999;
    private double t = 1.0;
    private double m[], v[];

    public AdaMax(int order) {
        m = new double[order + 1];
        v = new double[order + 1];
        for (int i = 0; i < m.length; i++) {
            m[i] = v[i] = 0;
        }
    }

    @Override
    public double[] getStep(double[] grad) {
        double ret[] = new double[grad.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = beta1 * m[i] + (1 - beta1) * grad[i];
            v[i] = Math.max(beta2 * v[i], Math.abs(grad[i]));
            ret[i] = alpha / (1 - Math.pow(beta1, t)) * m[i] / v[i];
            t += 1.0;
        }
        t += 1;
        return ret;
    }
}
