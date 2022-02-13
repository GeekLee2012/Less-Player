package xyz.less.graphic.view;

import java.util.function.Consumer;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import xyz.less.api.provider.Exporter;
import xyz.less.api.provider.GraphicApiProvider;
import xyz.less.api.provider.MediaPlayerApiProvider;
import xyz.less.bean.Audio;
import xyz.less.bean.Constants;
import xyz.less.bean.Resources.Images;
import xyz.less.graphic.Guis;
import xyz.less.graphic.control.DnmAction;
import xyz.less.graphic.control.ProgressBar;
import xyz.less.graphic.control.SliderBar;
import xyz.less.graphic.skin.MiniSkin;
import xyz.less.graphic.skin.SimpleSkin;
import xyz.less.graphic.visualization.ISpectrum;
import xyz.less.graphic.visualization.SpectrumManager;
import xyz.less.media.PlaybackQueue.PlayMode;
import xyz.less.util.StringUtil;

/**
 * 普通风格
 */
public final class MainView extends PlayerView {
	private Label mainTitleLbl;
	private BorderPane mainCenterPane;
	private Label coverArtLbl;
	private Label audioTitleLbl;
	private Label audioArtistLbl;
	private Label audioAlbumLbl;
//	private Label audioTimeLbl;
	private Label audioTimeCurrentLbl;
	private Label audioTimeDurationLbl;
	private ImageView defaultCoverArt;
	private ProgressBar progressBar;
	private ImageView lyricBtn;
	private ImageView spectrumBtn;
	private ImageView playBtn;
	private ImageView volumeBtn;
	private SliderBar volumeSlider;
	private ImageView playlistBtn;
	private ImageView repeatBtn;
	private ImageView shuffleBtn;
	
	private PlaylistView playlistView;
	private LyricView lyricView;
	
	private boolean alwaysOnTop;
	private PlayMode[] repeatModes = { PlayMode.NO_REPEAT, PlayMode.REPEAT_ALL, PlayMode.REPEAT_SELF };
	private PlayMode repeatMode = repeatModes[0];
	private boolean shuffleMode = true;
	private double currentMinutes;
	private boolean isBackMode = false; //是否为背面
	
	private Pane audioMetaBox;
	private ISpectrum spectrum;
	private boolean spectrumOn;
	private SpectrumManager spectrumMgr;
	private EventHandler<? super MouseEvent> prevSpHandler;
	private EventHandler<? super MouseEvent> nextSpHandler;
	
	public MainView(double width, double height) {
		super(width, height);
		setAlignment(Pos.CENTER);
		addChildren(Guis.loadFxml(SimpleSkin.MAIN_VIEW_FXML));
	}
	
	private void exportApis() {
		Consumer<Image> coverArtConsumer = (Image image) -> updateCoverArt(image, false);
		Exporter.exportObjectForField(GraphicApiProvider.class, "coverArtConsumer", coverArtConsumer);
		Exporter.exportObjectForField(MediaPlayerApiProvider.class, "volumeSlider", volumeSlider);
	}

	@Override
	protected void initStyles() {
		Guis.addStylesheet(SimpleSkin.MAIN_VIEW_STYLE, getMainStage());
	}
	
	@Override
	protected void doInitGraph() {
		initTop();
		initCenter();
		initBottom();
	}
	
