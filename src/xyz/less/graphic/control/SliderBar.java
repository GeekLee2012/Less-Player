package xyz.less.graphic.control;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import xyz.less.graphic.Guis;

public class SliderBar extends StackPane {
	private ProgressBar delegate;
	private HBox thumb;
	private double fromX;
	private double fromScreenX;
	
	public SliderBar() {
		this(0, 1, 0);
	}
	
	public SliderBar(double min, double max, double value) {
		delegate = new ProgressBar(min, max, value);
		delegate.setPrefSize(-1, 5);
		initGraph();
		initEvents();
	}
	
	private void initGraph() {
		thumb = new HBox();
		Guis.setAlignment(Pos.CENTER, this);
		Guis.addChildren(this, delegate, thumb);
		Guis.addStyleClass("m-slider-bar", this);
		Guis.addStyleClass("thumb", thumb);
	}
	
	private void initEvents() {
		delegate.setOnMouseClicked(e -> {
			double x = e.getX();
			setValue(x / delegate.getWidth());
		});
		
		thumb.setOnMousePressed(e -> {
			e.consume();
			fromX = thumb.getLayoutX() + thumb.getTranslateX();
			fromScreenX = e.getSceneX();
		});
		
		thumb.setOnMouseDragged(e -> {
			e.consume();
			double offsetX =  e.getSceneX() - fromScreenX;
			double toX = fromX + offsetX;
			toX = toX > 0 ? toX : 0;
			toX = toX < getPrefWidth() ? toX : getWidth();
			setValue(toX / getWidth());
		});
		
		setOnScroll(e -> {
			e.consume();
			double deltaX = e.getDeltaY();
			if(deltaX > 0) {
				setValue(getValue() + 0.05);
			} else {
				setValue(getValue() - 0.05);
			}
		});
	}

	public void addListener(ChangeListener<? super Number> listener) {
		delegate.addListener(listener);
	}

	public double getMin() {
		return delegate.getMin();
	}

	public void setMin(double min) {
		delegate.setMin(min);
	}

	public double getMax() {
		return delegate.getMax();
	}

	public void setMax(double max) {
		delegate.setMax(max);
	}
	
	public void setValue(double value) {
		double min = delegate.getMin();
		double max = delegate.getMax();
		value = value > min ? value : min;
		value = value < max ? value : max;
		delegate.setValue(value);
		updateProgress(value / (max - min));
	}
	
	public double getValue() {
		return delegate.getValue();
	}

	public void updateProgress(double percent) {
		delegate.updateProgress(percent);
		thumb.setTranslateX(getPrefWidth() * (-0.5D + percent));
	}

	public void setPrefSize(double width, double height) {
		super.setPrefSize(width, height);
		delegate.setPrefWidth(width);
		delegate.setMaxHeight(height);
	}

	public double getHalf() {
		return (getMax() - getMin()) / 2;
	}
}
