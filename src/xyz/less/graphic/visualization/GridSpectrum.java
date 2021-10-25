package xyz.less.graphic.visualization;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import xyz.less.graphic.Guis;

public class GridSpectrum extends Spectrum {
	private List<GridColumn> gridCols;
	private int rows;
	private int cols;
	private double colMinHeight = 3;
	
	public GridSpectrum(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		gridCols = new ArrayList<>(cols);
		initGraph();
	}

	private void initGraph() {
		Guis.addStyleClass("grid-spectrum", this);
//		setSpacing(1);
		for(int i = 0; i < cols; i++) {
			GridColumn gridCol = new GridColumn(rows);
			gridCol.setAlignment(Pos.BOTTOM_CENTER);
			gridCols.add(gridCol);
		}
		Guis.addChildren(this, gridCols);
	}
	
	public void updateGraph(double timestamp, double duration, 
			float[] magnitudes, float[] phases) {
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
		this.colMinHeight = value > 1 ? value : 1; 
	}
	
	public static class GridColumn extends VBox {
		private Rectangle[] rects;
		private DoubleProperty value = new SimpleDoubleProperty(0) {
	        @Override 
	        protected void invalidated() {
	            super.invalidated();
	            for(int i=0; i< rects.length; i++) {
	            	double last = get() / (rects[i].getHeight() + getSpacing());
//	            	System.out.println(rects[i].getWidth() + "," + rects[i].getHeight());
//	            	rects[i].setVisible((rects.length - i) < last);
	            	if((rects.length - i) < last) {
	            		Guis.addStyleClass("sp-rectangle", rects[i]);
	            	} else {
	            		Guis.removeStyleClass("sp-rectangle", rects[i]);
	            	}
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
		
	}
}