	private void initTop() {
		AnchorPane pane = byId("main_top");
	    
		Label logoBtn = byId("logo_btn");
		mainTitleLbl = byId("main_title");
		Label miniSkinBtn = byId("mini_skin_btn");
		Label pinBtn = byId("pin_btn");
		Label minBtn = byId("min_btn");
		Label closeBtn = byId("close_btn");
		
		Pane logoTitleBox = byId("logo_title_box");
		Pane winBtnsBox = byId("win_btns_box");
		AnchorPane.setLeftAnchor(logoTitleBox, 6.0);
		AnchorPane.setRightAnchor(winBtnsBox, 2.0);
		
		Guis.setGraphic(Images.MIN_SKIN, miniSkinBtn);
		Guis.setGraphic(Images.PIN[0], pinBtn);
		Guis.setGraphic(Images.MIN, minBtn);
		Guis.setGraphic(Images.CLOSE, closeBtn);
		
		Guis.addStyleClass("bottom-border-dark", pane);
		Guis.addStyleClass("label-logo", logoBtn);
		Guis.addStyleClass("app-title", mainTitleLbl);
		Guis.addStyleClass("label-btn", miniSkinBtn, pinBtn, minBtn, closeBtn);
		
		Guis.setPickOnBounds(true, miniSkinBtn, pinBtn, minBtn, closeBtn);
		
//		Guis.addHoverStyleClass("label-logo-hover", logoBtn);
		Guis.addHoverStyleClass("label-hover", miniSkinBtn, pinBtn, minBtn);
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		
		Guis.bind(logoTitleBox.prefHeightProperty(), pane.prefHeightProperty());
		Guis.bind(winBtnsBox.prefHeightProperty(), pane.prefHeightProperty());
		
		Guis.addDnmAction(getMainStage(), pane, arg -> {
			Guis.applyStages(stage -> {
				if(stage instanceof Attachable) {
					((Attachable)stage).attach(arg.getX(), arg.getY());
				}
			}, playlistView, lyricView);
		}, logoBtn, winBtnsBox);

		logoBtn.setOnMouseClicked(e -> {
			isBackMode = !isBackMode;
			updateAppTitle();
			Guis.toggleStyleClass(isBackMode, "back-mode-bg", logoBtn);
			Guis.toggleStyleClass(isBackMode, "back-mode-text", mainTitleLbl);
		});
		
		miniSkinBtn.setOnMouseClicked(e -> {
			toggleLyricView(false);
			switchToSkin(MiniSkin.NAME);
		});
		
		pinBtn.setOnMouseClicked(e -> {
			alwaysOnTop = (Guis.toggleImage(pinBtn, Images.PIN) == 1);
			Guis.setAlwaysOnTop(alwaysOnTop, getMainStage(), playlistView);
		});
		
		minBtn.setOnMouseClicked(e -> Guis.minimize(true, getMainStage()));
		closeBtn.setOnMouseClicked(e -> {
			Guis.applyStages(s -> s.close(), getMainStage(), playlistView, lyricView);
			Guis.exitApplication();
		});
	}
	
	private void initCenter() {
		mainCenterPane = byId("main_center");
		
		coverArtLbl = byId("cover_art");
		audioTitleLbl = byId("audio_title");
		audioArtistLbl = byId("audio_artist");
		audioAlbumLbl = byId("audio_album");
		audioMetaBox = byId("audio_metas");
		
		Guis.addStyleClass("cover-art", coverArtLbl);
		Guis.addStyleClass("audio-title", audioTitleLbl);
		Guis.addStyleClass("audio-artist", audioArtistLbl);
		Guis.addStyleClass("audio-album", audioAlbumLbl);
		
		fixLablesWidth();
		
		mainCenterPane.centerProperty().addListener((o, ov, nv) -> {
			if(nv instanceof ISpectrum) {
				nv.setOnMouseClicked(nextSpHandler);
			}
		});
	}
	
	//Fix Bugs
	private void fixLablesWidth() {
		double width = getMainStage().getWidth();
		audioTitleLbl.setPrefWidth(width);
//		audioArtistLbl.setPrefWidth(width);
//		audioAlbumLbl.setPrefWidth(width);
	}

