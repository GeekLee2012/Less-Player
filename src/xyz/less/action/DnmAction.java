package xyz.less.action;

import java.util.Arrays;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Drag And Move
 */
public class DnmAction  {
	private double fromX;
	private double fromY;
	private double fromScreenX;
	private double fromScreenY;
	private Stage stage;
	
	public DnmAction(Stage stage) {
		this.stage = stage;
	}
	
	public DnmAction enable(Node trigger, Consumer<DnmOffset> action, Node... ignoreTriggers) {
		trigger.setOnMousePressed(event -> {
			event.consume();
			fromX = stage.getX();
			fromY = stage.getY();
			fromScreenX = event.getScreenX();
			fromScreenY = event.getScreenY();
		});
		
		trigger.setOnMouseDragged(event -> {
			event.consume();
			double toScreenX = event.getScreenX();
			double toScreenY = event.getScreenY();
			double offsetX = toScreenX - fromScreenX;
			double offsetY = toScreenY - fromScreenY;
			double toX = fromX + offsetX;
			double toY = fromY + offsetY;
			stage.setX(toX);
			stage.setY(toY);
			if(action != null) {
				action.accept(new DnmOffset(offsetX, offsetY));
			}
		});
		
		if(ignoreTriggers != null) {
			Arrays.asList(ignoreTriggers).forEach(t -> {
				t.setOnMouseDragged(e -> {
					e.consume();
				});
			});
		}
		return this;
	}
	
	public DnmAction enable(Node trigger, Node... ignoreTriggers) {
		return enable(trigger, null, ignoreTriggers);
	}
	
	public static class DnmOffset {
		private double offsetX;
		private double offsetY;
		public DnmOffset(double offsetX, double offsetY) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
		public double getOffsetX() {
			return offsetX;
		}
		public void setOffsetX(double offsetX) {
			this.offsetX = offsetX;
		}
		public double getOffsetY() {
			return offsetY;
		}
		public void setOffsetY(double offsetY) {
			this.offsetY = offsetY;
		}
		@Override
		public String toString() {
			return "DnmOffset [offsetX=" + offsetX + ", offsetY=" + offsetY + "]";
		}
	}
}

