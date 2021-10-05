package xyz.less.graphic.visualization;

import javafx.scene.layout.HBox;

public abstract class Spectrum extends HBox {
	protected int index = 0;
	
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
}
