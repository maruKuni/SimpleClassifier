package application.optimizer;

public class AdamOptimizer implements Optimizer {
    private double t = 1;
    private double m[], v[], m_hat[], v_hat[];
    private final double alpha = 1e-3, beta1 = 0.9, beta2 = 0.999, epsilon = 1e-8;

    public AdamOptimizer(int order) {
        m = new double[order + 1];
        v = new double[order + 1];
        m_hat = new double[order + 1];
        v_hat = new double[order + 1];
        for (int i = 0; i < m.length; i++) {
            m[i] = v[i] = 0;
        }
    }

    @Override
    public double[] getStep(double[] grad) {
        double ret[] = new double[grad.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = beta1 * m[i] + (1 - beta1) * grad[i];
            v[i] = beta2 * v[i] + (1 - beta2) * grad[i] * grad[i];
            m_hat[i] = m[i] / (1 - Math.pow(beta1, t));
            v_hat[i] = v[i] / (1 - Math.pow(beta2, t));
            ret[i] = alpha * m_hat[i] / (Math.sqrt(v_hat[i]) + epsilon);
        }
        t += 1;
        return ret;
    }
}
