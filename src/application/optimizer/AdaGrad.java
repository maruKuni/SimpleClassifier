package application.optimizer;

public class AdaGrad implements Optimizer {
    private double r[];
    private final double eta0;
    private final double epsilon = 1e-3;

    public AdaGrad(int order, double eta0) {
        r = new double[order + 1];
        for (int i = 0; i < r.length; i++)
            r[i] = epsilon;
        this.eta0 = eta0;
    }

    @Override
    /**see also 
     * https://qiita.com/ak11/items/7f63a1198c345a138150
     * */
    public double[] getStep(double[] grad) {
        double ret[] = new double[grad.length];
        for (int i = 0; i < grad.length; i++) {
            r[i] += grad[i] * grad[i];
            ret[i] = eta0 * grad[i] / Math.sqrt(r[i]);
        }
        return ret;
    }
}
