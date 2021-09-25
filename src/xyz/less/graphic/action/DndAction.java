package xyz.less.graphic.action;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Drag And Drop
 */
public class DndAction {
	private Node trigger;
	private Consumer<Dragboard> action;
	private boolean enabled;
	
	public DndAction(Node trigger, Consumer<Dragboard> action) {
		this.trigger = trigger;
		this.action = action;
	}
	
	public DndAction enable(boolean value) {
		this.enabled = value;
		trigger.setOnDragOver(e -> {
			if(this.enabled) {
				e.acceptTransferModes(TransferMode.COPY);
			}
			e.consume();
		});
		
		trigger.setOnDragDropped(e -> {
			if(this.enabled && action != null) {
				action.accept(e.getDragboard());
			}
		});
		return this;
	}
	
	public static class DndResult {
		private boolean success;
		private DndType dndType;
		
		public DndResult() {
			this.success = false;
			this.dndType = DndType.UNKNOWN;
		}
		
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public DndType getDndType() {
			return dndType;
		}
		public void setDndType(DndType dndType) {
			this.dndType = dndType;
		}
	}
	
	public enum DndType {
		IMAGE, FILE, LINK, UNKNOWN;
	}
}
