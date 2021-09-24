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
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import xyz.less.action.DndAction.DndResult;
import xyz.less.action.DndAction.DndType;
import xyz.less.async.AsyncServices;
import xyz.less.bean.ConfigConstant;
import xyz.less.bean.Resources.Fxmls;
import xyz.less.bean.Resources.Images;
import xyz.less.bean.Resources.Styles;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public class MainView extends PlayerView {
	private Label mainTitleLbl;
	private Slider volumeSlider;
	private Label timeLbl;
	private Label coverArtLbl;
	private Label titleLbl;
	private Label artistLbl;
	private Label albumLbl;
	private ImageView defaultCoverArt;
	private ImageView playBtn;
	private ImageView volumeBtn;
	private ImageView playlistBtn;
	private PlaylistView playlistView;
	private ProgressBar progressBar;
	
	private boolean shuffleMode = true;
	private boolean alwaysOnTop;
	private boolean devMode = false;
	private DndResult dndResult;	
	
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
		initDatas();
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
				playlistView.attach();
			}, playlistView);
		}, winBtnsBox);
		
		logoBtn.setOnMouseClicked(e -> {
			devMode = !devMode;
			if(devMode) {
				setAppTitle(ConfigConstant.APP_TITLE_DEV_MODE);
				Guis.addStyleClass("dev-mode-bg", logoBtn);
				Guis.addStyleClass("dev-mode-text", mainTitleLbl);
			} else {
				setAppTitle(ConfigConstant.APP_TITLE);
				Guis.removeStyleClass("dev-mode-bg", logoBtn);
				Guis.removeStyleClass("dev-mode-text", mainTitleLbl);
			}
		});
		
		pinBtn.setOnMouseClicked(e -> {
			alwaysOnTop = (Guis.toggleImage(pinBtn, Images.PIN) == 1);
			Guis.setAlwaysOnTop(alwaysOnTop, mainStage, playlistView);
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
		
		Guis.addStyleClass("cover-art", coverArtLbl);
		Guis.addStyleClass("audio-title", titleLbl);
		Guis.addStyleClass("audio-artist", artistLbl);
		Guis.addStyleClass("audio-album", albumLbl);
	}
	
	private void initBottom() {
		BorderPane pane = byId("main_bottom");
		
		progressBar = byId("progress_bar");
		timeLbl = byId("audio_time");
		
		ImageView repeatBtn = byId("repeat_btn");
		ImageView playPrevBtn = byId("play_prev_btn");
		playBtn = byId("play_btn");
		ImageView playNextBtn = byId("play_next_btn");
		ImageView shuffleBtn = byId("shuffle_btn");
		
		playlistBtn = byId("playlist_btn");
		volumeBtn = byId("volume_btn");
		volumeSlider = byId("volume_bar");
		
		double ncnWidth = 175;
		((Pane)pane.getLeft()).setPrefWidth(ncnWidth);
		((Pane)pane.getRight()).setPrefWidth(ncnWidth);
		
		//TODO
		repeatBtn.setImage(Images.REPEAT[0]);
		playPrevBtn.setImage(Images.PLAY_PREV);
		playBtn.setImage(Images.PLAY[0]);
		playNextBtn.setImage(Images.PLAY_NEXT);
		shuffleBtn.setImage(Images.SHUFFLE[1]);
		volumeBtn.setImage(Images.VOLUME[0]);
		playlistBtn.setImage(Images.PLAYLIST[0]);
		
		Guis.setUserData(shuffleMode ? 1 : 0, shuffleBtn);
		
		//TODO
		Guis.applyChildrenDeeply(node -> {
				if(node instanceof ImageView) {
					ImageView btn = (ImageView)node;
					Guis.addStyleClass("image-btn", btn);
					Guis.setPickOnBounds(true, btn);
					Guis.setFitSize(ConfigConstant.PLAYER_ICON_FIT_SIZE, btn);
				}
			}, pane);
		
		Guis.bind(volumeSlider.maxProperty(), volumeSlider.prefWidthProperty());
		
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
			togglePlaylistView();
		});
		
		progressBar.addListener((o,ov,nv) -> {
			getMediaPlayer().seek(nv.doubleValue());
		});
		
		//TODO
		volumeSlider.setOnMouseClicked(e -> {
			volumeSlider.setValue(e.getX());
		});
		volumeSlider.valueProperty().addListener((o,ov,nv) -> {
			Region track = bySelector("#volume_bar .track");
			double x = nv.doubleValue();
			if(track != null) {
				track.setStyle(getTrackStyle(x));
			}
			updateVolumeBtn(x);
			getMediaPlayer().setVolumn(x / volumeSlider.getMax());
		});
	}
	
	private void initEvents() {
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
		setAppTitle(ConfigConstant.APP_TITLE);
		volumeSlider.setValue(ConfigConstant.INITIAL_VOLUME);
		updateMetadata(null);
		updateTimeText(0, 0);
		addIcons(Images.LOGO);
		initHelpText();
		getMediaPlayer().setShuffleMode(shuffleMode);
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
		mediaPlayer.clearPlaylist();
		updateDndFailText();
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

	private boolean handleDndUrl(String url) {
		//插件引擎实现
		return true;
	}

	private ImageView getDefaultCoverArt() {
		if(defaultCoverArt == null) {
			defaultCoverArt = new ImageView(Images.DEFAULT_COVER_ART);
			defaultCoverArt.setFitWidth(ConfigConstant.COVER_ART_FIT_SIZE);
			defaultCoverArt.setFitHeight(ConfigConstant.COVER_ART_FIT_SIZE);
		}
		return defaultCoverArt;
	}

	private void togglePlaylistView() {
		if(playlistView == null) {
			playlistView = new PlaylistView(mainStage, getMediaPlayer());
			
			playlistView.setOnHidden(e -> {
				updatePlaylistBtn();
			});
			playlistView.setOnShown(e -> {
				updatePlaylistBtn();
			});
		}
		if(playlistView.isShowing()) {
			playlistView.hide();
		} else {
			Guis.setAlwaysOnTop(alwaysOnTop, playlistView);
			playlistView.show();
		}
	}

	private void updatePlaylistBtn() {
		int index = playlistView.isShowing() ? 1 : 0;
		playlistBtn.setImage(Images.PLAYLIST[index]);
	}

	//TODO
	private String getTrackStyle(double x) {
		x = x > 0 ? x : 0.01;
		String style = "-fx-pref-height: 5;"
				+ "-fx-max-height: 5;"
				+ "-fx-background-color: #464646;"
				+ "-fx-background-radius: 0;"
				+ "-fx-background-image: url('/resources/images/music_volume_track.png');"
				+ "-fx-background-size: 1;"
				+ "-fx-background-repeat: repeat-y;";
		String key = "-fx-background-size:";
		int index1 = style.indexOf(key);
		int index2 = style.indexOf(";", index1);
		index1 += key.length();
		return style.substring(0, index1) + x + style.substring(index2);
	}

	@Override
	public void updateProgressBar(double percent) {
		progressBar.setSeekable(mediaPlayer.isInit());
		progressBar.updateProgress(percent);
	}
	
	@Override
	public void updateTimeText(double current, double duration) {
		timeLbl.setText(String.format(ConfigConstant.CURRENT_DURATION_FORMAT, 
				StringUtil.toMmss(current),
				StringUtil.toMmss(duration)));
	}
	
	@Override
	public void updateMetadata(Media media) {
		Image image = null;
		String title = null;
		String artist = null;
		String album = null;
		String source = null;
		if(media != null) {
			Map<String, Object> metadata = media.getMetadata();
//			metadata.forEach((k,v) -> {
//				System.out.println(k + ": " + v);
//			});
			//TODO
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
	
	@Override
	public void updatePlayBtn(boolean playing) {
		int index = playing ? 1 : 0;
		Guis.setUserData(index, playBtn);
		playBtn.setImage(Images.PLAY[index]);
	}
	
	private void updateVolumeBtn(double x) {
		double lowLimit = volumeSlider.getMax() / 2;
		int index = x > 0 ? (x >= lowLimit ? 0 : 1) : 2;
		Guis.setUserData(index, volumeBtn);
		volumeBtn.setImage(Images.VOLUME[index]);
	}

	@Override
	public void setAppTitle(String title) {
		mainTitleLbl.setText(title);
	}
	
	public void highlightPlaylist() {
		if(playlistView != null) {
			playlistView.highlightCurrentPlaying();
		}
	}
	
	private void initHelpText() {
		titleLbl.setText("试一试拖拽东西到播放器吧~");
		artistLbl.setText("类型: 文件、文件夹、其他");
		albumLbl.setText("<HELP>");
	}
	
}
