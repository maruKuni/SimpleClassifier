package application;

public class Point {
	private double x, y;
	private int label;

	public Point(double x, double y, int label) {
		this.x = x;
		this.y = y;
		this.label = label;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return Double.toString(x) + "," + Double.toString(y) + "," + Integer.toString(label);
	}

	public static Point transformIntoCartesian(double x, double y, double CANVAS_WIDTH, double CANVAS_HEIGHT,
			int label) {
		double newX = x - CANVAS_WIDTH / 2;
		double newY = y - CANVAS_HEIGHT / 2;
		return new Point(newX, newY, label);
	}

	public static Point ParsePoint(String line) throws Exception {
		String split[] = line.split(",");
		double x = Double.parseDouble(split[0]);
		double y = Double.parseDouble(split[1]);
		int label = Integer.parseInt(split[2]);
		return new Point(x, y, label);
	}
}
