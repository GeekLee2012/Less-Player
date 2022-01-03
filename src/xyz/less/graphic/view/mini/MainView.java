package xyz.less.graphic.view.mini;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import xyz.less.async.AsyncServices;
import xyz.less.bean.Audio;
import xyz.less.bean.Constants;
import xyz.less.bean.Resources;
import xyz.less.bean.Resources.Fxmls;
import xyz.less.bean.Resources.Images;
import xyz.less.bean.Resources.Styles;
import xyz.less.graphic.Guis;
import xyz.less.graphic.TwoLinesLyricRenderer;
import xyz.less.graphic.anim.RotateAnimation;
import xyz.less.graphic.handler.DefaultDndHandle;
import xyz.less.graphic.handler.IDndHandle;
import xyz.less.graphic.view.Attachable;
import xyz.less.graphic.view.PlayerView;
import xyz.less.graphic.view.PlaylistView;
import xyz.less.media.LyricParser;
import xyz.less.media.Metadatas;
import xyz.less.media.PlaybackQueue.PlayMode;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

/**
 * Mini风格
 */
public final class MainView extends PlayerView {
	private ImageView playBtn;
	private ImageView playlistBtn; 
	private Region audioProgress;
	private Label coverArtLbl;
	private Region coverAperture;
	private Label audioTitleLbl;
	private Label audioArtistLbl;
	
	private AnchorPane mainPane;
	private StackPane coverArtProgressBox;
	private AnchorPane metadataLyricPane;
	private VBox metadataBox;
	private VBox lyricBox;
	private PlaylistView playlistView;
	private boolean playing = false;
	private boolean useDefaultCoverArt;
	private boolean alwaysOnTop = true;
	private double volume = 1;
	private double animDuration = 13;
	
	private RotateAnimation rotateAnim = new RotateAnimation();
	private LyricParser lyricParser = new LyricParser();
	private TwoLinesLyricRenderer lyricRenderer = new TwoLinesLyricRenderer();
	
	private IDndHandle dndHandle;
	private Future<?> loadFuture;
	private Future<?> updateFuture;
	
	public MainView(double width, double height) {
		super(width, height);
		setAlignment(Pos.CENTER);
		addChildren(Guis.loadFxml(Fxmls.MINI_MAIN_VIEW));
	}
	
	public void initGraph() {
		initStyles();
		initContent();
		initEvents();
		initGraphDatas();
	}
	
	private void initStyles() {
		Guis.setTransparent(getMainStage());
		Guis.addStylesheet(Styles.MINI_MAIN_VIEW, getMainStage());
	}

	private void initContent() {
		mainPane = byId("main_pane");
		
		coverArtProgressBox = byId("cover_art_progress_box");
		metadataLyricPane = byId("metadata_lyric_pane");
		HBox actionsBox = byId("actions_box");
		metadataBox = byId("metadata_box");
		lyricBox = byId("lyric_box");
		Label line1 = byId("line_1");
		Label line2 = byId("line_2");
		
		audioProgress = byId("audio_progress");
		coverArtLbl = byId("cover_art");
		coverAperture = byId("cover_aperture");
		
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
		
		Guis.applyChildrenDeeply(node -> {
				if(node instanceof ImageView) {
					ImageView btn = (ImageView)node;
					Guis.addStyleClass("image-btn", btn);
					Guis.setPickOnBounds(true, btn);
				}
			}, mainPane);
		
		closeBtn.setOnMouseClicked(e -> {
			Guis.applyStages(s -> s.close(), getMainStage(), playlistView);
			Guis.exitApplication();
		});
		
		playModeBtn.setOnMouseClicked(e -> {
			int index = Guis.toggleImage(playModeBtn, Images.PLAY_MODE);
			getMediaPlayer().setPlayMode(PlayMode.valueOf(index));
		});
		
		playPrevBtn.setOnMouseClicked(e -> getMediaPlayer().playPrevious());
		playBtn.setOnMouseClicked(e -> togglePlay());
		playNextBtn.setOnMouseClicked(e -> getMediaPlayer().playNext());
		playlistBtn.setOnMouseClicked(e -> playlistView.toggle());
		
		Guis.addDnmAction(getMainStage(), getMainStage().getScene().getRoot(), dnmOffset -> {
			Guis.applyStages(stage -> {
				if(stage instanceof Attachable) {
					((Attachable)stage).attach();
				}
			}, playlistView);
		}, closeBtn);
		
		rotateAnim.setNode(coverArtLbl);
		rotateAnim.setDuration(Duration.seconds(animDuration));
		
		//TODO
		lyricRenderer.setLines(line1, line2);
		lyricRenderer.setOnNoLyric(()-> {
			metadataBox.setVisible(true);
			lyricBox.setVisible(false);
			line1.setText("暂时没有发现歌词");
			line2.setText("请继续欣赏音乐吧");
		});
	}
	
