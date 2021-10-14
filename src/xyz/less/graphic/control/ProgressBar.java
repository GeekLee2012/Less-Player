package xyz.less.graphic.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import xyz.less.graphic.Guis;

public class ProgressBar extends HBox {
	private DoubleProperty valueProperty;
	private double min = 0;
	private double max = 1;
	private HBox progress;
	private boolean seekable;
	private double scrollUnit = 0.01;
	
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
		Guis.bind(progress.prefHeightProperty(), this.prefHeightProperty());
	}
	
	private void initEvents() {
		setOnMouseClicked(e -> {
			if(isSeekable()) {
				double percent = e.getX() / getWidthsMax();
				updateProgress(percent);
				valueProperty.set(percent);
			}
		});
		/*
		setOnScroll(e -> {
			if(isSeekable()) {
				scroll(e);
			}
		});
		*/
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
		return valueProperty.get();
	}
	
	public boolean isSeekable() {
		return seekable;
	}

	public void setSeekable(boolean seekable) {
		this.seekable = seekable;
	}
	public double getScrollUnit() {
		return scrollUnit;
	}

	public void setScrollUnit(double value) {
		this.scrollUnit = value;
	}
	
	public void updateProgress(double percent) {
		percent = percent > 0 ? percent : 0;
		percent = percent < 1 ? percent : 1;
		progress.setPrefWidth(getWidthsMax() * percent);
	}

	public void setValue(double value) {
		value = value > min ? value : min;
		value = value < max ? value : max;
		valueProperty.set(value);
		updateProgress(value / (max - min));
	}
	
	private double getWidthsMax() {
		return Math.max(getWidth(), getPrefWidth());
	}
	
	public void scroll(ScrollEvent e) {
		e.consume();
		double deltaX = e.getDeltaY();
		double deltaValue = scrollUnit * (getMax() - getMin());
		if(deltaX > 0) {
			setValue(getValue() + deltaValue);
		} else {
			setValue(getValue() - deltaValue);
		}
	}
}