	private void initBottom() {
		BorderPane pane = byId("main_bottom");
		
		progressBar = byId("progress_bar");
//		audioTimeLbl = byId("audio_time");
		audioTimeCurrentLbl = byId("audio_time_current");
		audioTimeDurationLbl = byId("audio_time_duration");
		lyricBtn = byId("lyric_btn");
		spectrumBtn = byId("spectrum_btn");
		
		repeatBtn = byId("repeat_btn");
		ImageView playPrevBtn = byId("play_prev_btn");
		playBtn = byId("play_btn");
		ImageView playNextBtn = byId("play_next_btn");
		shuffleBtn = byId("shuffle_btn");
		
		playlistBtn = byId("playlist_btn");
		Pane volumebox = byId("volume_box");
		volumeBtn = byId("volume_btn");
		volumeSlider = byId("volume_bar");
		volumeSlider.setPrefSize(90, 5);
		volumeSlider.setThumbVisible(false);
		
		double lrWidth = 200; //188
		Guis.applyNodes(node -> ((Pane)node).setPrefWidth(lrWidth),
				pane.getLeft(), pane.getRight());
		
		//TODO
		lyricBtn.setImage(Images.LYRIC[0]);
		spectrumBtn.setImage(Images.SPECTRUM[0]);
		repeatBtn.setImage(Images.REPEAT[0]);
		playPrevBtn.setImage(Images.PLAY_PREV);
		playBtn.setImage(Images.PLAY[0]);
		playNextBtn.setImage(Images.PLAY_NEXT);
		shuffleBtn.setImage(Images.SHUFFLE[1]);
		volumeBtn.setImage(Images.VOLUME[0]);
		playlistBtn.setImage(Images.PLAYLIST[0]);
		
		Guis.setUserData(1, shuffleBtn);
		Guis.applyChildrenDeeply(node -> {
				if(node instanceof ImageView) {
					ImageView btn = (ImageView)node;
					Guis.addStyleClass("image-btn", btn);
					Guis.setPickOnBounds(true, btn);
					Guis.setFitSize(SimpleSkin.PLAYER_ICON_FIT_SIZE, btn);
				}
			}, pane);
		Guis.removeStyleClass("image-btn", volumeBtn);
		
		lyricBtn.setOnMouseClicked(e -> {
			Guis.toggleImage(lyricBtn, Images.LYRIC);
			lyricView.toggle();
		});
		
		spectrumBtn.setOnMouseClicked(e -> {
			Guis.ifPresent(getCurrent(), t -> {
				spectrumOn = !spectrumOn;
				toggleSpectrumView();
			});
		});
		
		repeatBtn.setOnMouseClicked(e -> {
			int index = Guis.toggleImage(repeatBtn, Images.REPEAT);
			repeatMode = repeatModes[index];
			if(!shuffleMode) {
				getMediaService().setPlayMode(repeatMode);
			}
		});
		
		playPrevBtn.setOnMouseClicked(e -> getMediaService().playPrev());
		playBtn.setOnMouseClicked(e -> togglePlay());
		playNextBtn.setOnMouseClicked(e -> getMediaService().playNext());
		
		shuffleBtn.setOnMouseClicked(e -> {
			int index = Guis.toggleImage(shuffleBtn, Images.SHUFFLE);
			shuffleMode = (index == 1);
			PlayMode playMode = shuffleMode? PlayMode.SHUFFLE : repeatMode;
			getMediaService().setPlayMode(playMode);
		});
		
		playlistBtn.setOnMouseClicked(e -> playlistView.toggle());
		
		progressBar.addListener((o, ov, nv) -> 
			getMediaService().seek(nv.doubleValue()) );
		
		Guis.addHoverAction(
				node -> volumeSlider.setThumbVisible(true),
				node -> volumeSlider.setThumbVisible(false), 
				volumebox);
		
		volumeBtn.setOnScroll(e -> volumeSlider.scroll(e));
		
		volumeSlider.addListener((o, ov, nv) -> {
			double value = nv.doubleValue();
			updateVolumeBtn(value);
			getMediaService().setVolume(value);
		});
	}

	@Override
	protected void initDatas() {
		setAppTitle(Constants.APP_TITLE);
		addIcons(Images.LOGO);
		
		volumeSlider.setValue(getMediaService().getVolume());
		updateVolumeBtn(volumeSlider.getValue());
		updatePlayModeBtn();
		
		initLyricView();
		initPlaylistView();
		initSpectrumView();
		updateProgress(-1, -1);
		
		Guis.setAlwaysOnTop(alwaysOnTop, getMainStage(), playlistView);
		initHelpText();
		
		exportApis();
	}
	
	private void updatePlayModeBtn() {
		PlayMode playMode = getMediaService().getPlayMode();
		shuffleMode = (playMode == PlayMode.SHUFFLE);
		int repeatIndex = shuffleMode ? 0 : playMode.ordinal();
		Guis.setImage(shuffleBtn, Images.SHUFFLE, shuffleMode);
		Guis.setImage(repeatBtn, Images.REPEAT, repeatIndex);
		
		Guis.setUserData(shuffleMode ? 1 : 0, shuffleBtn);
		Guis.setUserData(repeatIndex, repeatBtn);
	}