	private void initEvents() {
		getMainStage().addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			//空格键: 播放/暂停音乐
			if(KeyCode.SPACE == e.getCode()) {
				togglePlay();
			}
		});
		
		//拖拽
		Guis.addDndAction(this, ctx -> {
			initDndHandle().handle(ctx);
		});
		
		//自动隐藏
		if(isEnableAutoDrawer()) {
			Guis.addAutoDrawerAction(getMainStage()).setOnHidden(e -> {
				Guis.ifPresent(playlistView, t -> playlistView.hide());
			});
		}
		
		//TODO
		Guis.addHoverAction(e -> {
			if(playing) {
				boolean visible = metadataLyricPane.isVisible();
				metadataLyricPane.setVisible(!visible);
			}
		}, e-> {
			metadataLyricPane.setVisible(playing);
		}, getMainStage().getScene().getRoot());
	}
	
	//TODO
	private IDndHandle initDndHandle() {
		if(dndHandle != null) {
			return dndHandle;
		}
		dndHandle = new DefaultDndHandle();
		dndHandle.addHandler(ctx -> {
			ctx.successProperty().addListener((c, ov, nv)-> {
				if(!nv) {
					updateOnDndFail();
				}
			});
		}).addHandler(ctx -> {
			if(ctx.isImage()) { //图片
				updateCoverArt((Image)ctx.getUserData(), false);
			}
		}).addHandler(ctx -> {
			if(ctx.isLyric()) { //歌词
				ctx.setSuccess(loadLyric(StringUtil.transformUri(ctx.getUrl())));
			}
		}).addHandler(ctx -> { //目录或音频
			if(ctx.isAudio() || ctx.isDirectory()) {
				handleDndFile((File)ctx.getUserData());
			}
		}).addHandler(ctx -> { 
			if(ctx.isFile()) { //其他文件
				ctx.setSuccess(false);
			}
		}).addHandler(ctx -> { 
			if(ctx.isLink()) { //超链接
				handleDndLinkUrl(ctx.getUrl());
			}
		});
		return dndHandle;
	}

	protected void initGraphDatas() {
		setAppTitle();
		addIcons(Images.LOGO);
		updateMetadata();
		initPlaylistView();
		Guis.setAlwaysOnTop(alwaysOnTop, getMainStage());
		//TODO
		getMediaPlayer().setPlayMode(PlayMode.SHUFFLE);
		getMediaPlayer().setVolume(volume);
		
	}
	
	protected void doPlayFromArgs(File file) {
		handleDndFile(file);
	}

	//TODO
	private void updateOnDndFail() {
		resetPlaylistView();
		getMediaPlayer().resetPlaybackQueue();
		updateDndFailText();
	}

	private void handleDndFile(File dndFile) {
		if(!FileUtil.exists(dndFile)) {
			return ;
		}
		updateDndWaiting();
		AsyncServices.cancel(loadFuture, updateFuture);
		loadFuture = getMediaPlayer().loadFrom(dndFile);
		AsyncServices.submitFxTaskOnFutureDone(loadFuture, () ->{
			updateDndDone();
			if(getMediaPlayer().isPlaylistEmpty()) {
				dndHandle.getContext().setSuccess(false);
				return ;
			}
			getMediaPlayer().getPlaylist().sort();
			getMediaPlayer().play();
			updatePlaylistView();
			updateFuture = getMediaPlayer().updateMetadatas();
		}, null, () -> dndHandle.getContext().setSuccess(false));
	}

	//TODO
	private boolean handleDndLinkUrl(String url) {
		//插件引擎实现
		getMediaPlayer().playUrl(url);
		updatePlaylistView();
		return true;
	}
