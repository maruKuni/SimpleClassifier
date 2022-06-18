package application;

import java.util.*;
import java.util.function.*;
import application.optimizer.*;

public class SimpleClassifier {
    public enum GradDesc {
        GradDesc, SGD, miniBatch
    }

    public enum Optimize {
        constant, Adam, AdaMax, AdaGrad
    }

    private enum State {
        Forward, Grad, Update
    }

    private ArrayList<LabeledPoint> points;
    private ArrayList<LabeledPoint> regularizedPoints;
    public double weight[];
    private double grad[];
    private GradDesc gd;
    private Optimize lr;
    private int order, bathcSize, t = 1;
    private double eta, xStd, yStd, xMean, yMean;
    private final Random rnd;
    private final Optimizer opt;

    public SimpleClassifier(Builder builder) {
        rnd = new Random(System.currentTimeMillis());
        this.points = builder.points;
        this.order = builder.order;
        this.gd = builder.gd;
        this.lr = builder.lr;
        this.bathcSize = builder.bathcSize;
        this.eta = builder.eta;

        weight = new double[order + 1];
        grad = new double[order + 1];

        regularizedPoints = new ArrayList<>();

        regularizePoints();

        for (int i = 0; i < weight.length; i++) {
            weight[i] = rnd.nextGaussian();
        }
        switch (lr) {
        case AdaGrad:
            opt = new AdaGrad(order, 1e-2);
            break;
        case Adam:
            opt = new AdamOptimizer(order);
            break;
        case AdaMax:
            opt = new AdaMax(order);
            break;
        case constant:
            opt = new ConstantOptimizer(eta);
            break;
        default:
            opt = new ConstantOptimizer(1e-3);
        }
    }

    public static class Builder {
        private ArrayList<LabeledPoint> points;
        private GradDesc gd;
        private Optimize lr;
        private int order, bathcSize = 0;
        private double eta = 0;

        public Builder(ArrayList<LabeledPoint> points, int order, GradDesc gd, Optimize lr) {
            this.points = points;
            this.gd = gd;
            this.lr = lr;
            this.order = order;
        }

        public Builder eta(double val) {
            this.eta = val;
            return this;
        }

        public Builder batchSize(int val) {
            this.bathcSize = val;
            return this;
        }

        public SimpleClassifier build() {
            return new SimpleClassifier(this);
        }
    }

    private void regularizePoints() {
        xMean = points.stream().mapToDouble(p -> p.getX()).average().getAsDouble();
        yMean = points.stream().mapToDouble(p -> p.getY()).average().getAsDouble();
        xStd = Math.sqrt(
                points.stream().mapToDouble(LabeledPoint::getX).map(x -> Math.pow(x - xMean, 2.0)).average()
                        .getAsDouble());
        yStd = Math.sqrt(
                points.stream().mapToDouble(LabeledPoint::getY).map(x -> Math.pow(x - xMean, 2.0)).average()
                        .getAsDouble());
        final Function<LabeledPoint, LabeledPoint> regularize = p -> new LabeledPoint((p.getX() - xMean) / xStd,
                (p.getY() - yMean) / yStd,
                p.getLabel());
        regularizedPoints.addAll(points.stream()
                .map(regularize).toList());
    }

    /**arugment 'x' is non-regularized double value
     * return is non-regularized double value*/
    public Double calcFunc(Double x) {
        double regularX = (x.doubleValue() - xMean) / xStd;
        double tmp = 0;
        for (int i = 0; i < weight.length; i++) {
            tmp = tmp * regularX + weight[i];
        }
        tmp = tmp * yStd + yMean;
        return Double.valueOf(tmp);
    }

    public void classify(int step) {
        for (int i = 0; i < step; i++) {
            forward();
            update();
        }
    }

    /** forward and put gradient into grad[]*/
    private void forward() {
        switch (gd) {
        case GradDesc:
            forwardGradDesc();
            break;
        case miniBatch:
            forwardMiniBatch();
            break;
        case SGD:
            forwardSGD();
            break;
        }
    }

    private void update() {
        double step[] = opt.getStep(grad);
        for (int i = 0; i < weight.length; i++) {
            weight[i] += step[i];
        }
    }

    private double calcGrad(LabeledPoint p, int index) {
        return -Math.pow(p.getX(), (double) weight.length - index - 1)
                * (Math.max(p.getLabel(), 0) - sigmoid(p.getY() - (calcFunc(p.getX() * xStd + xMean) - yMean) / yStd));
    }

    private void forwardGradDesc() {
        for (int i = 0; i < weight.length; i++) {
            double sum = 0;
            for (LabeledPoint p : regularizedPoints) {
                sum += calcGrad(p, i);
            }
            grad[i] = sum;
        }
    }

    private void forwardMiniBatch() {
        Collections.shuffle(regularizedPoints);
        ArrayList<LabeledPoint> batch = new ArrayList<>();
        regularizedPoints.stream().limit(bathcSize).forEach(p -> batch.add(p));
        ;
        for (int i = 0; i < weight.length; i++) {

            double sum = 0;
            for (LabeledPoint p : batch) {
                sum += calcGrad(p, i);
            }
            grad[i] = sum;
        }
    }

    private void forwardSGD() {
        Collections.shuffle(regularizedPoints);
        for (int i = 0; i < weight.length; i++) {
            grad[i] = calcGrad(regularizedPoints.get(0), i);
        }
    }

    static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
