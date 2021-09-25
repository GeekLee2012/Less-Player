package xyz.less.graphic.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	private Node trigger;
	private Consumer<DnmOffset> action;
	private boolean enabled;
	private List<Node> ignoreTriggers;
	
	public DnmAction(Stage stage, Node trigger, Consumer<DnmOffset> action, Node... ignoreTriggers) {
		this.stage = stage;
		this.trigger = trigger;
		this.action = action;
		if(ignoreTriggers != null) {
			this.ignoreTriggers = new ArrayList<>();
			this.ignoreTriggers.addAll(Arrays.asList(ignoreTriggers));
		}
		
	}
	
	public DnmAction enable(boolean value) {
		this.enabled = value;
		trigger.setOnMousePressed(event -> {
			event.consume();
			if(!this.enabled) {
				return ;
			}
			fromX = stage.getX();
			fromY = stage.getY();
			fromScreenX = event.getScreenX();
			fromScreenY = event.getScreenY();
		});
		
		trigger.setOnMouseDragged(event -> {
			event.consume();
			if(!this.enabled) {
				return ;
			}
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
			ignoreTriggers.forEach(t -> {
				t.setOnMouseDragged(e -> {
					e.consume();
				});
			});
		}
		return this;
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