	//TODO 拖拽
	@Override
	protected void onDndWaiting() {
		doUpdateMetadata(null, false,
				"正在努力加载，请耐心等待~",
				"精彩即将开始", 
				"拖拽文件");
	}
	
	@Override
	protected void onDndAudioFileDone() {
		doUpdateMetadata(null, false, 
				"加载完成，正在努力识别~",
				"精彩即将开始", 
				"拖拽文件");
	}
	
	@Override
	protected void onDndFailed(String url) {
		doUpdateOnDndFailed();
//		if(!isBackMode) {
//			doUpdateOnDndFailed();
//		} else {
//			try {
//				Desktop desktop = Desktop.getDesktop();
//				desktop.browse(URI.create(url));
//			} catch (IOException e) {
//				e.printStackTrace();
//				doUpdateOnDndFailed();
//			}
//		}
	}
	
	@Override
	protected void onDndSuccess(Image image) {
		updateCoverArt(image, false);
	}
	
	@Override
	protected boolean onDndLyricDone(String url) {
		return loadLyric(url);
	}
	
	private void doUpdateOnDndFailed() {
		resetPlaylistView();
		getMediaService().removeAll(true);
		updateDndFailedText();
		spectrumOn = false;
		toggleSpectrumView();
	}

	private void initHelpText() {
		doUpdateMetadata(null,
				"试一试拖拽东西到播放器吧~",
				"类型: 文件、文件夹、其他", 
				"提示",
				null);
	}

	private void updateDndFailedText() {
		doUpdateMetadata(Images.DND_NOT_FOUND,
				"暂时无法识别哦\r试一试拖拽其他吧~",
				"神秘代号: 404", 
				"离奇事件", 
				null);
	}
	
	private void updateNoMediaText() {
		doUpdateMetadata(null,
				"暂时无法播放哦\r试一试拖拽其他吧~",
				"神秘代号: 500", 
				"离奇事件", 
				null);
	}
	
	private void doUpdateMetadata(Image cover, boolean applyTheme, 
			String title, String artist, String album) {
		updateCoverArt(cover, applyTheme);
		album = album.startsWith("<") ? album : "<" + album;
		album = album.endsWith(">") ? album : album + ">";
		
		audioTitleLbl.setText(title);
		audioArtistLbl.setText(artist);
		audioAlbumLbl.setText(album);
	}

	//TODO
	private ImageView getDefaultCoverArt() {
		Guis.ifNotPresent(defaultCoverArt, t -> {
			defaultCoverArt = new ImageView(Images.DEFAULT_COVER_ART);
			defaultCoverArt.setFitWidth(SimpleSkin.COVER_ART_FIT_SIZE);
			defaultCoverArt.setFitHeight(SimpleSkin.COVER_ART_FIT_SIZE);
		});
		return defaultCoverArt;
	}
	
	private void initLyricView() {
		Guis.ifNotPresent(lyricView, t -> {
			lyricView = new LyricView(getMainStage(), SimpleSkin.LYRIC_WIDTH, SimpleSkin.LYRIC_HEIGHT);
			lyricView.setOnHidden(e -> {
				updateLyricBtn();
				playlistView.attach(false);
			});
			lyricView.setOnShown(e -> {
				updateLyricBtn();
				updateLyricView(currentMinutes);
				playlistView.attach(true);
			});
		});
	}

	private void updateLyricView(double current) {
		this.currentMinutes = (current > 0 ? current : 0);
		Guis.ifPresent(lyricView != null && lyricView.isShowing(), 
				t -> lyricView.updateGraph(current));
		if(current < 0) {
			Guis.ifPresent(lyricView, t -> lyricView.hide());
		}
	}
	
	private void initSpectrumView() {
		Guis.ifNotPresent(spectrumMgr, t -> {
			spectrumMgr = new SpectrumManager();
		});
		Guis.ifNotPresent(spectrum, t -> {
			spectrum = spectrumMgr.next();
		});
		
		Guis.ifNotPresent(prevSpHandler, t -> {
			prevSpHandler = (MouseEvent e) -> {
				if(e.getClickCount() > 1 && spectrumMgr != null) {
					spectrum = spectrumMgr.prev();
					mainCenterPane.setCenter(spectrum.toNode());
				}
			};
		});
		
		Guis.ifNotPresent(nextSpHandler, t -> {
			nextSpHandler = (MouseEvent e) -> {
				if(e.getClickCount() > 1 && spectrumMgr != null) {
					spectrum = spectrumMgr.next();
					mainCenterPane.setCenter(spectrum.toNode());
				}
			};
		});
		
	}
	
