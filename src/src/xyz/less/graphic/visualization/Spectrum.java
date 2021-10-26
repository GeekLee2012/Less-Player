package xyz.less.graphic.visualization;

import javafx.scene.layout.HBox;

public abstract class Spectrum extends HBox {
	private int count = 0;
	
	public abstract void updateGraph(double timestamp, double duration, 
			float[] magnitudes, float[] phases) ;
	
	public void updateGraph(float[] magnitudes, float[] phases) {
		this.updateGraph(magnitudes, phases);
	}
	
	protected double getPaddingX() {
		return getPadding().getLeft() + getPadding().getRight();
	}
	
	protected double getPaddingY() {
		return getPadding().getTop() + getPadding().getBottom();
	}

	protected double getMagnitudePercent(float magnitude) {
		return 1 - Math.abs(magnitude) / 60D;
	}
	
	protected void startCount() {
		startCount(0);
	}
	
	protected void startCount(int count) {
		this.count = count;
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
		count = 0;
	}
}
