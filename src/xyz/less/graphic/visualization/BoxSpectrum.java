package xyz.less.graphic.visualization;

import javafx.scene.layout.HBox;

public abstract class BoxSpectrum extends HBox implements ISpectrum {
	private int count = 0;
	
	@Override
	public void updateGraph(double timestamp, double duration, float[] magnitudes, float[] phases) {
		updateGraph(magnitudes, phases);
	}
	
	protected void updateGraph(float[] magnitudes, float[] phases) {
		//由子类实现
	}
	
	protected double getPaddingX() {
		return getPadding().getLeft() + getPadding().getRight();
	}
	
	protected double getPaddingY() {
		return getPadding().getTop() + getPadding().getBottom();
	}

	protected double getGraphWidth() {
		double width = Math.max(getPrefWidth(), getWidth());
		width = Math.min(width, getMaxWidth());
		return getGtValue(width, 1);
	}
	
	protected double getGraphHeight() {
		double height = Math.max(getPrefHeight(), getHeight());
		return getGtValue(height, 1);
	}
	
	//Great than value
	private double getGtValue(double value, double min) {
		return value > min ? value : min;
	}
	
	protected void setCount(int count) {
		this.count = count;
	}
	
	protected void startCount() {
		setCount(0);
	}
	
	protected int incCount() {
		return ++count;
	}
	
	protected int descCount() {
		return --count;
	}
	
	protected int getCount() {
		return count;
	}
	
	protected void endCount() {
		endCount(0);
	}
	
	protected void endCount(int count) {
		setCount(count);
	}
}
