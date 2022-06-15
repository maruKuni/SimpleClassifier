package application;

import java.util.*;

public class SimpleClassifier {
	public enum GradDesc {
		GradDesc, SGD, miniBatch
	}

	public enum LearningRate {
		constant, Adam, AdaMax, AdaGrad
	}

	private ArrayList<Point> points;
	private double weight[];// index 0 as bias term, last index as coffecient of y
	private GradDesc gd;
	private LearningRate lr;
	private int order;
	private double eta;// for LearningRate as "constant"
}
