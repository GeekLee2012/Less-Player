package xyz.less.bean;

import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import xyz.less.graphic.skin.MiniSkin;
import xyz.less.graphic.skin.SkinManager;
import xyz.less.media.FxMediaPlayer;
import xyz.less.media.PlaybackQueue.PlayMode;

public final class AppContext {
	private static final AppContext context = new AppContext();
	private Configuration config;
	private Stage mainStage;
	private SkinManager skinManager;
	private FxMediaPlayer mediaPlayer;
	
	private AppContext() {
		
	}
	
	public static AppContext get() {
		return context;
	}
	
	public static AppContext get(Stage mainStage) {
		context.setMainStage(mainStage);
		return context;
	}
	
	public AppContext setMainStage(Stage stage) {
		this.mainStage = stage;
		return this;
	}
	
	public Stage getMainStage() {
		return mainStage;
	}

	public Configuration getConfiguration() {
		return config;
	}

	public void setConfiguration(Configuration cfg) {
		this.config = cfg;
	}
	
	public SkinManager getSkinManager() {
		if(skinManager == null) {
			skinManager = new SkinManager();
		}
		return skinManager;
	}

	public void setSkinManager(SkinManager skinManager) {
		this.skinManager = skinManager;
	}
	
	public FxMediaPlayer getMediaPlayer() {
		if(mediaPlayer == null) {
			mediaPlayer = new FxMediaPlayer();
			mediaPlayer.setMediaView(new MediaView());
			mediaPlayer.setVolume(Constants.INITIAL_VOLUME);
			mediaPlayer.setPlayMode(PlayMode.SHUFFLE);
		}
		return mediaPlayer;
	}
	
	public String getSkinName() {
		return config.getSkinName();
	}
	
	public void setSkinName(String name) {
		config.setSkinName(name);
	}
	
	public boolean isEnableAnim() {
		return config.isEnableAnim();
	}
	
	public boolean hasArgsPlaylist() {
		return config.hasPlaylistUri();
	}
	
	public String getArgsPlaylistUri() {
		return config.getArgsPlaylistUri();
	}
	
	public boolean isEnableAutoDrawer() {
		return config.isEnableAutoDrawer();
	}

	public boolean isEnableCoverAperture() {
		return config.isEnableCoverAperture();
	}
	
	public boolean isMiniSkin() {
		return MiniSkin.NAME.equalsIgnoreCase(getSkinName());
	}

}
