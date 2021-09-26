package xyz.less.graphic.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.HBox;
import xyz.less.graphic.Guis;

public class ProgressBar extends HBox {
	private DoubleProperty valueProperty;
	private double min = 0;
	private double max = 1;
	private HBox progress;
	private boolean seekable;
	
	public ProgressBar() {
		this(0, 1, 0);
	}
	
	public ProgressBar(double min, double max, double value) {
		this.min = min;
		this.max = max;
		this.valueProperty = new SimpleDoubleProperty(value);
		initGraph();
		initEvents();
	}
	
	private void initGraph() {
		progress = new HBox();
		Guis.addChildren(this, progress);
		Guis.addStyleClass("m-progress-bar", this);
		Guis.addStyleClass("progress", progress);
	}
	
	private void initEvents() {
		setOnMouseClicked(e -> {
			if(isSeekable()) {
				double percent = e.getX() / getProgressMaxWidth();
				updateProgress(percent);
				valueProperty.set(percent);
			}
		});
	}

	public void addListener(ChangeListener<? super Number> listener) {
		valueProperty.addListener(listener);
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}
	
	public double getValue() {
//		double percent = progress.getPrefWidth() / getProgressMaxWidth();
//		return  (max - min) * percent;
		return valueProperty.get();
	}
	
	public boolean isSeekable() {
		return seekable;
	}

	public void setSeekable(boolean seekable) {
		this.seekable = seekable;
	}

	public void updateProgress(double percent) {
		percent = percent > 0 ? percent : 0;
		percent = percent < 1 ? percent : 1;
		progress.setPrefWidth(getProgressMaxWidth() * percent);
	}

	public void setValue(double value) {
		value = value > min ? value : min;
		value = value < max ? value : max;
		valueProperty.set(value);
		updateProgress(value / (max - min));
	}
	
	private double getProgressMaxWidth() {
		return Math.max(getWidth(), getPrefWidth());
	}
}
