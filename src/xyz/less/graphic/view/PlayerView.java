package xyz.less.graphic.view;

import java.io.File;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import xyz.less.bean.ArgsBean;
import xyz.less.bean.Audio;
import xyz.less.bean.ConfigConstant;
import xyz.less.graphic.Guis;
import xyz.less.media.FxMediaPlayer;
import xyz.less.media.MediaPlayerListener;
import xyz.less.util.FileUtil;

public abstract class PlayerView extends StackPane implements MediaPlayerListener {
	protected Stage mainStage;
	private MediaView mediaView;
	private FxMediaPlayer mediaPlayer;
	private ArgsBean argsBean;
	
	public PlayerView(Stage stage, double width, double height) {
		setMainStage(stage);
		setArgsBean();
		setWidth(width);
		setHeight(height);
		setMediaView(new MediaView());
		addHiddenChildren(mediaView);
		setFxMediaPlayer();
		initDatas();
	}
	
	public Stage getMainStage() {
		return mainStage;
	}

	public void setMainStage(Stage mainStage) {
		this.mainStage = mainStage;
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
	
	protected void setArgsBean() {
		argsBean = (ArgsBean)mainStage.getUserData();
	}
	
	protected boolean isEnableAnim() {
		return argsBean.isEnableAnim();
	}
	
	protected boolean hasArgsPlaylist() {
		return argsBean.hasPlaylistUri();
	}
	
	protected String getArgsPlaylistUri() {
		return argsBean.getPlaylistUri();
	}
	
	/**
	 * 一般不建议Override
	 */
	public void playFromArgs() {
		if(hasArgsPlaylist()) {
			File file = new File(getArgsPlaylistUri());
			if(FileUtil.exists(file)) {
				doPlayFromArgs(file);
			}
		}
	}
	
	protected abstract void doPlayFromArgs(File file);
	
	protected abstract void updateProgress(double currentMinutes, double durationMinutes);
	
	protected abstract void updateOnPlaying(boolean playing);
	
	protected abstract void updateOnReady(Audio audio, Map<String, Object> metadata);

	public abstract void initGraph();
	
	/* 不要求实现 */
	public void setAppTitle(String title) {
		mainStage.setTitle(title);
	}
	
	public void setAppTitle() {
		mainStage.setTitle(ConfigConstant.APP_TITLE);
	}
	
	protected void initDatas() {}
	
	protected void highlightPlaylist() {}
	
	@Override
	public void onReady(Audio audio, Map<String, Object> metadata) {
		updateOnReady(audio, metadata);
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
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onNoMedia() {
		updateProgress(0, 0);
		updateOnPlaying(false);
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
	
	@Override
	public void onNoPlayableMedia() {
		// TODO Auto-generated method stub
	}

	public void togglePlay() {
		if(mediaPlayer != null) {
			mediaPlayer.play();
		}
	}

}
