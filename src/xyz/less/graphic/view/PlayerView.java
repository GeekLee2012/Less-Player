package xyz.less.graphic.view;

import java.io.File;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import xyz.less.Main;
import xyz.less.bean.AppContext;
import xyz.less.bean.Audio;
import xyz.less.bean.Constants;
import xyz.less.graphic.Guis;
import xyz.less.media.FxMediaPlayer;
import xyz.less.media.IMediaPlayerListener;
import xyz.less.util.FileUtil;

public abstract class PlayerView extends StackPane implements IMediaPlayerListener {
	protected AppContext appContext;
	private MediaView mediaView;
	private FxMediaPlayer mediaPlayer;
	
	public PlayerView(double width, double height) {
		//TODO
		setAppContext(Main.getAppContext());
		setWidth(width);
		setHeight(height);
		setMediaView(new MediaView());
		addHiddenChildren(mediaView);
		setFxMediaPlayer();
		initDatas();
	}
	
	public void setAppContext(AppContext context) {
		this.appContext = context;
	}
	
	public Stage getMainStage() {
		return appContext.getMainStage();
	}

	private void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}

	public FxMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	
	public void setFxMediaPlayer() {
		if(mediaPlayer == null) {
			mediaPlayer = new FxMediaPlayer();
			mediaPlayer.setMediaView(mediaView);
			mediaPlayer.addPlayerListener(this);
		}
	}
	
	public void setMediaPlayer() {
		//TODO 自定义MediaPlayer -> SPI
	}
	
	public <T> T byId(String id) {
		return Guis.byId(id, appContext.getMainStage());
	}
	
	public <T> T bySelector(String selector) {
		return Guis.bySelector(selector, appContext.getMainStage());
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
		Guis.addIcons(appContext.getMainStage(), icons);
		return this;
	}
	
	protected boolean isEnableAnim() {
		return appContext.isEnableAnim();
	}
	
	protected boolean hasArgsPlaylist() {
		return appContext.hasArgsPlaylist();
	}
	
	protected String getArgsPlaylistUri() {
		return appContext.getArgsPlaylistUri();
	}
	
	protected boolean isEnableAutoDrawer() {
		return appContext.isEnableAutoDrawer();
	}
	
	protected boolean isEnableCoverAperture() {
		return appContext.isEnableCoverAperture();
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
		getMainStage().setTitle(title);
	}
	
	public void setAppTitle() {
		getMainStage().setTitle(Constants.APP_TITLE);
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
