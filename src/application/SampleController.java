package application;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.canvas.*;
import java.net.URL;
import java.util.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.*;

public class SampleController implements Initializable {

	@FXML
	private Button buttonResetAll, buttonGraphReset, buttonPointReset, buttonConfigReset, buttonDo, buttonSave,
			buttonLoad;
	@FXML
	private TextField textFieldOrder, textFieldIterateNum, textFieldBatchSize, textFieldLearningRate;
	@FXML
	private ComboBox<String> comboBoxOptimizer, comboBoxGradDesc;
	@FXML
	private Canvas canvasMain;

	private ArrayList<Point> points;
	private GraphicsContext gc;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		points = new ArrayList<Point>();
		comboBoxGradDesc.getItems().addAll("最急降下法", "ミニバッチ法", "確率的勾配降下法");
		comboBoxGradDesc.setValue("最急降下法");
		comboBoxOptimizer.getItems().addAll("定数", "Adam", "AdaGrad", "AdaMax");
		comboBoxOptimizer.setValue("定数");
		textFieldOrder.setText("3");
		textFieldIterateNum.setText("100");
		textFieldBatchSize.setText("10");
		textFieldLearningRate.setText("1e-2");
		canvasMain.getTransforms().add(new Affine(1, 0, 0, 0, -1, 700));
		gc = canvasMain.getGraphicsContext2D();
		clearCanvas();
		drawAxis();
	}

	@FXML
	protected void handleCanvasReleased(MouseEvent e) {
		int label;
		if (e.getButton() == MouseButton.PRIMARY) {
			label = 1;
		} else {
			label = -1;
		}
		Point p = Point.transformIntoCartesian(e.getX(), e.getY(), canvasMain.getWidth(), canvasMain.getHeight(),
				label);
		points.add(p);
		drawPoint(p);
		System.out.println(p.toString());
	}

	@FXML
	protected void handleDoPressed(ActionEvent e) {

	}

	@FXML
	protected void handleConfigResetPressed(ActionEvent e) {
		textFieldOrder.setText("3");
		textFieldIterateNum.setText("100");
		textFieldBatchSize.setText("10");
		textFieldLearningRate.setText("1e-2");
		comboBoxGradDesc.setValue("最急降下法");
		comboBoxOptimizer.setValue("定数");
	}

	@FXML
	protected void handlePointResetPressed(ActionEvent e) {
		points.clear();
		clearCanvas();
		drawAxis();
	}

	@FXML
	protected void handleGraphResetPressed(ActionEvent e) {
		clearCanvas();
		drawAxis();
		redrawPoints();
	}

	@FXML
	protected void handleAllResetPressed(ActionEvent e) {

	}

	@FXML
	protected void handleSavePressed(ActionEvent e) throws IOException {
		FileChooser fc = new FileChooser();
		fc.setTitle("Save points into CSV");
		fc.getExtensionFilters().add(new ExtensionFilter("Comma Separated Values", "*.csv"));
		File saveFile = fc.showSaveDialog(null);
		if (saveFile == null) {
			return;
		}
		PrintWriter pw = new PrintWriter(new FileOutputStream(saveFile));
		points.stream().forEach(point -> pw.println(point));
		pw.flush();
		pw.close();
	}

	@FXML
	protected void handleLoadPressed(ActionEvent e) throws IOException {
		ArrayList<Point> newPoints = new ArrayList<>();
		FileChooser fc = new FileChooser();
		fc.setTitle("Load points from CSV");
		fc.getExtensionFilters().add(new ExtensionFilter("Comma Separated Value", "*.csv"));
		File loadFile = fc.showOpenDialog(null);
		if (loadFile == null) {
			return;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(loadFile))) {
			String input;
			int lineNum = 1;
			while ((input = br.readLine()) != null) {
				Point p = null;
				try {
					p = Point.ParsePoint(input);
				} catch (Exception ex) {
					ex.printStackTrace();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("エラー");
					alert.setHeaderText(lineNum + "行: データを変換できません");
					return;
				}
				lineNum++;
				newPoints.add(p);
			}
			br.close();
		}
		points.clear();
		points.addAll(newPoints);
		newPoints.clear();
		newPoints = null;

		redrawPoints();
	}

	@FXML
	protected void handleGradDescSelected(ActionEvent e) {
		String selected = comboBoxGradDesc.getValue();
		if (selected.equals("ミニバッチ法")) {
			textFieldBatchSize.setDisable(false);
		} else {
			textFieldBatchSize.setDisable(true);
		}
	}

	@FXML
	protected void handleLearningRateSelected(ActionEvent e) {
		String selected = comboBoxOptimizer.getValue();
		if (selected.equals("定数")) {
			textFieldLearningRate.setDisable(false);
		} else {
			textFieldLearningRate.setDisable(true);
		}
	}

	/** draw cartesian axis */
	private void drawAxis() {
		gc.setStroke(Color.BLACK);
		gc.strokeLine(0, canvasMain.getHeight() / 2, canvasMain.getWidth(), canvasMain.getHeight() / 2);
		gc.strokeLine(canvasMain.getWidth() / 2, 0, canvasMain.getWidth() / 2, canvasMain.getHeight());
		gc.setFont(new Font("Arial Italic", 20));
		gc.strokeText("0", canvasMain.getWidth() / 2, canvasMain.getHeight() / 2);
	}

	/** redraw points. required canvas already filled white and drawn axis */
	private void redrawPoints() {
		points.stream().forEach(p -> drawPoint(p));
	}

	private void drawPoint(Point p) {
		int radius = 10;
		double x = p.getX() + canvasMain.getWidth() / 2;
		double y = p.getY() + canvasMain.getHeight() / 2;
		if (p.getLabel() == 1) {
			gc.setFill(Color.RED);
		} else {
			gc.setFill(Color.BLUE);
		}
		gc.fillOval(x - radius / 2, y - radius / 2, radius, radius);
	}

	/** fill canvas white */
	private void clearCanvas() {
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
	}
}
