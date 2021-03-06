package xyz.less.graphic.control;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import xyz.less.util.FileUtil;

/**
 * Drag and Drop
 */
public final class DndAction {
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
	
	public class DndContext {
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
			if(board.hasUrl()) {
				return board.getUrl();
			} else if (board.hasFiles()) {
				return FileUtil.toExternalForm(getFile());
			}
			return null;
		}
		
		public File getFile() {
			if(board.hasFiles()) {
				List<File> files = board.getFiles();
				if(files != null && !files.isEmpty()) {
					return files.get(0);
				}
			}
			return null;
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
		
		public Image getImage() {
			return userData instanceof Image ? 
					(Image)userData : null;
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
		
		public boolean isJar() {
			return dndType == DndType.JAR;
		}
	}
	
	public enum DndType {
		IMAGE, LYRIC, AUDIO, DIR, 
		FILE, LINK, JAR, UNKNOWN;
	}

}
