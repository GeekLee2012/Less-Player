package xyz.less.graphic;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import xyz.less.media.FxMediaPlayer;

public abstract class PlayerView extends StackPane {
	protected Stage mainStage;
	protected MediaView mediaView;
	protected FxMediaPlayer mediaPlayer;
	
	public PlayerView(Stage stage, double width, double height) {
		this.mainStage = stage;
		setWidth(width);
		setHeight(height);
		setMediaView(new MediaView());
		addHiddenChildren(mediaView);
	}
	
	public MediaView getMediaView() {
		return mediaView;
	}

	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}

	public FxMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(FxMediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}
	
	public <T> T byId(String id) {
		return Guis.byId(id, mainStage);
	}
	
	public <T> T bySelector(String selector) {
		return Guis.bySelector(selector, mainStage);
	}
	
	public PlayerView addHiddenChildren(Node... nodes) {
		Guis.setVisible(false, nodes);		
		return addChildren(nodes);
	}
	
	public PlayerView addChildren(Node... nodes) {
		Guis.addChildren(this, nodes);
		return this;
	}
	
	public PlayerView addIcons(Image... icons) {
		Guis.addIcons(mainStage, icons);
		return this;
	}
	
	public abstract void setAppTitle(String title);
	
	public abstract void updateTimeText(double currentMinutes, double durationMinutes);

	public abstract void updateProgressBar(double percent);

	public abstract void updatePlayBtn(boolean playing);

	public abstract void updateMetadata(Media media);

	public abstract void highlightPlaylist();

	public abstract void initGraph();
	
	protected abstract void initDatas();
}
