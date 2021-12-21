package xyz.less.graphic.views.mini;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import xyz.less.async.AsyncServices;
import xyz.less.bean.Audio;
import xyz.less.bean.ConfigConstant;
import xyz.less.bean.Resources.Fxmls;
import xyz.less.bean.Resources.Images;
import xyz.less.bean.Resources.Styles;
import xyz.less.graphic.Guis;
import xyz.less.graphic.action.DndAction.DndResult;
import xyz.less.graphic.action.DndAction.DndType;
import xyz.less.graphic.views.Attachable;
import xyz.less.graphic.views.PlayerView;
import xyz.less.graphic.views.PlaylistView;
import xyz.less.graphic.views.TwoLinesLyricRenderer;
import xyz.less.media.LyricParser;
import xyz.less.media.Metadatas;
import xyz.less.media.PlaybackQueue.PlayMode;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public final class MainView extends PlayerView {
	private ImageView playBtn;
	private ImageView playlistBtn; 
	private Label coverArtLbl;
	private Label audioProgress;
	private Label audioTitleLbl;
	private Label audioArtistLbl;
	
	private DndResult<?> dndResult;
	private ImageView defaultCoverArt;
	
	private AnchorPane mainPane;
	private StackPane coverArtProgressBox;
	private AnchorPane metadataLyricPane;
	private VBox metadataBox;
	private VBox lyricBox;
	private PlaylistView playlistView;
	private boolean playing = false;
	private boolean alwaysOnTop = true;
	
	private LyricParser lyricParser = new LyricParser();
	private TwoLinesLyricRenderer lyricRenderer = new TwoLinesLyricRenderer();
	
	Future<?> loadFuture;
	Future<?> updateFuture;
	
	public MainView(Stage stage, double width, double height) {
		super(stage, width, height);
		setAlignment(Pos.CENTER);
		addChildren(Guis.loadFxml(Fxmls.MINI_MAIN_VIEW));
	}
	
	public void initGraph() {
		initStyles();
		initContent();
		initEvents();
		initGraphDatas();
		initPlaylistFromArgs();
	}
	
	private void initStyles() {
		mainStage.getScene().setFill(null);
		Guis.addStylesheet(Styles.MINI_MAIN_VIEW, mainStage);
	}

	private void initContent() {
		coverArtProgressBox = byId("cover_art_progress_box");
		mainPane = byId("main_pane");
		metadataLyricPane = byId("metadata_lyric_pane");
		HBox actionsBox = byId("actions_box");
		metadataBox = byId("metadata_box");
		lyricBox = byId("lyric_box");
		Label line1 = byId("line_1");
		Label line2 = byId("line_2");
		
		coverArtLbl = byId("cover_art");
		audioProgress = byId("audio_progress");
		Label closeBtn = byId("close_btn");
		audioTitleLbl = byId("audio_title");
		audioArtistLbl = byId("audio_artist");
		
		ImageView playModeBtn = byId("playmode_btn");
		ImageView playPrevBtn = byId("play_prev_btn");
		playBtn = byId("play_btn");
		ImageView playNextBtn = byId("play_next_btn");
		playlistBtn = byId("playlist_btn");
		
		AnchorPane.setLeftAnchor(coverArtProgressBox, 10D);
		AnchorPane.setBottomAnchor(mainPane, 0D);
		AnchorPane.setBottomAnchor(metadataLyricPane, 0D);
		
		AnchorPane.setTopAnchor(closeBtn, 6D);
		AnchorPane.setRightAnchor(closeBtn, 6D);
		AnchorPane.setRightAnchor(actionsBox, 25D);
		AnchorPane.setRightAnchor(metadataBox, 25D);
		AnchorPane.setRightAnchor(lyricBox, 25D);
		
		Guis.setGraphic(Images.CLOSE, closeBtn);
		Guis.addStyleClass("label-btn", closeBtn);
		Guis.setPickOnBounds(true, closeBtn);
		
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		
		Guis.addStyleClass("bottom-radius", mainPane);
		Guis.addStyleClass("bottom-radius", metadataLyricPane);
		
		//TODO
		playModeBtn.setImage(Images.PLAY_MODE[3]);
		playPrevBtn.setImage(Images.PLAY_PREV);
		playBtn.setImage(Images.PLAY[0]);
		playNextBtn.setImage(Images.PLAY_NEXT);
		playlistBtn.setImage(Images.PLAYLIST[0]);
		Guis.setUserData(3, playModeBtn);
		
		Guis.setGraphic(getDefaultCoverArt(), coverArtLbl);
		
		Guis.applyChildrenDeeply(node -> {
				if(node instanceof ImageView) {
					ImageView btn = (ImageView)node;
					Guis.addStyleClass("image-btn", btn);
					Guis.setPickOnBounds(true, btn);
//					Guis.setFitSize(ConfigConstant.PLAYER_ICON_FIT_SIZE, btn);
				}
			}, mainPane);
		
		closeBtn.setOnMouseClicked(e -> Guis.exitApplication());
		
		playModeBtn.setOnMouseClicked(e -> {
			int index = Guis.toggleImage(playModeBtn, Images.PLAY_MODE);
			getMediaPlayer().setPlayMode(PlayMode.valueOf(index));
		});
		
		playPrevBtn.setOnMouseClicked(e -> getMediaPlayer().playPrevious());
		playBtn.setOnMouseClicked(e -> togglePlay());
		playNextBtn.setOnMouseClicked(e -> getMediaPlayer().playNext());
		playlistBtn.setOnMouseClicked(e -> playlistView.toggle());
		
		Guis.addDnmAction(mainStage, mainStage.getScene().getRoot(), dnmOffset -> {
			Guis.applyStages(stage -> {
				if(stage instanceof Attachable) {
					((Attachable)stage).attach();
				}
			}, playlistView);
		}, closeBtn);
		
		//TODO
		lyricRenderer.setLines(line1, line2);
		lyricRenderer.setOnNoLyric(()-> {
			metadataBox.setVisible(true);
			lyricBox.setVisible(false);
		});
	}
	
	private void initEvents() {
		mainStage.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			//空格键: 播放/暂停音乐
			if(KeyCode.SPACE == e.getCode()) {
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
		
		Guis.addAutoDrawerAction(mainStage).setOnHidden(e -> {
			Guis.ifPresent(playlistView, t -> playlistView.hide());
		});
		
		Guis.addHoverAction(e -> {
			if(playing) {
				boolean visible = metadataLyricPane.isVisible();
				metadataLyricPane.setVisible(!visible);
			}
		}, e-> {
			metadataLyricPane.setVisible(playing);
		}, mainStage.getScene().getRoot());
	}

	protected void initGraphDatas() {
		setAppTitle(ConfigConstant.APP_TITLE_DEFAULT_MODE);
		addIcons(Images.LOGO);
		updateMetadata(null, null);
		initPlaylistView();
		Guis.setAlwaysOnTop(alwaysOnTop, mainStage);
		//TODO
		getMediaPlayer().setPlayMode(PlayMode.SHUFFLE);
		getMediaPlayer().setVolumn(1);
	}
	
	//TODO
	private void initPlaylistFromArgs() {
		String[] args = (String[])mainStage.getUserData();
		if(args != null && args.length > 0) {
			String name = StringUtil.toSlash(args[0]);
			doHandleDndFile(new File(name));
		}
	}

	//TODO
	private void handleDndFailed(String url) {
		if(!dndResult.isSuccess()) {
			try {
				Desktop desktop = Desktop.getDesktop();
				desktop.browse(URI.create(url));
			} catch (IOException e) {
//					e.printStackTrace();
				updateOnDndFail();
			}
		} 
	}

	//TODO
	private void updateOnDndFail() {
		resetPlaylistView();
		getMediaPlayer().resetPlaybackQueue();
	}
	
	//TODO
	private void handleDndFile(Dragboard result) {
		try {
			List<File> fileList = result.getFiles();
			File dndFile = fileList.get(0);
			if(FileUtil.isImage(dndFile)) { //图片
				dndResult.setDndType(DndType.IMAGE);
				updateCoverArt(new Image(result.getUrl()));
				dndResult.setSuccess(true);
			} else if(FileUtil.isDirectory(dndFile)
					|| FileUtil.isAudio(dndFile)) { //目录或音频
				dndResult.setSuccess(true);
				doHandleDndFile(dndFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doHandleDndFile(File dndFile) {
		AsyncServices.cancel(loadFuture, updateFuture);
		loadFuture = getMediaPlayer().loadFrom(dndFile);
		AsyncServices.submitFxTaskOnFutureDone(loadFuture, () ->{
			if(getMediaPlayer().isPlaylistEmpty()) {
				updateOnDndFail();
				return ;
			}
			getMediaPlayer().getPlaylist().sort();
			getMediaPlayer().play();
			updatePlaylistView();
			updateFuture = getMediaPlayer().updateMetadatas();
			AsyncServices.submitFxTaskOnFutureDone(updateFuture,() -> {
				updatePlaylistViewTimeLbl();
			});
		}, null, () -> updateOnDndFail());
	}

	private boolean handleDndLinkUrl(String url) {
		//插件引擎实现
//		System.out.println(url);
		getMediaPlayer().playUrl(url);
		updatePlaylistView();
		return true;
	}
	
	private void initHelpText() {
		doUpdateMetadata(null, 
				"试一试拖拽东西到播放器吧~",
				"类型: 文件、文件夹、其他");
	}

	private void updateDndFailText() {
		doUpdateMetadata(Images.DND_NOT_FOUND, 
				"暂时无法识别哦\r试一试拖拽其他吧~",
				"神秘代号: 404");
	}
	
	private void updateDndWaiting() {
		doUpdateMetadata(null, 
				"正在努力加载，请耐心等待~",
				"精彩即将开始");
	}
	
	private void updateDndDone() {
		doUpdateMetadata(null, 
				"加载完成，正在努力识别~",
				"精彩即将开始");
	}
	
	private void updateNoMediaText() {
		doUpdateMetadata(null, 
				"暂时无法播放哦\r试一试拖拽其他吧~",
				"神秘代号: 500");
	}
	
	private void doUpdateMetadata(Image cover, String title, String artist) {
		updateCoverArt(cover);
//		metadataLyricPane.setVisible(true);
		audioTitleLbl.setText(StringUtil.getDefault(title, ConfigConstant.UNKOWN_AUDIO));
		audioArtistLbl.setText(StringUtil.getDefault(artist, ConfigConstant.UNKOWN_ARTIST));
	}

	//TODO
	private ImageView getDefaultCoverArt() {
		Guis.ifNotPresent(defaultCoverArt, t -> {
			defaultCoverArt = new ImageView(Images.DEFAULT_COVER_ART);
			defaultCoverArt.setFitWidth(85);
			defaultCoverArt.setFitHeight(85);
//			defaultCoverArt.setSmooth(true);
		});
		return defaultCoverArt;
	}
	
	private void initPlaylistView() {
		Guis.ifNotPresent(playlistView, t -> {
			playlistView = new PlaylistView(mainStage, getMediaPlayer());
			playlistView.setTopVisible(false);
			playlistView.setWidth(mainStage.getWidth());
			playlistView.setHeight(300);
			playlistView.setAttachAction(s -> {
				playlistView.setX(mainStage.getX());
				playlistView.setY(mainStage.getY() + mainStage.getHeight() + 1);
			});
			playlistView.getScene().getRoot().getStyleClass().add("playlistView-padding");
			
			playlistView.setOnShown(e -> {
				updatePlaylistBtn();
			});
			
			playlistView.setOnHidden(e -> {
				updatePlaylistBtn();
			});
		});
	}

	private void updatePlaylistView() {
		Guis.ifPresent(playlistView, t -> playlistView.updateGraph());
	}
	
	private void updatePlaylistViewTimeLbl() {
		Guis.ifPresent(playlistView, 
				t -> playlistView.updateTimeLbl());
	}
	
	private void resetPlaylistView() {
		
	}
	
	private void updatePlaylistBtn() {
		Guis.setImage(playlistBtn, Images.PLAYLIST, playlistView.isShowing());
	}
	
	//TODO
	public void updateProgressBar(double current, double duration) {
		double r = audioProgress.getWidth()/2 + 0.5;
		Arc arc = new Arc();
		arc.setRadiusX(r);
		arc.setRadiusY(r);
		arc.setCenterX(r);
		arc.setCenterY(r);
		arc.setStartAngle(90);
		arc.setLength(-360 * current/duration);
//		arc.setFill(Paint.valueOf("#1ca388"));
		arc.setType(ArcType.ROUND);
		audioProgress.setClip(arc);
		Guis.addStyleClass("theme-fg", audioProgress);
	}
	
	public void updateMetadata(Audio audio, Map<String, Object> metadata) {
		if(audio == null || metadata == null) {
			return ;
		}
		String title = (String)metadata.get(Metadatas.TITLE);
		String artist = (String)metadata.get(Metadatas.ARTIST);
		String album = (String)metadata.get(Metadatas.ALBUM);
		Image image = (Image)metadata.get(Metadatas.COVER_ART);
		
		//TODO 元数据可能会出现乱码
		image = image != null ? image : audio.getCoverArt();
		title = !StringUtil.isBlank(title) ? title : audio.getTitle();
		artist = !StringUtil.isBlank(artist) ? artist : audio.getArtist();
		album = !StringUtil.isBlank(album)  ? album : audio.getAlbum();
		
		doUpdateMetadata(image, title, artist);
	}
	
	public void updateCoverArt(Image image) {
		ImageView graphic = getDefaultCoverArt();
		if(image != null) { //TODO
			graphic = new ImageView(image);
			double size = 85;
			graphic.setFitWidth(size);
			graphic.setFitHeight(size);
			double r = size/2;
			Circle circle = new Circle(r);
			circle.setCenterX(r);
			circle.setCenterY(r);
			graphic.setClip(circle);
		}
		Guis.setGraphic(graphic, coverArtLbl);
	}
	
	public void updatePlayBtn(boolean playing) {
		Guis.setUserData(Guis.setImage(playBtn, Images.PLAY, playing), 
				playBtn);
	}
	
	@Override
	public void highlightPlaylist() {
		Guis.ifPresent(playlistView, t -> playlistView.highlightCurrentPlaying());
	}
	
	@Override
	protected void updateOnPlaying(boolean playing) {
		this.playing = playing;
		updatePlayBtn(playing);
	}

	@Override
	protected void updateOnReady(Audio audio, Map<String, Object> metadata) {
		updateMetadata(audio, metadata);
		loadLyric(audio);
	}
	
	private void loadLyric(Audio audio) {
		boolean hasLyric = loadLyric(audio.getSource());
		//TODO
		lyricBox.setVisible(hasLyric);
		metadataBox.setVisible(!hasLyric);
	}

	
	public boolean loadLyric(String uri) {
		try {
			lyricRenderer.reset();
			int index = uri.lastIndexOf(".");
			uri = uri.substring(0, index) + ".lrc";
			return lyricRenderer.setLyric(lyricParser.parse(uri));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void onNoPlayableMedia() {
		metadataLyricPane.setVisible(false);
		metadataBox.setVisible(true);
		lyricBox.setVisible(false);
	}

	@Override
	public void updateProgress(double current, double duration) {
		updateProgressBar(current, duration);
		updateLyric(current, duration);
	}

	private void updateLyric(double current, double duration) {
		lyricRenderer.render(current);
	}

	@Override
	public void setAppTitle(String title) {
		mainStage.setTitle(ConfigConstant.APP_TITLE);
	}
	
}
