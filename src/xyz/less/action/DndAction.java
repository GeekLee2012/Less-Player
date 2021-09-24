package xyz.less.action;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Drag And Drop
 */
public class DndAction {
	
	public DndAction() {
		
	}
	
	public DndAction enable(Node node, Consumer<Dragboard> consumer) {
		node.setOnDragOver(e -> {
			e.acceptTransferModes(TransferMode.COPY);
			e.consume();
		});
		
		node.setOnDragDropped(e -> {
//			System.out.println(e);
			if(consumer != null) {
				consumer.accept(e.getDragboard());
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
