package xyz.less.graphic.view;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import xyz.less.async.AsyncServices;
import xyz.less.bean.AppContext;
import xyz.less.bean.Audio;
import xyz.less.bean.Constants;
import xyz.less.graphic.Guis;
import xyz.less.graphic.handler.DefaultDndHandle;
import xyz.less.graphic.handler.IDndHandle;
import xyz.less.graphic.handler.IDndHandleAction;
import xyz.less.graphic.skin.PlayerSkin;
import xyz.less.graphic.skin.Skin;
import xyz.less.media.IMediaPlayerListener;
import xyz.less.media.Metadatas;
import xyz.less.service.IMediaService;
import xyz.less.service.PluginsService;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public abstract class PlayerView extends StackPane implements IMediaPlayerListener {
	private PlayerSkin skin;
	private DndUnsafe dndUnsafe = new DndUnsafe();
	
	public PlayerView(double width, double height) {
		setWidth(width);
		setHeight(height);
		setMediaService();
	}
	
	public AppContext getAppContext() {
		return AppContext.get();
	}
	
	public Stage getMainStage() {
		return getAppContext().getMainStage();
	}
	
	public IMediaService getMediaService() {
		return getAppContext().getMediaService();
	}
	
	public Skin switchToSkin(String skinName) {
		return getAppContext().switchToSkin(skinName);
	}
	
	public void setMediaService() {
		getMediaService().addListener(this);
		addHiddenChildren(getMediaService().getMediaView());
	}
	
	public Audio getCurrent() {
		return getMediaService().getCurrent();
	}
	
	public Map<String, Object> getCurrentMetadata() {
		return getMediaService().getCurrentMetadata();
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
	
	protected boolean isEnableCoverAperture() {
		return AppContext.get().isEnableCoverAperture();
	}
	
	/**
	 * ???????????????Override
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
		Audio audio = getCurrent();
		if(audio != null && audio.getDuration() <= 0 && duration < 100) {
			audio.setDuration(duration);
		}
	}

	private double getAdjustDuration(double duration) {
		Audio audio = getCurrent();
		if(audio != null && audio.getDuration() > 0) {
			duration = duration < 100 ? duration : audio.getDuration();
		}
		return  duration;
	}
	
	/**
	 * ???????????????Override
	 */
	public void initGraph() {
		initStyles();
		doInitGraph();
		initDatas();
		initEvents();
	}

	protected void initStyles() {
		//???????????????
	}
	
	//???????????????
	protected void initEvents() {
		//????????????
		enableHotKeys();
		//??????
		enableDndAction();
	}
	
	protected void initDatas() {
		//???????????????
	}
	
	protected abstract void updateProgress(double currentMinutes, double durationMinutes);
	
	protected abstract void updateOnPlaying(boolean playing);
	
	protected abstract void updateOnReady(Audio audio);

	protected abstract void doInitGraph();
	
	/* ??????????????? */
	public void setAppTitle(String title) {
		getMainStage().setTitle(title);
	}
	
	public void setAppTitle() {
		getMainStage().setTitle(Constants.APP_NAME);
	}
	
	public void restore() {
		initDatas();
		restoreMediaPlayer();
		updatePlaylist();
		onRestore();
	}
	
	protected boolean isPlaying() {
		return getMediaService().isPlaying();
	}
	
	protected void restoreMediaPlayer() {
		updateOnPlaying(isPlaying());
		onReady(getCurrent(), getCurrentMetadata());
	}
	
	protected void onRestore() {
		//???????????????
	}
	
	protected void updatePlaylist() {
		//???????????????
	}
	
	protected void highlightPlaylist() {
		//???????????????
	}

	@Override
	public void onInit(Audio audio) {

	}

	/**
	 * @param audio ?????????????????????????????????????????????
	 * @param metadata ??????????????????????????????
	 */
	@Override
	public void onReady(Audio audio, Map<String, Object> metadata) {
		updateMetadata(audio, metadata);
		updateOnReady(audio);
	}
	
	/** ???????????????Override */
	protected void updateMetadata(Audio audio, Map<String, Object> metadata) {
		if(audio == null || metadata == null) {
			doUpdateOnNoMetadata();
			return ;
		}
		Image image = Metadatas.getCoverArtImage(metadata);
		String title = Metadatas.getTitle(metadata);
		String artist = Metadatas.getArtist(metadata);
		String album = Metadatas.getAlbum(metadata);
		String extra = Metadatas.getExtra(metadata);
		
//		System.out.println(audio.getTitle() + " : " + title);
//		System.out.println(audio.getArtist() + " : " + artist);
//		System.out.println(audio.getAlbum() + " : " + album);
//		System.out.println("");
		
		//TODO ??????????????????????????????
		image = image != null ? image : audio.getCoverArtImage();
		title = StringUtil.getDefault(title, 
				StringUtil.getDefault(audio.getTitle(), Constants.UNKOWN_AUDIO));
		artist = StringUtil.getDefault(artist, 
				StringUtil.getDefault(audio.getArtist(), Constants.UNKOWN_ARTIST));
		album = !StringUtil.isBlank(album)  ? album : audio.getAlbum();
		album = StringUtil.getDefault(album, Constants.UNKOWN_ALBUM);
		
		doUpdateMetadata(image, title, artist, album, extra);
	}

	protected abstract void doUpdateOnNoMetadata();
	
	protected abstract void doUpdateMetadata(Image image, String title, String artist, String album, String extra);


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
		updateProgress(currentMinutes, getAdjustDuration(durationMinutes));
	}
	
	@Override
	public void onSpectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		//???????????????
	}
	
	@Override
	public void onNoMedia() {
		updateProgress(-1, -1);
		updateOnPlaying(false);
		updatePlaylist();
	}
	
	@Override
	public void onEndOfMedia() {
		//???????????????
	}
	
	@Override
	public void onReset() {
		//???????????????
	}
	
	@Override
	public void onError(Exception ex) {
		//???????????????
	}
	
	@Override
	public void onNoPlayableMedia() {
		//???????????????
	}

	@Override
	public void onPlaylistUpdated() {
		updatePlaylist();
	}

	public void togglePlay() {
		if(isPlaying()) {
			getMediaService().pause();
		} else {
			getMediaService().play();
		}
	}

	public PlayerSkin getSkin() {
		return skin;
	}

	public void setSkin(PlayerSkin skin) {
		this.skin = skin;
	}

	/** ????????????<br>
	 * ?????????: ??????/???????????? 
	 */
	public void enableHotKeys() {
		//TODO
		getMainStage().addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			//?????????: ??????/????????????
			if(KeyCode.SPACE == e.getCode()) {
				togglePlay();
			}
		});
	}
	
	public void enableDndAction() {
		dndUnsafe.enable(this);
	}
	
	public PlayerView addHandleAction(IDndHandleAction action) {
		dndUnsafe.getDndHandle().addHandleAction(action);
		return this;
	}
	
	public void handleDndAudioFile(File file) {
		handleDndAudioFile(file);
	}
	
	//TODO ???????????????
	private class DndUnsafe {
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
				if(ctx.isImage()) { //??????
					onDndDone(ctx.getImage());
				}
			}).addHandleAction(ctx -> {
				if(ctx.isLyric()) { //??????
					String url = StringUtil.transformUri(ctx.getUrl());
					ctx.setSuccess(onDndLyricDone(url));
				}
			}).addHandleAction(ctx -> { //???????????????
				if(ctx.isAudio() || ctx.isDirectory()) {
					handleDndAudioFile(ctx.getFile());
				}
			}).addHandleAction(ctx -> { 
				if(ctx.isJar()) { //Jar???
					File file = ctx.getFile();
					onDndJarDone(file);
				}
			}).addHandleAction(ctx -> { 
				if(ctx.isFile()) { //????????????
					onDndDone(ctx.getFile());
				}
			}).addHandleAction(ctx -> { 
				if(ctx.isLink()) { //?????????
					onDndLinkDone(ctx.getUrl());
				}
			});
		}
		
		private void handleDndAudioFile(File dndFile) {
			onDndWaiting();
			AsyncServices.cancel(loadFuture, updateFuture);
			loadFuture = getMediaService().loadFrom(FileUtil.toExternalForm(dndFile ));
			AsyncServices.submitFxTaskOnFutureDone(loadFuture, () ->{
				onDndAudioFileDone();
				if(getMediaService().getPlaylist().isEmpty()) {
					dndHandle.getContext().setSuccess(false);
					return ;
				}
				getMediaService().getPlaylist().sort((o1, o2)-> o1.compareTo(o2));
				getMediaService().play();
				updatePlaylist();
				updateFuture = getMediaService().syncMetadatas();
				AsyncServices.submitFxTaskOnFutureDone(updateFuture, () -> updatePlaylist());
				onDndAudioFileSuccess();
			}, null, () -> dndHandle.getContext().setSuccess(false));
		}
	}
	
	/****** DnD???????????? ******/
	/** Waiting -> Done -> Failed/Success */
	protected void onDndWaiting() {
		//???????????????
	}
	
	protected void onDndAudioFileDone() {
		//???????????????
	}
	
	protected boolean onDndLyricDone(String url) {
		//???????????????
		return false;
	}
	
	protected void onDndFailed(String url) {
		//???????????????
	}

	protected void onDndAudioFileSuccess() {
		//???????????????
	}
	
	protected void onDndSuccess(Image image) {
		//???????????????
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
