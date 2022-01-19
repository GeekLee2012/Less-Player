package xyz.less.graphic.view;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import xyz.less.api.provider.IExportable;
import xyz.less.async.AsyncServices;
import xyz.less.bean.AppContext;
import xyz.less.bean.Audio;
import xyz.less.bean.Constants;
import xyz.less.graphic.Guis;
import xyz.less.graphic.action.DndAction.DndContext;
import xyz.less.graphic.handler.DefaultDndHandle;
import xyz.less.graphic.handler.IDndHandle;
import xyz.less.graphic.skin.PlayerSkin;
import xyz.less.graphic.skin.Skin;
import xyz.less.media.FxMediaPlayer;
import xyz.less.media.IMediaPlayerListener;
import xyz.less.service.PluginsService;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public abstract class PlayerView extends StackPane implements IMediaPlayerListener ,IExportable {
	private PlayerSkin skin;
	private DndUnsafe dndUnsafe = new DndUnsafe();
	
	public PlayerView(double width, double height) {
		setWidth(width);
		setHeight(height);
		setupMediaPlayer();
	}
	
	public AppContext getAppContext() {
		return AppContext.get();
	}
	
	public Stage getMainStage() {
		return getAppContext().getMainStage();
	}
	
	public FxMediaPlayer getMediaPlayer() {
		return getAppContext().getMediaPlayer();
	}
	
	public Skin switchToSkin(String skinName) {
		return getAppContext().switchToSkin(skinName);
	}
	
	public void setupMediaPlayer() {
		getMediaPlayer().addPlayerListener(this);
		addHiddenChildren(getMediaPlayer().getMediaView());
	}
	
	public Audio getCurrentAudio() {
		return getMediaPlayer().getCurrentAudio();
	}
	
	public boolean isNoMedia() {
		return getCurrentAudio() == null;
	}
	
	public Map<String, Object> getCurrentMetadata() {
		return getMediaPlayer().getCurrentMetadata();
	}
	
	public <T> T byId(String id) {
		return Guis.byId(id, getMainStage());
	}
	
	public <T> T bySelector(String selector) {
		return Guis.bySelector(selector, getMainStage());
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
		Guis.addIcons(getMainStage(), icons);
		return this;
	}
	
	protected boolean isEnableAnim() {
		return AppContext.get().isEnableAnim();
	}
	
	protected boolean hasArgsPlaylist() {
		return AppContext.get().hasArgsPlaylist();
	}
	
	protected String getArgsPlaylistUri() {
		return AppContext.get().getArgsPlaylistUri();
	}
	
	protected boolean isEnableAutoDrawer() {
		return AppContext.get().isEnableAutoDrawer();
	}
	
	protected boolean isEnableCoverAperture() {
		return AppContext.get().isEnableCoverAperture();
	}
	
	/**
	 * 一般不建议Override
	 */
	public void playFromArgs() {
		try {
			if(hasArgsPlaylist()) {
				File file = new File(getArgsPlaylistUri());
				if(FileUtil.exists(file)) {
					dndUnsafe.handleDndAudioFile(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateAudioDuration(double duration) {
		Audio audio = getCurrentAudio();
		if(audio != null && audio.getDuration() <= 0) {
			audio.setDuration(duration);
		}
	}
	
	/**
	 * 一般不建议Override
	 */
	public void initGraph() {
		initStyles();
		doInitGraph();
		initEvents();
		initDatas();
	}

	protected void initStyles() {
		//由子类重写
	}
	
	protected void initEvents() {
		//由子类重写
	}
	
	protected void initDatas() {
		//由子类重写
	}
	
	protected abstract void updateProgress(double currentMinutes, double durationMinutes);
	
	protected abstract void updateOnPlaying(boolean playing);
	
	protected abstract void updateOnReady(Audio audio, Map<String, Object> metadata);

	protected abstract void doInitGraph();
	
	/* 不要求实现 */
	public void setAppTitle(String title) {
		getMainStage().setTitle(title);
	}
	
	public void setAppTitle() {
		getMainStage().setTitle(Constants.APP_TITLE);
	}
	
	
	public void restore() {
		initDatas();
		restoreMediaPlayer();
		updatePlaylist();
		onRestore();
	}
	
	protected boolean isPlaying() {
		return !getMediaPlayer().isNotPlaying();
	}
	
	protected void restoreMediaPlayer() {
		updateOnPlaying(isPlaying());
		updateOnReady(getCurrentAudio(), getCurrentMetadata());
	}
	
	protected void onRestore() {
		//由子类重写
	}
	
	protected void updatePlaylist() {
		//由子类重写
	}
	
	protected void highlightPlaylist() {
		//由子类重写
	}
	
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
		updateAudioDuration(durationMinutes);
		updateProgress(currentMinutes, durationMinutes);
	}
	
	@Override
	public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		//由子类重写
	}
	
	@Override
	public void onNoMedia() {
		updateProgress(-1, -1);
		updateOnPlaying(false);
		updatePlaylist();
	}
	
	@Override
	public void onEndOfMedia() {
		//由子类重写
	}
	
	@Override
	public void onReset() {
		//由子类重写
	}
	
	@Override
	public void onError() {
		//由子类重写
	}
	
	@Override
	public void onNoPlayableMedia() {
		//由子类重写
	}

	public void togglePlay() {
		getMediaPlayer().play();
	}

	public PlayerSkin getSkin() {
		return skin;
	}

	public void setSkin(PlayerSkin skin) {
		this.skin = skin;
	}

	/** 播放热键<br>
	 * 空格键: 播放/暂停音乐 
	 */
	public void enableHotKeys() {
		getMainStage().addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			//空格键: 播放/暂停音乐
			if(KeyCode.SPACE == e.getCode()) {
				togglePlay();
			}
		});
	}
	
	public void enableDndAction() {
		dndUnsafe.enable(this);
	}
	
	public PlayerView addHandleAction(Consumer<DndContext> handle) {
		dndUnsafe.getDndHandle().addHandleAction(handle);
		return this;
	}
	
	public void handleDndAudioFile(File file) {
		handleDndAudioFile(file);
	}
	
	//TODO 拖拽实现类
	class DndUnsafe {
		private IDndHandle dndHandle;
		private Future<?> loadFuture;
		private Future<?> updateFuture;
		
		public void enable(PlayerView view) {
//			PluginEngine.reloadDndPlugins();
			Guis.addDndAction(view, ctx -> {
				getDndHandle().handle(ctx);
			});
		}
		
		private IDndHandle getDndHandle() {
			if(dndHandle == null) {
				dndHandle = new DefaultDndHandle();
				addDndHandleActions();
			}
			return dndHandle;
		}
		
		private void addDndHandleActions() {
			dndHandle.addHandleAction(ctx -> {
				ctx.successProperty().addListener((c, ov, nv)-> {
					if(!nv) {
						onDndFailed(ctx.getUrl());
					}
				});
			}).addHandleAction(ctx -> {
				if(ctx.isImage()) { //图片
					onDndDone((Image)ctx.getUserData());
				}
			}).addHandleAction(ctx -> {
				if(ctx.isLyric()) { //歌词
					String url = StringUtil.transformUri(ctx.getUrl());
					ctx.setSuccess(onDndLyricDone(url));
				}
			}).addHandleAction(ctx -> { //目录或音频
				if(ctx.isAudio() || ctx.isDirectory()) {
					handleDndAudioFile((File)ctx.getUserData());
				}
			}).addHandleAction(ctx -> { 
				if(ctx.isJar()) { //Jar包
					File file = (File)ctx.getUserData();
					onDndJarDone(file);
				}
			}).addHandleAction(ctx -> { 
				if(ctx.isFile()) { //其他文件
					onDndDone((File)ctx.getUserData());
				}
			}).addHandleAction(ctx -> { 
				if(ctx.isLink()) { //超链接
					onDndLinkDone(ctx.getUrl());
				}
			});
		}
		
		private void handleDndAudioFile(File dndFile) {
			onDndWaiting();
			AsyncServices.cancel(loadFuture, updateFuture);
			loadFuture = getMediaPlayer().loadFrom(dndFile);
			AsyncServices.submitFxTaskOnFutureDone(loadFuture, () ->{
				onDndAudioFileDone();
				if(getMediaPlayer().isPlaylistEmpty()) {
					dndHandle.getContext().setSuccess(false);
					return ;
				}
				getMediaPlayer().getPlaylist().sort();
				getMediaPlayer().play();
				updatePlaylist();
				updateFuture = getMediaPlayer().updateMetadatas();
				onDndAudioFileSuccess();
			}, null, () -> dndHandle.getContext().setSuccess(false));
		}
	}
	
	/****** DnD回调方法 ******/
	/** Waiting -> Done -> Failed/Success */
	protected void onDndWaiting() {
		//由子类重写
	}
	
	protected void onDndAudioFileDone() {
		//由子类重写
	}
	
	protected boolean onDndLyricDone(String url) {
		//由子类重写
		return false;
	}
	
	protected void onDndFailed(String url) {
		//由子类重写
	}

	protected void onDndAudioFileSuccess() {
		//由子类重写
	}
	
	protected void onDndSuccess(Image image) {
		//由子类重写
	}
	
	/****** DndPlugins ******/
	private void onDndDone(Image image) {
		onDndSuccess(image);
		PluginsService.getDndPlugin().onDndSuccess(image);
	}
	
	private void onDndDone(File file) {
		PluginsService.getDndPlugin().onDndDone(file);
	}
	
	private void onDndLinkDone(String url) {
		PluginsService.getDndPlugin().onDndLinkDone(url);
	}
	
	private void onDndJarDone(File file) {
		AsyncServices.submit(() -> PluginsService.loadJar(file));
	}
}
