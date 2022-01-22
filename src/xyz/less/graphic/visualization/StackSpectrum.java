package xyz.less.graphic.visualization;

import javafx.scene.layout.StackPane;

public abstract class StackSpectrum extends StackPane implements ISpectrum {

	@Override
	public void updateGraph(double timestamp, double duration, float[] magnitudes, float[] phases) {
		updateGraph(magnitudes, phases);
	}
	
	protected void updateGraph(float[] magnitudes, float[] phases) {
		//由子类实现
	}

}
