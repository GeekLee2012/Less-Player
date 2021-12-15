package xyz.less.graphic.effect;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class AutoDrawerEffect {
	private boolean enable = true;
	private static final double OFFSET = 10;
	private static final double HANDLE_SIZE = 5;
	
	public AutoDrawerEffect(Stage stage) {
		setupDrawer(stage);
	}
	
	public AutoDrawerEffect enable(boolean value) {
		this.enable = value;
		return this;
	}
	
	private void setupDrawer(Stage stage) {
		stage.getScene().setOnMouseEntered(e -> {
			if(!enable) {
				return ;
			}
			double x = stage.getX();
			double y = stage.getY();
			double width = stage.getWidth();
//			double height = stage.getHeight();
			Rectangle2D screenRect = Screen.getPrimary().getBounds();
			if(y <= screenRect.getMinY() + OFFSET) {
				stage.setY(0);
			}else if(x <= screenRect.getMinX() + OFFSET) {
				stage.setX(0);
			} else if(x + width >= screenRect.getMaxX() - OFFSET) {
				stage.setX(screenRect.getMaxX() - width);
			} 
		});
		stage.getScene().setOnMouseExited(e -> {
			if(!enable) {
				return ;
			}
			double x = stage.getX();
			double y = stage.getY();
			double width = stage.getWidth();
			double height = stage.getHeight();
			Rectangle2D screenRect = Screen.getPrimary().getBounds();
			if(y <= screenRect.getMinY() + OFFSET) {
				stage.setY(-height + HANDLE_SIZE);
			} else if(x <= screenRect.getMinX() + OFFSET) {
				stage.setX(-width + HANDLE_SIZE);
			} else if(x + width >= screenRect.getMaxX() - OFFSET) {
				stage.setX(screenRect.getMaxX() - HANDLE_SIZE);
			} 
		});
	}
}
