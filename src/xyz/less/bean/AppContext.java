package xyz.less.bean;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.graphic.Guis;
import xyz.less.graphic.skin.MiniSkin;
import xyz.less.graphic.skin.Skin;
import xyz.less.graphic.skin.SkinManager;
import xyz.less.media.PlaybackQueue.PlayMode;
import xyz.less.service.DefaultMediaService;
import xyz.less.service.IMediaService;
import xyz.less.util.FileUtil;

public final class AppContext {
	private static final AppContext CONTEXT = new AppContext();
	private Configuration config;
	private Stage mainStage;
	private SkinManager skinManager;
	private IMediaService mediaService;
	
	private AppContext() {
		
	}
	
	public static AppContext get() {
		return CONTEXT;
	}

	public static AppContext init(String[] args) {
		return get().setConfiguration(Configuration.parseFrom(args));
	}

	public AppContext initMainStage(Stage mainStage) {
		mainStage.initStyle(StageStyle.TRANSPARENT);
		mainStage.setOnCloseRequest(e -> Guis.exitApplication());
		return setMainStage(mainStage);
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

	public AppContext setConfiguration(Configuration cfg) {
		this.config = cfg;
		return this;
	}
	
	public SkinManager getSkinManager() {
		if(skinManager == null) {
			skinManager = new SkinManager();
		}
		return skinManager;
	}

	public AppContext setSkinManager(SkinManager skinMgr) {
		this.skinManager = skinMgr;
		return this;
	}
	
	public IMediaService getMediaService() {
		if(mediaService == null) {
			mediaService = new DefaultMediaService();
			mediaService.setMediaView(new MediaView());
			mediaService.setVolume(Constants.DEFAULT_VOLUME);
			mediaService.setPlayMode(PlayMode.SHUFFLE);
		}
		return mediaService;
	}
	
	public String getSkinName() {
		return config.getSkinName();
	}
	
	public AppContext setSkinName(String name) {
		config.setSkinName(name);
		return this;
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
	
	public boolean isEnableCoverAperture() {
		return config.isEnableCoverAperture();
	}
	
	public boolean isMiniSkin() {
		return MiniSkin.NAME.equalsIgnoreCase(getSkinName());
	}

	public Skin switchToSkin(String skinName) {
		return getSkinManager().switchToSkin(skinName);
	}
	
	public Audio getCurrent() {
		return getMediaService().getCurrent();
	}
	
	public Map<String, Object> getCurrentMetadata() {
		return getMediaService().getCurrentMetadata();
	}
	
	public String[] getSuffixes() {
		 Set<String> suffixSet = getMediaService().getSuffixSet();
		 return suffixSet.toArray(new String[suffixSet.size()]);
	}

	public boolean isFileSupported(File file) {
		return FileUtil.isFileSupported(file, getSuffixes());
	}


}
