package xyz.less.graphic;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.HBox;

public class ProgressBar extends HBox {
	private DoubleProperty valueProperty;
	private double min;
	private double max;
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
				double x = e.getX();
				progress.setPrefWidth(x);
				progress.setMaxWidth(x);
				valueProperty.set(x / getWidth());
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
		return progress.getPrefWidth() * getWidth();
	}
	
	public boolean isSeekable() {
		return seekable;
	}

	public void setSeekable(boolean seekable) {
		this.seekable = seekable;
	}

	public void updateProgress(double percent) {
		double width = getWidth() * percent;
		progress.setPrefWidth(width);
		progress.setMaxWidth(width);
	}
	
}
