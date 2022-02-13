package xyz.less.graphic.control;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.stage.Stage;
import xyz.less.graphic.Guis;

/**
 * Drag and Move
 */
public final class DnmAction  {
	private double fromSceneX;
	private double fromSceneY;
	
	private boolean enabled = true;
	private boolean alwaysOnTop;
	private boolean dragged;
	
	public DnmAction(Stage stage, Node trigger, Consumer<Pos> action, Node... ignoreTriggers) {
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

	private void setupTrigger(Stage stage, Node trigger, Consumer<Pos> action) {
		trigger.setOnMousePressed(e -> {
			e.consume();
			if(!enabled) {
				return ;
			}
			alwaysOnTop = stage.isAlwaysOnTop();
			fromSceneX = e.getSceneX();
			fromSceneY = e.getSceneY();
		});
		
		trigger.setOnMouseDragged(e -> {
			e.consume();
			if(!this.enabled) {
				return ;
			}
			dragged = true;
			stage.setAlwaysOnTop(true); //移动时保持置顶

			double toX = e.getScreenX() - fromSceneX;
			double toY = e.getScreenY() - fromSceneY;

			stage.setX(toX);
			stage.setY(toY);

			if(action != null) {
				action.accept(new Pos(stage.getX(), stage.getY()));
			}
		});
		
		trigger.setOnMouseReleased(e -> {
			if(action != null) {
				action.accept(new Pos(stage.getX(), stage.getY()));
			}
			if(dragged) {
				stage.setAlwaysOnTop(alwaysOnTop);
				dragged = false;
			}
		});
	}

	public static class Pos {
		private double x;
		private double y;

		public Pos(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

	}

}