	private void toggleSpectrumView() {
		mainCenterPane.setCenter(spectrumOn ? spectrum.toNode() : audioMetaBox);
		mainCenterPane.getLeft().setOnMouseClicked(spectrumOn ? prevSpHandler : null);
		updateSpectrumBtn();
		updateAppTitle();
	}

	private String getAdjustAppTitle() {
		String result = audioTitleLbl.getText();
		if(isBackMode) {
			return spectrumOn ? Constants.BACK_MODE_PREFIX
					+ " " + Constants.PLAYING_PREFIX + result 
					: Constants.APP_TITLE_BACK_MODE;
		} 
		return spectrumOn ? Constants.PLAYING_PREFIX + result 
				: Constants.APP_TITLE;
	}

	private void initPlaylistView() {
		if(playlistView == null) {
			playlistView = new PlaylistView(getMainStage(), SimpleSkin.PLAYLIST_WIDTH, SimpleSkin.PLAYLIST_HEIGHT);
		}
		Guis.ifPresent(playlistView, t -> {
//			playlistView.setTopVisible(true);
			playlistView.setRowWidth(SimpleSkin.PLAYLIST_ROW_WIDTH);
//			playlistView.setWidth(SimpleSkin.PLAYLIST_WIDTH);
//			playlistView.setHeight(SimpleSkin.PLAYLIST_HEIGHT);
			
			playlistView.setOnHidden(e -> {
				updatePlaylistBtn();
				playlistView.attach();
			});
			playlistView.setOnShown(e -> {
				updatePlaylistBtn();
				playlistView.attach();
			});
		});
	}

	@Override
	protected void updatePlaylist() {
		Guis.ifPresent(playlistView, 
				t-> playlistView.updateGraph());
	}
	
	private void resetPlaylistView() {
		Guis.ifPresent(playlistView, 
				t -> playlistView.resetGraph(true));
	}

	public void updateLyricBtn() {
		Guis.setImage(lyricBtn, Images.LYRIC, lyricView.isShowing());
	}
	
	private void updateSpectrumBtn() {
		Guis.setImage(spectrumBtn, Images.SPECTRUM, spectrumOn);
	}
	
	private void updateAppTitle() {
		setAppTitle(getAdjustAppTitle());
	}
	
	private void updatePlaylistBtn() {
		Guis.setImage(playlistBtn, Images.PLAYLIST, playlistView.isShowing());
	}
	
	public void updateProgressBar(double current, double duration) {
		progressBar.setSeekable(getMediaService().isSeekable());
		double percent = duration > 0 ? (current/duration) : 0;
		progressBar.updateProgress(percent);
	}
	
	public void updateAudioTimeText(double current, double duration) {
		current = current > 0 ? current : 0; 
		duration = duration > 0 ? duration : 0;
		current = current > duration ? duration : current;
		audioTimeCurrentLbl.setText(StringUtil.toMmss(current));
		audioTimeDurationLbl.setText(StringUtil.toMmss(duration));
//		audioTimeLbl.setText(String.format(Constants.CURRENT_DURATION_FORMAT,
//				StringUtil.toMmss(current),
//				StringUtil.toMmss(duration)));
	}
	
	@Override
	protected void doUpdateOnNoMetadata() {
		audioTitleLbl.setText(Constants.UNKOWN_AUDIO);
		audioArtistLbl.setText(Constants.UNKOWN_ARTIST);
		audioAlbumLbl.setText(Constants.UNKOWN_ALBUM);
		updateCoverArt(null);
		updateAppTitle();
	}
	
	@Override
	protected void doUpdateMetadata(Image image, String title, String artist, String album, String extra) {
		updateCoverArt(image);
		
		album = StringUtil.isBlank(extra)  ? album : extra;
		album = album.startsWith("<") ? album : "<" + album;
		album = album.endsWith(">") ? album : album + ">";
		
		audioTitleLbl.setText(title);
		audioArtistLbl.setText(artist);
		audioAlbumLbl.setText(album);
		updateAppTitle();
	}
	
