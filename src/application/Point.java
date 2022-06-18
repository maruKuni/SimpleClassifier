package application;

public class Point {
    protected double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static Point transformIntoCartesian(double x, double y, double CANVAS_WIDTH, double CANVAS_HEIGHT) {
        double newX = x - CANVAS_WIDTH / 2;
        double newY = y - CANVAS_HEIGHT / 2;
        return new Point(newX, newY);
    }

    public static Point ParsePoint(String line) throws Exception {
        String split[] = line.split(",");
        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return Double.toString(x) + "," + Double.toString(y);
    }
}
