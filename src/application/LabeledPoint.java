package application;

public class LabeledPoint extends Point {
    private int label;

    public LabeledPoint(double x, double y, int label) {
        super(x, y);
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return Double.toString(x) + "," + Double.toString(y) + "," + Integer.toString(label);
    }

    public static LabeledPoint transformIntoCartesian(double x, double y, double CANVAS_WIDTH, double CANVAS_HEIGHT,
            int label) {
        double newX = x - CANVAS_WIDTH / 2;
        double newY = y - CANVAS_HEIGHT / 2;
        return new LabeledPoint(newX, newY, label);
    }

    public static LabeledPoint ParsePoint(String line) throws Exception {
        String split[] = line.split(",");
        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        int label = Integer.parseInt(split[2]);
        return new LabeledPoint(x, y, label);
    }
}
