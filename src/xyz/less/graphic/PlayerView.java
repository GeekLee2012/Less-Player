package xyz.less.graphic;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import xyz.less.media.FxMediaPlayer;
import xyz.less.media.MediaPlayerListener;

public abstract class PlayerView extends StackPane implements MediaPlayerListener {
	protected Stage mainStage;
	private MediaView mediaView;
	private FxMediaPlayer mediaPlayer;
	
	public PlayerView(Stage stage, double width, double height) {
		this.mainStage = stage;
		setWidth(width);
		setHeight(height);
		setMediaView(new MediaView());
		addHiddenChildren(mediaView);
		initDatas();
	}
	
	private void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}

	public FxMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	
	public void setFxMediaPlayer() {
		if(mediaPlayer == null) {
			this.mediaPlayer = new FxMediaPlayer();
			this.mediaPlayer.setMediaView(mediaView);
			this.mediaPlayer.addPlayerListener(this);
		}
	}
	
	public void setMediaPlayer() {
		//TODO 自定义MediaPlayer -> SPI
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
	
	protected abstract void updateProgress(double currentMinutes, double durationMinutes);
	
	protected abstract void updateOnPlaying(boolean playing);
	
	protected abstract void updateOnReady(Media media);

	public abstract void initGraph();
	
	protected void initDatas() {
		//不要求子类必须实现
	}
	
	protected void highlightPlaylist() {
		//不要求子类必须实现
	}
	
	@Override
	public void onReady(Media Media) {
		updateOnReady(Media);
	}
	
	@Override
	public void onPlaying() {
		updateOnPlaying(true);
		highlightPlaylist();
	}
	
	@Override
	public void onPaused() {
		updateOnPlaying(false);
	}
	
	@Override
	public void onCurrentChanged(double currentMinutes, double durationMinutes) {
		updateProgress(currentMinutes, durationMinutes);
	}
	
	@Override
	public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		
	}
	
	@Override
	public void onNoMedia() {
		updateProgress(0, 0);
	}
	
	@Override
	public void onEndOfMedia() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onReset() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onError() {
		// TODO Auto-generated method stub
	}

	public void togglePlay() {
		mediaPlayer.play();
	}
	
}
