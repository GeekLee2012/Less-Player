package xyz.less.graphic.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import xyz.less.graphic.Guis;

/**
 * 进度条
 */
public final class ProgressBar extends HBox {
	private DoubleProperty valueProp;
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
		this.valueProp = new SimpleDoubleProperty(value);
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
				valueProp.set(percent);
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
		valueProp.addListener(listener);
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
		return valueProp.get();
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
		valueProp.set(value);
		updateProgress(value / (max - min));
	}
	
	private double getWidthsMax() {
		return Math.max(getWidth(), getPrefWidth());
	}
	
	public void scroll(ScrollEvent e) {
		e.consume();
		double delta = e.getDeltaY() > 0 ? 1 : -1;
		double deltaValue = delta * scrollUnit * (getMax() - getMin());
		setValue(getValue() + deltaValue);
	}
}