/*
	private void showHelpText() {
		showMetadataBox(true);
		setInfoTextStyle(true);
		doUpdateMetadata(null, false,
				"试一试拖拽东西到播放器吧~",
				"类型: 文件、文件夹、其他");
	}

	private void updateNoMediaText() {
		showMetadataBox(true);
		setInfoTextStyle(true);
		doUpdateMetadata(null, false,
				"暂时无法播放哦",
				"试一试拖拽其他吧~");
	}
*/	
	private void updateDndFailText() {
		showMetadataBox(true);
		setInfoTextStyle(true);
		doUpdateMetadata(Images.DND_NOT_FOUND, true,
				"暂时无法识别哦，",
				"试一试拖拽其他吧~");
	}
	
	private void updateDndWaiting() {
		showMetadataBox(true);
		setInfoTextStyle(true);
		doUpdateMetadata(null, false, 
				"正在努力加载，",
				"请耐心等待~");
	}
	
	private void updateDndDone() {
		showMetadataBox(true);
		setInfoTextStyle(true);
		doUpdateMetadata(null, false,
				"加载已完成，",
				"正在努力识别~");
	}

	private void doUpdateMetadata(Image cover, boolean applyTheme, String title, String artist) {
		updateCoverArt(cover, applyTheme);
		audioTitleLbl.setText(StringUtil.getDefault(title, Constants.UNKOWN_AUDIO));
		audioArtistLbl.setText(StringUtil.getDefault(artist, Constants.UNKOWN_ARTIST));
	}

	private void initPlaylistView() {
		Stage mainStage = getMainStage();
		//TODO
		Guis.ifNotPresent(playlistView, t -> {
			playlistView = new PlaylistView(mainStage, getMediaPlayer());
			playlistView.setTopVisible(false);
			playlistView.setWidth(mainStage.getWidth());
			playlistView.setHeight(315); // 显示8首歌曲
			
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
	
	private void resetPlaylistView() {
		Guis.ifPresent(playlistView, 
				t -> playlistView.resetGraph(true));
	}
	
	private void updatePlaylistBtn() {
		Guis.setImage(playlistBtn, Images.PLAYLIST, playlistView.isShowing());
	}
	
	//TODO
	public void updateProgressBar(double current, double duration) {
		double r = getProgressArcRadius();
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
	
	private double getProgressArcRadius() {
		return audioProgress.getWidth()/2 + 0.5;
	}
	
	public void updateMetadata() {
		updateMetadata(null, null);
	}
	
	public void updateMetadata(Audio audio, Map<String, Object> metadata) {
		if(audio == null || metadata == null) {
			updateCoverArt(null, false);
			return ;
		}
		String title = Metadatas.getTitle(metadata);
		String artist = Metadatas.getArtist(metadata);
		String album = Metadatas.getAlbum(metadata);
		Image image = Metadatas.getCoverArt(metadata);
		String extra = Metadatas.getExtra(metadata);
		
		//TODO 元数据可能会出现乱码
		image = image != null ? image : audio.getCoverArt();
		title = !StringUtil.isBlank(title) ? title : audio.getTitle();
		artist = !StringUtil.isBlank(artist) ? artist : audio.getArtist();
		album = !StringUtil.isBlank(album)  ? album : audio.getAlbum();
		//迷你模式显示额外信息
		artist = StringUtil.isBlank(extra) ? artist : extra;
		
		setInfoTextStyle(false);
		doUpdateMetadata(image, false, title, artist);
	}
	
	public void updateCoverArt(Image image, boolean applyTheme) {
		//TODO
		useDefaultCoverArt = (image == null);
		image = useDefaultCoverArt ? Images.DEFAULT_COVER_ART : image;
		ImageView graphic = new ImageView(image);
		double size = 85;
		graphic.setFitWidth(size);
		graphic.setFitHeight(size);
		graphic.setSmooth(true);
//		graphic.setPreserveRatio(true);
		double r = size/2;
		Circle circle = new Circle(r);
		circle.setCenterX(r);
		circle.setCenterY(r);
		graphic.setClip(circle);
		Guis.setGraphic(graphic, coverArtLbl);
		if(applyTheme) {
			Guis.addStyleClass("theme-fg", coverArtLbl);
		} else {
			Guis.removeStyleClass("theme-fg", coverArtLbl);
		}
		
		updateCoverAperture();
	}
	
	private void updateCoverAperture() {
		boolean visible = false;
		if(isEnableCoverAperture()) {
			visible = !useDefaultCoverArt;
		} 
		coverAperture.setVisible(visible);
	}

	public void updatePlayBtn() {
		Guis.setUserData(Guis.setImage(playBtn, Images.PLAY, playing), 
				playBtn);
	}
	
	public void updatePlayAnim() {
		if(!isEnableAnim() || useDefaultCoverArt) {
			return ;
		}
		if(playing) {
			rotateAnim.start();
		} else {
			rotateAnim.stop();
		}
	}
	
	@Override
	public void highlightPlaylist() {
		Guis.ifPresent(playlistView, t -> playlistView.highlightCurrentPlaying());
	}
	
	@Override
	protected void updateOnPlaying(boolean playing) {
		this.playing = playing;
		updatePlayBtn();
		updatePlayAnim();
	}

	@Override
	protected void updateOnReady(Audio audio, Map<String, Object> metadata) {
		updateMetadata(audio, metadata);
		loadLyric(audio);
	}
	
	private void loadLyric(Audio audio) {
		//TODO
		String source = audio.getSource();
		int index = source.lastIndexOf(".");
		String uri = source.substring(0, index) + Resources.LYRIC_SUFFIXES[0];
		boolean hasLyric = loadLyric(uri);
		
		lyricBox.setVisible(hasLyric);
		metadataBox.setVisible(!hasLyric);
	}

	
	public boolean loadLyric(String uri) {
		try {
			lyricRenderer.reset();
			return lyricRenderer.setLyric(lyricParser.parse(uri));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void onNoPlayableMedia() {
		showMetadataBox(false);
	}

	private void showMetadataBox(boolean value) {
		metadataLyricPane.setVisible(value);
		metadataBox.setVisible(true);
		lyricBox.setVisible(false);
	}
	
	private void setInfoTextStyle(boolean value) {
		String infoStyleClass = "info-line";
		if(value) {
			Guis.addStyleClass(infoStyleClass, audioTitleLbl, audioArtistLbl);
		} else {
			Guis.removeStyleClass(infoStyleClass, audioTitleLbl, audioArtistLbl);
		}
		
	}

	@Override
	public void updateProgress(double current, double duration) {
		updateProgressBar(current, duration);
		updateLyric(current, duration);
	}

	private void updateLyric(double current, double duration) {
		lyricRenderer.render(current);
	}

}
