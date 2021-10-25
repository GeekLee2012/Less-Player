package xyz.less.graphic.control;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import xyz.less.graphic.Guis;

public class SliderBar extends StackPane implements ChangeListener<Number> {
	private ProgressBar delegate;
	private HBox thumb;
	private double fromX;
	private double fromSceneX;
	private final static double SCROLL_UNIT = 0.05;
	private final static double THUMB_BASE_PERCENT = -0.5;
	
	public SliderBar() {
		this(0, 1, 0);
	}
	
	public SliderBar(double min, double max, double value) {
		delegate = new ProgressBar(min, max, value);
		thumb = new HBox();
		
		initGraph();
		initEvents();
	}
	
	private void initGraph() {
		Guis.setAlignment(Pos.CENTER, this);
		Guis.addChildren(this, delegate, thumb);
		Guis.addStyleClass("m-slider-bar", this);
		Guis.addStyleClass("thumb", thumb);
		
		setScrollUnit(SCROLL_UNIT);
		addListener(this);
	}
	
	private void initEvents() {
		delegate.setOnMouseClicked(e -> {
			setValue(e.getX() / delegate.getWidth());
		});
		
		thumb.setOnMousePressed(e -> {
			e.consume();
			fromX = thumb.getLayoutX() + thumb.getTranslateX();
			fromSceneX = e.getSceneX();
		});
		
		thumb.setOnMouseDragged(e -> {
			e.consume();
			double offsetX =  e.getSceneX() - fromSceneX;
			double toX = fromX + offsetX;
			toX = toX > 0 ? toX : 0;
			toX = toX < getWidthsMax() ? toX : getWidthsMax();
			setValue(toX / getWidthsMax());
		});
		
		setOnScroll(e -> {
			scroll(e);
		});
	}
	
	public void setThumbVisible(boolean value) {
		thumb.setVisible(value);
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
	
	public double getScrollUnit() {
		return delegate.getScrollUnit();
	}

	public void setScrollUnit(double value) {
		delegate.setScrollUnit(value);
	}

	public void setValue(double value) {
		delegate.setValue(value);
	}
	
	public double getValue() {
		return delegate.getValue();
	}

	private void updateThumbPos() {
		setThumbPos(getValue() / getMaxLength());
	}
	
	private void setThumbPos(double percent) {
		thumb.setTranslateX(getWidthsMax() * (THUMB_BASE_PERCENT + percent));
	}

	public void setPrefSize(double width, double height) {
		super.setPrefSize(width, height);
		delegate.setPrefWidth(width);
		delegate.setMaxHeight(height);
	}
	
	private double getMaxLength() {
		return getMax() - getMin();
	}
	
	private double getWidthsMax() {
		return Math.max(getWidth(), getPrefWidth());
	}
	
	public double getHalf() {
		return getMaxLength() / 2;
	}
	
	public void scroll(ScrollEvent e) {
		delegate.scroll(e);
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
		updateThumbPos();
	}
	
}