	public void updateCoverArt(Image image) {
		ImageView graphic = null;
		double bordersWidth = SimpleSkin.COVER_ART_BORDERS_WIDTH;
		//TODO
		Guis.toggleStyleClass(image == Images.DND_NOT_FOUND, "theme-fg", coverArtLbl);
		if(image != null && !image.isError()) {
			graphic = new ImageView(image);
			graphic.setFitWidth(getCoverArtWidth() - bordersWidth);
			graphic.setFitHeight(getCoverArtHeight() - bordersWidth);
		} else {
			graphic = getDefaultCoverArt();
		}
		Guis.setGraphic(graphic, coverArtLbl);
	}
	
	//TODO
	public void updateCoverArt(Image image, boolean applyTheme) {
		ImageView graphic = null;
		double bordersWidth = SimpleSkin.COVER_ART_BORDERS_WIDTH;
		Guis.toggleStyleClass(applyTheme, "theme-fg", coverArtLbl);
		if(image != null && !image.isError()) {
			graphic = new ImageView(image);
			graphic.setFitWidth(getCoverArtWidth() - bordersWidth);
			graphic.setFitHeight(getCoverArtHeight() - bordersWidth);
		} else {
			graphic = getDefaultCoverArt();
		}
		Guis.setGraphic(graphic, coverArtLbl);
	}
	
	private double getCoverArtWidth() {
		double width = Math.max(coverArtLbl.getWidth(), coverArtLbl.getPrefWidth());
		return Math.max(width, 222);
	}
	
	private double getCoverArtHeight() {
		double height = Math.max(coverArtLbl.getHeight(), coverArtLbl.getPrefHeight());
		return Math.max(height, 222);
	}
	
	public void updatePlayBtn(boolean playing) {
		Guis.setUserData(Guis.setImage(playBtn, Images.PLAY, playing), 
				playBtn);
	}
	
	private void updateVolumeBtn(double value) {
		double lowLimit = volumeSlider.getHalf();
		int index = value > 0 ? (value >= lowLimit ? 0 : 1) : 2;
		Guis.setUserData(Guis.setImage(volumeBtn, Images.VOLUME, index), 
				volumeBtn);
	}

	@Override
	public void setAppTitle(String title) {
		setAppTitle();
		mainTitleLbl.setText(title);
	}
	
	@Override
	public void highlightPlaylist() {
		Guis.ifPresent(playlistView != null 
				&& playlistView.isShowing(), 
				t-> playlistView.highlightCurrentPlaying());
	}
	
	@Override
	protected void updateOnPlaying(boolean playing) {
		updatePlayBtn(playing);
	}

	@Override
	protected void updateOnReady(Audio audio) {
		loadLyric(audio);
	}
	
	@Override
	public void onNoPlayableMedia() {
		updateNoMediaText();
	}
	
	private void toggleLyricView(boolean visible) {
		Guis.ifPresent(lyricView, t -> {
				if(visible) {
					lyricView.show();
				} else {
					lyricView.hide();
				}
			});
	}
	
	private void loadLyric(Audio audio) {
		Guis.ifPresent(lyricView, 
				t -> lyricView.loadLyric(audio));
	}
	
	private boolean loadLyric(String uri) {
		return lyricView == null ? false : 
			lyricView.loadLyric(uri) ;
	}

	@Override
	public void updateProgress(double current, double duration) {
		updateProgressBar(current, duration);
		updateAudioTimeText(current, duration);
		updateLyricView(current);
	}
	
	@Override
	public void onSpectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		Guis.ifPresent(spectrumOn, 
				t -> spectrum.updateGraph(timestamp, duration, 
						magnitudes, phases));
	}
	
	@Override
	protected void onRestore() {
		Guis.ifNotPresent(getCurrent(), e -> initHelpText());
		Guis.ifPresent(spectrumOn, e -> toggleSpectrumView());
	}
	//TODO Bug: 打包成exe文件执行时，
	//从最小化状态中还原为正常显示状态时，
	//原本已打开的当前播放列表未能被正常显示
}
