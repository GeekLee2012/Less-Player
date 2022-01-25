package xyz.less.graphic.visualization;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.transform.Rotate;
import xyz.less.graphic.Guis;

public class CircleSpectrum extends StackSpectrum {
	private int samples = 36;
	private double radius = 50; //中间圆的半径
	private double boxMaxHeight = 66;
	private List<Region> boxes = new ArrayList<>();
	
	public CircleSpectrum(double radius, int samples) {
		this.radius = radius;
		this.samples = samples;
		
		setAlignment(Pos.CENTER);
		initGraph();
	}
	
	private void initGraph() {
		//TODO
		double pivotX0 = 0;
		double pivotY0 = 0;
//		System.out.println("(" + pivotX0 + "," + pivotY0 + ")");
		for(int i = 0; i < samples; i++) {
			Region box = new Region();
			Guis.addStyleClass("circle-region", box);
			
			double angle = 360D / samples * i;
			double radian = getRadian(angle);
			double sinA = Math.sin(radian);
			double cosA = Math.cos(radian);
			double maxWidth = box.getMaxWidth();
			double maxHeight = box.getMaxHeight();
			double tranX = pivotX0 + maxWidth / 2 + radius * sinA;
			double tranY = pivotY0 + maxHeight / 2 - radius * cosA;
			tranX = tranX + maxHeight * sinA;
			tranY = tranY - maxHeight * cosA;
			
			box.setTranslateX(tranX);
			box.setTranslateY(tranY);
			
			Rotate r = new Rotate(angle, pivotX0, pivotY0);
			box.getTransforms().add(r);
			
			getChildren().add(box);
			boxes.add(box);
		}
	}
	
	@Override
	protected void updateGraph(float[] magnitudes, float[] phases) {
		for(int i = 0; i < samples; i++) {
			Region box = boxes.get(i);
			float percent = magnitudes[i];
			double maxHeight = boxMaxHeight * getMagnitudePercent(percent);
			maxHeight = maxHeight > 6 ? maxHeight : 6;
			box.setMaxHeight(maxHeight);
			
			double angle = 360D / samples * i;
			double radian = getRadian(angle);
			double sinA = Math.sin(radian);
			double cosA = Math.cos(radian);
			double maxWidth = box.getMaxWidth();
			maxHeight = box.getMaxHeight();
			double tranX = maxWidth / 2 + radius * sinA;
			double tranY = maxHeight / 2 - radius * cosA;
			tranX = tranX + maxHeight * sinA;
			tranY = tranY - maxHeight * cosA;
			
//			System.out.println(i + ": " + tranX + "," + tranY);
			box.setTranslateX(tranX);
			box.setTranslateY(tranY);
		}
	}

	private double getRadian(double angle) {
		return angle / 360D * 2 * Math.PI;
	}

}
