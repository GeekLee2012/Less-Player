package xyz.less.graphic.visualization;

import javafx.scene.Node;

public interface ISpectrum {
	void updateGraph(double timestamp, double duration, 
			float[] magnitudes, float[] phases) ;
	
	default public Node toNode() {
		return (Node)this;
	}
	
	default public double getMagnitudePercent(double magnitude) {
		return 1 - Math.abs(magnitude) / 60D;
	}

}
