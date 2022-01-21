package xyz.less.graphic.visualization;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import xyz.less.graphic.Guis;

/**
 * 折线（多边形）频谱
 * 使用时建议设置width和height属性 <br>
 * 必要时还需设置maxWidth和maxHeight属性
 */
public class PolyLineSpectrum extends BoxSpectrum {
	private static final int DEFAULT_SAMPLES = 128;
	private int samples;
	private Double[] samplePoints;
	private Polyline poly = new Polyline();
	
	private double offsetX = 15;
	private double offsetY = 2;
	
	public PolyLineSpectrum(int samples, double maxWidth) {
		this.samples = samples > 0 ? samples : DEFAULT_SAMPLES;
		this.samplePoints= new Double[this.samples << 1];
		
		setMaxWidth(maxWidth);
		setAlignment(Pos.BOTTOM_CENTER);
		Guis.addStyleClass("polyline-spectrum", this);
		
		getChildren().add(poly);
		Guis.addStyleClass("sp-polyline", poly);
	}
	
	@Override
	protected void updateGraph(float[] magnitudes, float[] phases) {
		resetGraph();
		double width = getGraphWidth() / samples;
		double maxX = 0, maxY = 0;
		int i = 0, j = 0, count = 0;
		for (float magnitude : magnitudes) {
			if(count++ >= samples) {
				break;
			}
			double height = getGraphHeight() * getMagnitudePercent(magnitude);
			height = height > offsetY ? height : (offsetY + 2);
			
			double x = (width * j++) + offsetX;
			double y = height;
			samplePoints[i] = x;
			samplePoints[i+1] = y;
			i = i + 2;
			
			//做垂直翻转时的原点坐标（maxX/2, maxY/2）
			maxX = x > maxX ? x : maxX;
			maxY = y > maxY ? y : maxY;
			
//			System.out.println("(" + x + "," + y + ") " + width);
		}
		//添加绘制点和连线
		poly.getPoints().addAll(offsetX, offsetY); //起点
		poly.getPoints().addAll(samplePoints);
		poly.getPoints().addAll(maxX, offsetY); //终点
		//垂直翻转
		Scale scale = new Scale(1, -1, maxX/2, maxY/2);
		Translate tran = new Translate(0, -4);
		poly.getTransforms().setAll(scale, tran);
	}

	private void resetGraph() {
		List<Double> points = poly.getPoints();
		if(points != null) {
			poly.getPoints().removeAll(points);
		}
	}
	
}
