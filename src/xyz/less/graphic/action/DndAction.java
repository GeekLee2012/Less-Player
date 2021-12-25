package xyz.less.graphic.action;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Drag And Drop
 */
public class DndAction {
	private boolean enabled = true;
	
	public DndAction(Node trigger, Consumer<DndContext> action) {
		if(trigger != null) {
			setupTrigger(trigger, action);
		}
	}
	
	public DndAction enable(boolean value) {
		this.enabled = value;
		return this;
	}

	private void setupTrigger(Node trigger, Consumer<DndContext> action) {
		trigger.setOnDragOver(e -> {
			if(this.enabled) {
				e.acceptTransferModes(TransferMode.COPY);
			}
			e.consume();
		});
		
		trigger.setOnDragDropped(e -> {
			if(this.enabled && action != null) {
				action.accept(new DndContext(e.getDragboard()));
			}
		});
	}
	
	public static class DndContext {
		private Dragboard board;
		private BooleanProperty successProp;
		private DndType dndType;
		private Object userData;
		
		public DndContext() {
			successProp = new SimpleBooleanProperty(true);
			setDndType(DndType.UNKNOWN);
		}
		
		public DndContext(Dragboard board) {
			this();
			setDragboard(board);
		}
		private void setDragboard(Dragboard board) {
			this.board = board;
		}
		public String getUrl() {
			return board.getUrl();
		}
		public File getFile() {
			List<File> files = board.getFiles();
			if(files == null || files.isEmpty()) {
				return null;
			}
			return files.get(0);
		}
		public boolean isSuccess() {
			return this.successProp.get();
		}
		public void setSuccess(boolean success) {
			this.successProp.set(success);
		}
		public BooleanProperty successProperty() {
			return this.successProp;
		}
		public DndType getDndType() {
			return dndType;
		}
		public void setDndType(DndType dndType) {
			this.dndType = dndType;
		}

		public Object getUserData() {
			return userData;
		}

		public void setUserData(Object userData) {
			this.userData = userData;
		}
		
		public boolean isImage() {
			return dndType == DndType.IMAGE;
		}
		
		public boolean isLyric() {
			return dndType == DndType.LYRIC;
		}
		
		public boolean isAudio() {
			return dndType == DndType.AUDIO;
		}
		
		public boolean isDirectory() {
			return dndType == DndType.DIR;
		}
		
		public boolean isFile() {
			return dndType == DndType.FILE;
		}
		
		public boolean isLink() {
			return dndType == DndType.LINK;
		}
	}
	
	public enum DndType {
		IMAGE, LYRIC, AUDIO, DIR, FILE, LINK, UNKNOWN;
	}

}
