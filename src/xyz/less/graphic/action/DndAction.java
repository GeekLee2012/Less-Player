package xyz.less.graphic.action;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Drag And Drop
 */
public class DndAction {
	private boolean enabled = true;
	
	public DndAction(Node trigger, Consumer<Dragboard> action) {
		if(trigger != null) {
			setupTrigger(trigger, action);
		}
	}
	
	public DndAction enable(boolean value) {
		this.enabled = value;
		return this;
	}

	private void setupTrigger(Node trigger, Consumer<Dragboard> action) {
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
	}
	
	public static class DndResult<T> {
		private boolean success;
		private DndType dndType;
		private T userData;
		
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

		public T getUserData() {
			return userData;
		}

		public void setUserData(T userData) {
			this.userData = userData;
		}
	}
	
	public enum DndType {
		IMAGE, LYRIC, FILE, LINK, UNKNOWN;
	}
}
