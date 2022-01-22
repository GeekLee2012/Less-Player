package xyz.less.graphic.action;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.stage.Stage;
import xyz.less.graphic.Guis;

/**
 * Drag and Move
 */
public class DnmAction  {
//	private double fromX;
//	private double fromY;
	private double fromScreenX;
	private double fromScreenY;
	private double fromSceneX;
	private double fromSceneY;
	
	private boolean enabled = true;
	
	public DnmAction(Stage stage, Node trigger, Consumer<DnmOffset> action, Node... ignoreTriggers) {
		if(stage != null && trigger != null) {
			setupTrigger(stage, trigger, action);
		}
		Guis.applyNodes(node -> {
			node.setOnMouseDragged(e -> {
				e.consume();
			});
		}, ignoreTriggers);
	}
	
	public DnmAction enable(boolean value) {
		this.enabled = value;
		return this;
	}

	private void setupTrigger(Stage stage, Node trigger, Consumer<DnmOffset> action) {
		trigger.setOnMousePressed(e -> {
			e.consume();
			if(!this.enabled) {
				return ;
			}
//			fromX = stage.getX();
//			fromY = stage.getY();
			fromScreenX = e.getScreenX();
			fromScreenY = e.getScreenY();
			fromSceneX = e.getSceneX();
			fromSceneY = e.getSceneY();
		});
		
		trigger.setOnMouseDragged(e -> {
			e.consume();
			if(!this.enabled) {
				return ;
			}
			double toScreenX = e.getScreenX();
			double toScreenY = e.getScreenY();
			double offsetX = toScreenX - fromScreenX;
			double offsetY = toScreenY - fromScreenY;
//			double toX = fromX + offsetX;
//			double toY = fromY + offsetY;
			
			double toX = toScreenX - fromSceneX;
			double toY = toScreenY - fromSceneY;
			stage.setX(toX);
			stage.setY(toY);
			if(action != null) {
				action.accept(new DnmOffset(offsetX, offsetY));
			}
		});
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

