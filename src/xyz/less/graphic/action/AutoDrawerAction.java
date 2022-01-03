package xyz.less.graphic.action;

import java.util.function.Consumer;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import xyz.less.graphic.Guis;

/**
 * 抽屉效果，即贴边(屏幕边缘，下边缘除外)自动隐藏
 */
public class AutoDrawerAction {
	private boolean enable = true;
	private static final double OFFSET = 10;
	private static final double HANDLE_SIZE = 5;
	private Consumer<Stage> onHiddenConsumer;
	private Consumer<Stage> onShownConsumer;
	
	public AutoDrawerAction(Stage stage) {
		setupDrawer(stage);
	}
	
	public AutoDrawerAction enable(boolean value) {
		this.enable = value;
		return this;
	}
	
	public AutoDrawerAction setOnHidden(Consumer<Stage> onHiddenAction) {
		this.onHiddenConsumer = onHiddenAction;
		return this;
	}

	public AutoDrawerAction setOnShown(Consumer<Stage> onShownAction) {
		this.onShownConsumer = onShownAction;
		return this;
	}

	private void setupDrawer(Stage stage) {
		stage.getScene().setOnMouseEntered(e -> {
			e.consume();
			if(!enable) {
				return ;
			}
			double x = stage.getX();
			double y = stage.getY();
			double width = stage.getWidth();
//			double height = stage.getHeight();
			Rectangle2D screenRect = Screen.getPrimary().getBounds();
			boolean shown = false;
			if(y <= screenRect.getMinY() + OFFSET) {
				shown = true;
				stage.setY(0);
			}else if(x <= screenRect.getMinX() + OFFSET) {
				shown = true;
				stage.setX(0);
			} else if(x + width >= screenRect.getMaxX() - OFFSET) {
				shown = true;
				stage.setX(screenRect.getMaxX() - width);
			}
			if(shown) {
				Guis.ifPresent(onShownConsumer, c -> {
					c.accept(stage);
				});
			}
		});
		stage.getScene().setOnMouseExited(e -> {
			e.consume();
			if(!enable) {
				return ;
			}
			double sx = e.getScreenX();
			double sy = e.getScreenY();
			double x = stage.getX();
			double y = stage.getY();
			double width = stage.getWidth();
			double height = stage.getHeight();
			if(isPointInStage(stage, sx, sy)) {
				//TODO Bug
				return ;
			}
			Rectangle2D screenRect = Screen.getPrimary().getBounds();
			boolean hidden = false;
			if(y <= screenRect.getMinY() + OFFSET) {
				hidden = true;
				stage.setY(-height + HANDLE_SIZE);
			} else if(x <= screenRect.getMinX() + OFFSET) {
				hidden = true;
				stage.setX(-width + HANDLE_SIZE);
			} else if(x + width >= screenRect.getMaxX() - OFFSET) {
				hidden = true;
				stage.setX(screenRect.getMaxX() - HANDLE_SIZE);
			}
			if(hidden) {
				Guis.ifPresent(onHiddenConsumer, c -> {
					c.accept(stage);
				});
			}
		});
	}
	
	private static boolean isPointInStage(Stage stage, double x, double y) {
		double sx = stage.getX();
		double sy = stage.getY();
		double width = stage.getWidth();
		double height = stage.getHeight();
		double error = 5;
		return (x >= sx - error && x <= sx + width + error)
				&& (y >= sy - error && y <= sy + height + error);
	}
}
