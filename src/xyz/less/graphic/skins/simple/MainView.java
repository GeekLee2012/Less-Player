package xyz.less.graphic.skins.simple;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import xyz.less.async.AsyncServices;
import xyz.less.bean.Audio;
import xyz.less.bean.ConfigConstant;
import xyz.less.bean.Resources.Fxmls;
import xyz.less.bean.Resources.Images;
import xyz.less.bean.Resources.Styles;
import xyz.less.graphic.Guis;
import xyz.less.graphic.PlayerView;
import xyz.less.graphic.action.DndAction.DndResult;
import xyz.less.graphic.action.DndAction.DndType;
import xyz.less.graphic.control.ProgressBar;
import xyz.less.graphic.control.SliderBar;
import xyz.less.graphic.visualization.RectangleSpectrum;
import xyz.less.graphic.visualization.Spectrum;
import xyz.less.media.Metadatas;
import xyz.less.media.PlaybackQueue.PlayMode;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public final class MainView extends PlayerView {
	private Label mainTitleLbl;
	private Label timeLbl;
	private Label coverArtLbl;
	private Label titleLbl;
	private Label artistLbl;
	private Label albumLbl;
	private ImageView defaultCoverArt;
	private ProgressBar progressBar;
	private ImageView lyricBtn;
	private ImageView spectrumBtn;
	private ImageView playBtn;
	private ImageView volumeBtn;
	private SliderBar volumeSlider;
	private ImageView playlistBtn;
	private LyricView lyricView;
	private PlaylistView playlistView;
	
	private boolean alwaysOnTop;
	private PlayMode[] repeatModes = { PlayMode.NO_REPEAT, PlayMode.REPEAT_ALL, PlayMode.REPEAT_SELF };
	private PlayMode repeatMode = repeatModes[0];
	private boolean devMode = false;
	private DndResult<?> dndResult;
	private double currentMinutes;
	
	private Pane audioMetaBox;
	private Spectrum spectrum;
	private boolean spectrumOn;
	
	public MainView(Stage stage, double width, double height) {
		super(stage, width, height);
		setAlignment(Pos.CENTER);
		addChildren(Guis.loadFxml(Fxmls.MAIN_VIEW));
	}
	
	public void initGraph() {
		initStyles();
		
		initTop();
		initCenter();
		initBottom();
		
		initEvents();
		initGraphDatas();
	}
	
	private void initStyles() {
		Guis.addStylesheet(Styles.MAIN_VIEW, mainStage);
	}

	private void initTop() {
		AnchorPane pane = byId("main_top");
	    
		Label logoBtn = byId("logo_btn");
		mainTitleLbl = byId("main_title");
		Label pinBtn = byId("pin_btn");
		Label minBtn = byId("min_btn");
		Label closeBtn = byId("close_btn");
		
		Pane logoTitleBox = byId("logo_title_box");
		Pane winBtnsBox = byId("win_btns_box");
		AnchorPane.setLeftAnchor(logoTitleBox, 6.0);
		AnchorPane.setRightAnchor(winBtnsBox, 2.0);
		
		Guis.setGraphic(new ImageView(Images.PIN[0]), pinBtn);
		Guis.setGraphic(new ImageView(Images.MIN), minBtn);
		Guis.setGraphic(new ImageView(Images.CLOSE), closeBtn);
		
		Guis.addStyleClass("bottom-border-dark", pane);
		Guis.addStyleClass("label-logo", logoBtn);
		Guis.addStyleClass("app-title", mainTitleLbl);
		Guis.addStyleClass("label-btn", pinBtn, minBtn, closeBtn);
		
		Guis.setPickOnBounds(true, pinBtn, minBtn, closeBtn);
		
//		Guis.addHoverStyleClass("label-logo-hover", logoBtn);
		Guis.addHoverStyleClass("label-hover", pinBtn, minBtn);
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		
		Guis.applyChildrenPrefHeight(pane);
		
		Guis.addDnmAction(mainStage, pane, dnmOffset -> {
			Guis.applyStages(stage -> {
				if(stage == playlistView) {
					playlistView.attach();
				} else if(stage == lyricView) {
					lyricView.attach();
				}
			}, playlistView, lyricView);
		}, winBtnsBox);
		
		logoBtn.setOnMouseClicked(e -> {
			devMode = !devMode;
			updateAppTitle();
			if(devMode) {
				Guis.addStyleClass("dev-mode-bg", logoBtn);
				Guis.addStyleClass("dev-mode-text", mainTitleLbl);
			} else {
				Guis.removeStyleClass("dev-mode-bg", logoBtn);
				Guis.removeStyleClass("dev-mode-text", mainTitleLbl);
			}
		});
		
		pinBtn.setOnMouseClicked(e -> {
			alwaysOnTop = (Guis.toggleImage(pinBtn, Images.PIN) == 1);
			Guis.setAlwaysOnTop(alwaysOnTop, mainStage, playlistView, lyricView);
		});
		
		minBtn.setOnMouseClicked(e -> {
			Guis.minimize(true, mainStage);
		});
		
		closeBtn.setOnMouseClicked(e -> {
			Guis.exitApplication();
		});
	}
	
	private void initCenter() {
		coverArtLbl = byId("cover_art");
		titleLbl = byId("audio_title");
		artistLbl = byId("audio_artist");
		albumLbl = byId("audio_album");
		audioMetaBox = byId("audio_metas");
		
		Guis.addStyleClass("cover-art", coverArtLbl);
		Guis.addStyleClass("audio-title", titleLbl);
		Guis.addStyleClass("audio-artist", artistLbl);
		Guis.addStyleClass("audio-album", albumLbl);
	}
	
	private void initBottom() {
		BorderPane pane = byId("main_bottom");
		
		progressBar = byId("progress_bar");
		timeLbl = byId("audio_time");
		lyricBtn = byId("lyric_btn");
		
		spectrumBtn = byId("spectrum_btn");
		
		ImageView repeatBtn = byId("repeat_btn");
		ImageView playPrevBtn = byId("play_prev_btn");
		playBtn = byId("play_btn");
		ImageView playNextBtn = byId("play_next_btn");
		ImageView shuffleBtn = byId("shuffle_btn");
		
		playlistBtn = byId("playlist_btn");
		Pane volumebox = byId("volume_box");
		volumeBtn = byId("volume_btn");
		volumeSlider = byId("volume_bar");
		volumeSlider.setPrefSize(90, 5);
		volumeSlider.setThumbVisible(false);
		
		double ncnWidth = 188;
		((Pane)pane.getLeft()).setPrefWidth(ncnWidth);
		((Pane)pane.getRight()).setPrefWidth(ncnWidth);
		
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
					Guis.setFitSize(ConfigConstant.PLAYER_ICON_FIT_SIZE, btn);
				}
			}, pane);
		Guis.removeStyleClass("image-btn", volumeBtn);
		
		lyricBtn.setOnMouseClicked(e -> {
			Guis.toggleImage(lyricBtn, Images.LYRIC);
			lyricView.toggle();
		});
		
		spectrumBtn.setOnMouseClicked(e -> {
			if(getMediaPlayer().isInit()) {
				spectrumOn = !spectrumOn;
				toggleSpectrumView();
			}
		});
		
		repeatBtn.setOnMouseClicked(e -> {
			int index = Guis.toggleImage(repeatBtn, Images.REPEAT);
			repeatMode = repeatModes[index];
			getMediaPlayer().setPlayMode(repeatMode);
		});
		
		playPrevBtn.setOnMouseClicked(e -> {
			getMediaPlayer().playPrevious();
		});
		
		playBtn.setOnMouseClicked(e -> {
			togglePlay();
		});
		
		playNextBtn.setOnMouseClicked(e -> {
			getMediaPlayer().playNext();
		});
		
		shuffleBtn.setOnMouseClicked(e -> {
			int index = Guis.toggleImage(shuffleBtn, Images.SHUFFLE);
			if(index == 1) {
				getMediaPlayer().setPlayMode(PlayMode.SHUFFLE);
			} else {
				getMediaPlayer().setPlayMode(repeatMode);
			}
		});
		
		playlistBtn.setOnMouseClicked(e -> {
			playlistView.toggle();
		});
		
		progressBar.addListener((o,ov,nv) -> {
			getMediaPlayer().seek(nv.doubleValue());
		});
		
		Guis.addHoverAction(
				node -> volumeSlider.setThumbVisible(true),
				node -> volumeSlider.setThumbVisible(false), 
				volumebox);
		
		volumeBtn.setOnScroll(e -> {
			volumeSlider.scroll(e);
		});
		
		volumeSlider.addListener((o,ov,nv) -> {
			double value = nv.doubleValue();
			updateVolumeBtn(value);
			getMediaPlayer().setVolumn(value);
		});
	}
	
	private void initEvents() {
		mainStage.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> {
			//空格键: 播放/暂停音乐
			if(KeyCode.SPACE == event.getCode()) {
				togglePlay();
			}
		});
		
		//TODO
		Guis.addDndAction(this, result -> {
			dndResult = new DndResult<>();
			String url = result.getUrl();
			
			if(url.startsWith(ConfigConstant.FILE_PREFIX)) {
				dndResult.setDndType(DndType.FILE);
				handleDndFile(result);
			} else if(url.startsWith(ConfigConstant.HTTPS_PREFIX) 
					|| url.startsWith(ConfigConstant.HTTP_PREFIX)) {
				dndResult.setDndType(DndType.LINK);
				dndResult.setSuccess(true);
				handleDndLinkUrl(url);
			}
			handleDndFailed(url);
		});
	}

	protected void initGraphDatas() {
		setAppTitle(ConfigConstant.APP_TITLE_DEFAULT_MODE);
		addIcons(Images.LOGO);
		volumeSlider.setValue(ConfigConstant.INITIAL_VOLUME);
		getMediaPlayer().setPlayMode(PlayMode.SHUFFLE);
		updateMetadata(null, null);
		updateTimeText(0, 0);
		initHelpText();
		initLyricView();
		initPlaylistView();
		initSpectrumView();
	}

	private void handleDndFailed(String url) {
		if(!dndResult.isSuccess()) {
			if(!devMode) {
				updateOnDndFail();
			} else {
				//TODO
				try {
					Desktop desktop = Desktop.getDesktop();
					desktop.browse(URI.create(url));
				} catch (IOException e) {
//					e.printStackTrace();
					updateOnDndFail();
				}
			}
		} 
	}

	private void updateOnDndFail() {
		resetPlaylistView();
		getMediaPlayer().resetPlaybackQueue();
		updateDndFailText();
		//TODO
		spectrumOn = false;
		toggleSpectrumView();
	}
	
	//TODO
	private void handleDndFile(Dragboard result) {
		try {
			List<File> fileList = result.getFiles();
			File dndFile = fileList.get(0);
			if(FileUtil.isImage(dndFile)) { //图片
				dndResult.setDndType(DndType.IMAGE);
				updateCoverArt(new Image(result.getUrl()), false);
				dndResult.setSuccess(true);
			}else if(FileUtil.isLryic(dndFile)) { //歌词
				dndResult.setDndType(DndType.LYRIC);
				boolean success = loadLyric(result.getUrl());
				dndResult.setSuccess(success);
			} else if(FileUtil.isDirectory(dndFile)
					|| FileUtil.isAudio(dndFile)) { //目录或音频
				dndResult.setSuccess(true);
				updateDndWaiting();
				Future<?> future = getMediaPlayer().loadFrom(dndFile);
				AsyncServices.submitFxTaskOnFutureDone(future, () ->{
					updateDndDone();
					if(getMediaPlayer().isPlaylistEmpty()) {
						updateDndFailText();
						return ;
					}
					getMediaPlayer().play();
					updatePlaylistView();
					Future<?> updateFuture = getMediaPlayer().updateMetadatas();
					AsyncServices.submitFxTaskOnFutureDone(updateFuture,() -> {
						updatePlaylistViewTimeLbl();
					});
				}, () -> updateDndFailText());
			}
			//TODO 歌词
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateDndFailText() {
		updateCoverArt(Images.DND_NOT_FOUND, true);
		titleLbl.setText("暂时无法识别哦\r试一试拖拽其他吧~");
		artistLbl.setText("神秘代号: 404");
		albumLbl.setText("<离奇事件>");
	}
	
	private void updateDndWaiting() {
//		updateCoverArt(Images.DND_WAITING, true);
		titleLbl.setText("正在努力加载，请耐心等待~");
		artistLbl.setText("精彩即将开始");
		albumLbl.setText("<拖拽文件>");
	}
	
	private void updateDndDone() {
		titleLbl.setText("加载完成，正在努力识别~");
		artistLbl.setText("精彩即将开始");
		albumLbl.setText("<拖拽文件>");
	}
	
	private void updateNoMediaText() {
//		updateCoverArt(Images.DND_NOT_FOUND, true);
		titleLbl.setText("暂时无法播放哦\r试一试拖拽其他吧~");
		artistLbl.setText("神秘代号: 500");
		albumLbl.setText("<离奇事件>");
	}

	private boolean handleDndLinkUrl(String url) {
		//插件引擎实现
		System.out.println(url);
		getMediaPlayer().playUrl(url);
		updatePlaylistView();
		return true;
	}

	//TODO
	private ImageView getDefaultCoverArt() {
		if(defaultCoverArt == null) {
			defaultCoverArt = new ImageView(Images.DEFAULT_COVER_ART);
			defaultCoverArt.setFitWidth(ConfigConstant.COVER_ART_FIT_SIZE);
			defaultCoverArt.setFitHeight(ConfigConstant.COVER_ART_FIT_SIZE);
		}
		return defaultCoverArt;
	}
	
	private void initLyricView() {
		if(lyricView == null) {
			lyricView = new LyricView(mainStage);
			lyricView.setOnHidden(e -> {
				updateLyricBtn();
				playlistView.attach(false);
			});
			lyricView.setOnShown(e -> {
				updateLyricBtn();
				updateLyricView(currentMinutes);
				playlistView.attach(true);
			});
		}
	}

	private void updateLyricView(double current) {
		this.currentMinutes = current;
		if(lyricView != null && lyricView.isShowing()) {
			lyricView.updateGraph(current);
		}
	}
	
	private void initSpectrumView() {
		spectrum = new RectangleSpectrum(66);
//		spectrum = new GridSpectrum(32, 28);
		spectrum.setSpacing(1);
		spectrum.setAlignment(Pos.BOTTOM_CENTER);
	}
	
	private void toggleSpectrumView() {
		BorderPane mainCenterPane = byId("main_center");
		if(spectrumOn) {
			mainCenterPane.setCenter(spectrum);
		} else {
			mainCenterPane.setCenter(audioMetaBox);
		}
		updateSpectrumBtn();
		updateAppTitle();
	}

	private String getAdjustAppTitle() {
		String result = titleLbl.getText();
		if(devMode) {
			return spectrumOn ? ConfigConstant.DEV_MODE_PREFIX
					+ " " + ConfigConstant.PLAYING_PREFIX + result 
					: ConfigConstant.APP_TITLE_DEV_MODE;
		} 
		return spectrumOn ? ConfigConstant.PLAYING_PREFIX + result 
				: ConfigConstant.APP_TITLE_DEFAULT_MODE;
	}

	private void initPlaylistView() {
		if(playlistView == null) {
			playlistView = new PlaylistView(mainStage, getMediaPlayer());
			
			playlistView.setOnHidden(e -> {
				updatePlaylistBtn();
			});
			playlistView.setOnShown(e -> {
				updatePlaylistBtn();
			});
		}
	}

	//TODO
	private void updatePlaylistView() {
		if(playlistView != null) {
			playlistView.updateGraph();
		}
	}
	
	
	private void updatePlaylistViewTimeLbl() {
		if(playlistView != null) {
			playlistView.updateTimeLbl();
		}
	}
	
	private void resetPlaylistView() {
		if(playlistView != null) {
			playlistView.resetGraph(true);
		}
	}

	public void updateLyricBtn() {
		int index = lyricView.isShowing() ? 1 : 0;
		lyricBtn.setImage(Images.LYRIC[index]);
	}
	
	private void updateSpectrumBtn() {
		int index = spectrumOn ? 1 : 0;
		spectrumBtn.setImage(Images.SPECTRUM[index]);
	}
	
	private void updateAppTitle() {
		setAppTitle(getAdjustAppTitle());
	}
	
	private void updatePlaylistBtn() {
		int index = playlistView.isShowing() ? 1 : 0;
		playlistBtn.setImage(Images.PLAYLIST[index]);
	}
	
	public void updateProgressBar(double current, double duration) {
		progressBar.setSeekable(getMediaPlayer().isInit());
		progressBar.updateProgress(current/duration);
	}
	
	public void updateTimeText(double current, double duration) {
		timeLbl.setText(String.format(ConfigConstant.CURRENT_DURATION_FORMAT, 
				StringUtil.toMmss(current),
				StringUtil.toMmss(duration)));
	}
	
	public void updateMetadata(Audio audio, Map<String, Object> metadata) {
		if(audio == null || metadata == null) {
			titleLbl.setText(ConfigConstant.UNKOWN_AUDIO);
			artistLbl.setText(ConfigConstant.UNKOWN_ARTIST);
			albumLbl.setText(ConfigConstant.UNKOWN_ALBUM);
			updateCoverArt(null, false);
			updateAppTitle();
			return ;
		}
		String title = (String)metadata.get(Metadatas.TITLE);
		String artist = (String)metadata.get(Metadatas.ARTIST);
		String album = (String)metadata.get(Metadatas.ALBUM);
		Image image = (Image)metadata.get(Metadatas.COVER_ART);
		
		image = image != null ? image : audio.getCoverArt();
		title = !StringUtil.isBlank(title) ? title : audio.getTitle();
		artist = !StringUtil.isBlank(artist) ? artist : audio.getArtist();
		album = !StringUtil.isBlank(album)  ? album : audio.getAlbum();
		
//		System.out.println(String.format("title: %1$s, artist: %2$s, album: %3$s", title, artist, album));
		System.out.println("title: " + title + ", messy: " + StringUtil.isMessyCode(title));
		System.out.println("artist: " + artist + ", messy: " + StringUtil.isMessyCode(artist));
		System.out.println("album: " + album + ", messy: " + StringUtil.isMessyCode(album));
		
		String titleDefault = StringUtil.getDefault(title, ConfigConstant.UNKOWN_AUDIO);
		//TODO
		album = StringUtil.getDefault(album, ConfigConstant.UNKOWN_ALBUM);
		album = album.startsWith("<") ? album : "<" + album + ">";
		
		titleLbl.setText(StringUtil.getDefault(title, titleDefault));
		artistLbl.setText(StringUtil.getDefault(artist, ConfigConstant.UNKOWN_ARTIST));
		albumLbl.setText(album);
		
		updateCoverArt(image, false);
		updateAppTitle();
	}
	
	public void updateCoverArt(Image image, boolean applyTheme) {
		if(applyTheme) {
			Guis.addStyleClass("theme-fg", coverArtLbl);
		} else {
			Guis.removeStyleClass("theme-fg", coverArtLbl);
		}
		ImageView graphic = getDefaultCoverArt();
		if(image != null) {
			graphic = new ImageView(image);
			double bordersWidth = ConfigConstant.COVER_ART_BORDERS_WIDTH;
			graphic.setFitWidth(coverArtLbl.getWidth() - bordersWidth);
			graphic.setFitHeight(coverArtLbl.getHeight() - bordersWidth);
		}
		Guis.setGraphic(graphic, coverArtLbl);
	}
	
	public void updatePlayBtn(boolean playing) {
		int index = playing ? 1 : 0;
		Guis.setUserData(index, playBtn);
		playBtn.setImage(Images.PLAY[index]);
	}
	
	private void updateVolumeBtn(double value) {
		double lowLimit = volumeSlider.getHalf();
		int index = value > 0 ? (value >= lowLimit ? 0 : 1) : 2;
		Guis.setUserData(index, volumeBtn);
		volumeBtn.setImage(Images.VOLUME[index]);
	}

	@Override
	public void setAppTitle(String title) {
		mainTitleLbl.setText(title);
	}
	
	public void highlightPlaylist() {
		if(playlistView != null && playlistView.isShowing()) {
			playlistView.highlightCurrentPlaying();
		}
	}
	
	private void initHelpText() {
		titleLbl.setText("试一试拖拽东西到播放器吧~");
		artistLbl.setText("类型: 文件、文件夹、其他");
		albumLbl.setText("<HELP>");
	}

	@Override
	protected void updateOnPlaying(boolean playing) {
		updatePlayBtn(playing);
	}

	@Override
	protected void updateOnReady(Audio audio, Map<String, Object> metadata) {
		updateMetadata(audio, metadata);
		loadLyric(audio);
	}
	
	@Override
	public void onNoPlayableMedia() {
		updateNoMediaText();
	}
	
	private void loadLyric(Audio audio) {
		if(lyricView != null) {
			lyricView.loadLyric(audio);
		}
	}
	
	private boolean loadLyric(String uri) {
		if(lyricView != null) {
			return lyricView.loadLyric(uri);
		}
		return false;
	}

	@Override
	public void updateProgress(double current, double duration) {
		updateProgressBar(current, duration);
		updateTimeText(current, duration);
		updateLyricView(current);
	}
	
	@Override
	public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		if(spectrumOn) {
			spectrum.updateGraph(timestamp, duration, magnitudes, phases);
		}
	}
	
	//TODO a Bug: 打包成exe文件执行时，
	//从最小化状态中还原为正常显示状态时，
	//原本已打开的当前播放列表未能被正常显示
}
