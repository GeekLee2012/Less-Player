package xyz.less.graphic;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javafx.application.Platform;
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
import javafx.scene.media.Media;
import javafx.stage.Stage;
import xyz.less.async.AsyncServices;
import xyz.less.bean.ConfigConstant;
import xyz.less.bean.Resources.Fxmls;
import xyz.less.bean.Resources.Images;
import xyz.less.bean.Resources.Styles;
import xyz.less.graphic.action.DndAction.DndResult;
import xyz.less.graphic.action.DndAction.DndType;
import xyz.less.graphic.control.ProgressBar;
import xyz.less.graphic.control.SliderBar;
import xyz.less.graphic.visualization.RectangleSpectrum;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public class MainView extends PlayerView {
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
	private boolean shuffleMode = true;
	private boolean devMode = false;
	private DndResult dndResult;
	private double currentMinutes;
	
	private Pane audioMetaBox;
	private RectangleSpectrum rectSpectrum;
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
		volumeBtn = byId("volume_btn");
		volumeSlider = byId("volume_bar");
		volumeSlider.setPrefSize(90, 5);
		
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
		
		Guis.setUserData(shuffleMode ? 1 : 0, shuffleBtn);
		
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
			getMediaPlayer().setRepeatMode(
					Guis.toggleImage(repeatBtn, Images.REPEAT));
		});
		
		playPrevBtn.setOnMouseClicked(e -> {
			getMediaPlayer().playPrevious();
		});
		
		playBtn.setOnMouseClicked(e -> {
			getMediaPlayer().play();
		});
		
		playNextBtn.setOnMouseClicked(e -> {
			getMediaPlayer().playNext();
		});
		
		shuffleBtn.setOnMouseClicked(e -> {
			shuffleMode = !shuffleMode;
			Guis.toggleImage(shuffleBtn, Images.SHUFFLE);
			getMediaPlayer().setShuffleMode(shuffleMode);
		});
		
		playlistBtn.setOnMouseClicked(e -> {
			playlistView.toggle();
		});
		
		progressBar.addListener((o,ov,nv) -> {
			getMediaPlayer().seek(nv.doubleValue());
		});
		
		volumeSlider.addListener((o,ov,nv) -> {
			double value = nv.doubleValue();
			updateVolumeBtn(value);
			getMediaPlayer().setVolumn(value);
		});
	}
	
	private void initEvents() {
		//TODO 按键监听失效
		addEventHandler(KeyEvent.KEY_RELEASED, (event) -> {
			//空格键: 播放/暂停音乐
			if(KeyCode.SPACE == event.getCode()) {
				getMediaPlayer().play();
			}
		});
		
		//TODO
		Guis.addDndAction(this, result -> {
			dndResult = new DndResult();
			String url = result.getUrl();
			
			if(url.startsWith(ConfigConstant.FILE_PREFIX)) {
				dndResult.setDndType(DndType.FILE);
				handleDndFile(result);
			} else if(url.startsWith(ConfigConstant.HTTPS_PREFIX) 
					|| url.startsWith(ConfigConstant.HTTP_PREFIX)) {
				dndResult.setDndType(DndType.LINK);
				handleDndUrl(url);
			}
			handleDndFailed(url);
		});
	}

	@Override
	protected void initDatas() {
		setFxMediaPlayer();
	}
	
	protected void initGraphDatas() {
		setAppTitle(ConfigConstant.APP_TITLE_DEFAULT_MODE);
		volumeSlider.setValue(ConfigConstant.INITIAL_VOLUME);
		updateMetadata(null);
		updateTimeText(0, 0);
		addIcons(Images.LOGO);
		initHelpText();
		getMediaPlayer().setShuffleMode(shuffleMode);
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
		getMediaPlayer().clearPlaylist();
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
			} else if(FileUtil.isDirectory(dndFile)
					|| FileUtil.isAudio(dndFile)) { //目录或音频
				dndResult.setSuccess(true);
				Future<?> future = getMediaPlayer().loadFrom(dndFile);
				AsyncServices.submit(() ->{
					if(future != null) {
						try {
							future.get();
						} catch (Exception e) {
							e.printStackTrace();
						}
						Platform.runLater(() -> {
							getMediaPlayer().play();
							updatePlaylistView();
						});
					}
				});
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

	private boolean handleDndUrl(String url) {
		//插件引擎实现
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
		rectSpectrum = new RectangleSpectrum(99);
		rectSpectrum.setSpacing(1);
		rectSpectrum.setAlignment(Pos.BOTTOM_LEFT);
	}
	
	private void toggleSpectrumView() {
		BorderPane mainCenterPane = byId("main_center");
		if(spectrumOn) {
			mainCenterPane.setCenter(rectSpectrum);
		} else {
			mainCenterPane.setCenter(audioMetaBox);
		}
		updateSpectrumBtn();
		updateAppTitle();
	}

	private String getAdjustAppTitle() {
		String result = titleLbl.getText();
		if(devMode) {
			return spectrumOn ? ConfigConstant.DEV_MODE_PREFIX.concat(result) 
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
	
	private void resetPlaylistView() {
		if(playlistView != null) {
			playlistView.resetGraph(true);
		}
	}

	private void updateLyricBtn() {
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
	
	public void updateMetadata(Media media) {
		Image image = null;
		String title = null;
		String artist = null;
		String album = null;
		String source = null;
		if(media != null) { //TODO
			Map<String, Object> metadata = media.getMetadata();
			source = media.getSource();
			source = StringUtil.getDefault(StringUtil.decodeNameFromUrl(source), 
									ConfigConstant.UNKOWN_AUDIO);
			
			image = (Image)metadata.get("image");
			title = (String)metadata.get("title");
			artist = (String)metadata.get("artist");
			album = (String)metadata.get("album");
		}
		
		//TODO
		album = StringUtil.getDefault(album, ConfigConstant.UNKOWN_ALBUM);
		album = album.startsWith("<") ? album : "<" + album + ">";
		
		titleLbl.setText(StringUtil.getDefault(title, source));
		artistLbl.setText(StringUtil.getDefault(artist, ConfigConstant.UNKOWN_ARTIST));
		albumLbl.setText(album);
		
		updateCoverArt(image, false);
		updateAppTitle();
	}
	
	public void updateCoverArt(Image image, boolean applyTheme) {
		if(applyTheme) {
			Guis.addStyleClass("theme-bg", coverArtLbl);
		} else {
			Guis.removeStyleClass("theme-bg", coverArtLbl);
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
	protected void updateOnReady(Media media) {
		updateMetadata(media);
		loadLyric(media.getSource());
	}
	
	private void loadLyric(String source) {
		if(lyricView != null) {
			lyricView.loadLyric(source);
		}
	}

	@Override
	public void updateProgress(double current, double duration) {
		updateProgressBar(current, duration);
		updateTimeText(current, duration);
		updateLyricView(current);
	}
	
	@Override
	public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		rectSpectrum.updateGraph(timestamp, duration, magnitudes, phases);
	}
}
