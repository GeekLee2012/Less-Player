package xyz.less.graphic.visualization;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import xyz.less.graphic.Guis;

/**
 * 使用时建议设置width和height属性 <br>
 * 必要时还需设置maxWidth和maxHeight属性
 */
public class RectangleSpectrum extends BoxSpectrum {
	private List<Region> rectList;
	private int rectNums;
	private double rectMinHeight = 1;
	
	public RectangleSpectrum(int rectNums) {
		this.rectNums = rectNums;
		this.rectList = new ArrayList<>(rectNums);
		initGraph();
	}

	private void initGraph() {
		Guis.addStyleClass("rectangle-spectrum", this);
		setSpacing(1);
		setAlignment(Pos.BOTTOM_CENTER);
		
		for(int i = 0; i < rectNums; i++) {
			HBox rect = new HBox();
			Guis.addStyleClass("sp-rectangle", rect);
			rectList.add(rect);
		}
		Guis.addChildren(this, rectList);
	}
	
	@Override
	protected void updateGraph(float[] magnitudes, float[] phases) {
		startCount();
		//TODO
		double spacing = getSpacing() + 0.65;
		double width = (getWidth() - getPaddingX()) / rectNums - spacing;
		rectList.forEach(rect -> {
			double percent = getMagnitudePercent(magnitudes[incCount()]);
			double height = (getHeight() - getPaddingY()) * percent;
			height = height > rectMinHeight ? height : rectMinHeight; 
			rect.setPrefSize(width, height);
			rect.setMaxSize(width, height);
		});
		endCount();
	}
	
	public void setRectangleMinHeight(double value) {
		this.rectMinHeight = value > 1 ? value : 1; 
	}
	
}
