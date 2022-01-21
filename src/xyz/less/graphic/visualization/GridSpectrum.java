package xyz.less.graphic.visualization;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import xyz.less.graphic.Guis;

/**
 * 使用时建议设置width和height属性 <br>
 * 必要时还需设置maxWidth和maxHeight属性
 */
public class GridSpectrum extends BoxSpectrum {
	private List<GridColumn> gridCols;
	private int rows;
	private int cols;
	private double colMinHeight;
	
	public GridSpectrum(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		gridCols = new ArrayList<>(cols);
		initGraph();
	}

	private void initGraph() {
		setSpacing(1);
		setAlignment(Pos.BOTTOM_CENTER);
		setColumnMinHeight(3);
		Guis.addStyleClass("grid-spectrum", this);
		
		for(int i = 0; i < cols; i++) {
			GridColumn gridCol = new GridColumn(rows);
			gridCol.setAlignment(Pos.BOTTOM_CENTER);
			gridCols.add(gridCol);
		}
		Guis.addChildren(this, gridCols);
	}
	
	@Override
	protected void updateGraph(float[] magnitudes, float[] phases) {
		startCount();
		gridCols.forEach(col -> {
			double percent = getMagnitudePercent(magnitudes[incCount()]);
			double height = (getHeight() - getPaddingY()) * percent;
			height = height > colMinHeight ? height : colMinHeight; 
			col.setValue(height);
		});
		endCount();
	}

	public void setColumnMinHeight(double value) {
		colMinHeight = value > 3 ? value : 3; 
	}
	
	public static class GridColumn extends VBox {
		private Rectangle[] rects;
		private DoubleProperty value = new SimpleDoubleProperty(0) {
	        @Override 
	        protected void invalidated() {
	            super.invalidated();
	            for(int i=0; i< rects.length; i++) {
	            	double last = get() / (rects[i].getHeight() + getSpacing());
	            	Guis.toggleStyleClass((rects.length - i) < last, "sp-rectangle", rects[i]);
	            }
	        }
	    };
		
		public GridColumn(int rows) {
			rects = new Rectangle[rows];
			setSpacing(1);
			for(int i = 0; i < rows; i++) {
				rects[i] = new Rectangle(12, 5);
				rects[i].setVisible(true);
				Guis.addStyleClass("grid-rectangle", rects[i]);
			}
			Guis.addChildren(this, rects);
		}

		public Rectangle[] getRects() {
			return rects;
		}

		public DoubleProperty getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value.set(value);;
		}
		
		public void setFitWidth(double width) {
			setMinWidth(width);
			setPrefWidth(width);
			setMaxWidth(width);
		}
		
	}
}
