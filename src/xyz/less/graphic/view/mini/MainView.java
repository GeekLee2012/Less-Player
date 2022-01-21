package xyz.less.graphic.view.mini;

import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import xyz.less.api.provider.GraphicApiProvider;
import xyz.less.api.provider.PlaylistApiProvider;
import xyz.less.bean.Audio;
import xyz.less.bean.Constants;
import xyz.less.bean.Resources;
import xyz.less.bean.Resources.Images;
import xyz.less.graphic.Guis;
import xyz.less.graphic.TwoLinesLyricRenderer;
import xyz.less.graphic.anim.RotateAnimation;
import xyz.less.graphic.skin.MiniSkin;
import xyz.less.graphic.skin.SimpleSkin;
import xyz.less.graphic.view.Attachable;
import xyz.less.graphic.view.PlayerView;
import xyz.less.graphic.view.PlaylistView;
import xyz.less.media.LyricParser;
import xyz.less.media.Metadatas;
import xyz.less.media.PlaybackQueue.PlayMode;
import xyz.less.util.StringUtil;

/**
 * Mini风格
 */
public final class MainView extends PlayerView {
	private ImageView playModeBtn;
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
	private boolean useDefaultCoverArt;
	private boolean alwaysOnTop = true;
	private double animDuration = 13;
	
	private RotateAnimation rotateAnim = new RotateAnimation();
	private LyricParser lyricParser = new LyricParser();
	private TwoLinesLyricRenderer lyricRenderer = new TwoLinesLyricRenderer();
	
	public MainView(double width, double height) {
		super(width, height);
		setAlignment(Pos.CENTER);
		addChildren(Guis.loadFxml(MiniSkin.MINI_MAIN_VIEW_FXML));
		//TODO
		exportApis();
	}
	
	private void exportApis() {
		Consumer<Image> coverArtConsumer = (Image image) -> updateCoverArt(image, false);
		Runnable onPlaylistChanged = () -> { 
			//getMediaPlayer().getPlaylist().sort();
			updatePlaylist();
			getMediaPlayer().updateMetadatas();
		};
		exportObjectForField(GraphicApiProvider.class, "coverArtConsumer", coverArtConsumer);
		exportObjectForField(PlaylistApiProvider.class, "onChanged", onPlaylistChanged);
	}

	@Override
	protected void initStyles() {
		Guis.setTransparent(getMainStage());
		Guis.addStylesheet(MiniSkin.MINI_MAIN_VIEW_STYLE, getMainStage());
	}
	
	@Override
	protected void doInitGraph() {
		mainPane = byId("main_pane");
		
		coverArtProgressBox = byId("cover_art_progress_box");
		StackPane coverArtBox = byId("cover_art_box");
		
		metadataLyricPane = byId("metadata_lyric_pane");
		HBox actionsBox = byId("actions_box");
		metadataBox = byId("metadata_box");
		lyricBox = byId("lyric_box");
		Label line1 = byId("line_1");
		Label line2 = byId("line_2");
		
		audioProgress = byId("audio_progress");
		coverArtLbl = byId("cover_art");
		coverAperture = byId("cover_aperture");
		Region logoMask = byId("logo_mask");
		Label logoBtn = byId("logo_btn");
		
		Label closeBtn = byId("close_btn");
		audioTitleLbl = byId("audio_title");
		audioArtistLbl = byId("audio_artist");
		
		playModeBtn = byId("playmode_btn");
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
		
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		Guis.addStyleClass("label-logo", logoBtn);
		Guis.setPickOnBounds(true, logoBtn, playModeBtn, playPrevBtn, 
				playBtn, playNextBtn, playlistBtn, closeBtn);
		
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
		
		Guis.addHoverAction(e -> {
			logoMask.setVisible(true);
			logoBtn.setVisible(true);
		}, e -> {
			logoMask.setVisible(false);
			logoBtn.setVisible(false);
		}, coverArtBox);
		
		logoMask.setOnMouseClicked(e -> {
			switchToSkin(SimpleSkin.NAME);
		});
		logoBtn.setOnMouseClicked(e -> {
			switchToSkin(SimpleSkin.NAME);
		});
		
		
		Guis.addDnmAction(getMainStage(), getMainStage().getScene().getRoot(), dnmOffset -> {
			Guis.applyStages(stage -> {
				if(stage instanceof Attachable) {
					((Attachable)stage).attach();
				}
			}, playlistView);
		}, coverArtProgressBox,playModeBtn, playPrevBtn, 
			playBtn, playNextBtn, playlistBtn, closeBtn);
		
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

	@Override
	protected void initEvents() {
		//开启热键
		enableHotKeys();
		//拖拽
		enableDndAction();
		//自动隐藏
		if(isEnableAutoDrawer()) {
			Guis.addAutoDrawerAction(getMainStage()).setOnHidden(e -> {
				Guis.ifPresent(playlistView, t -> playlistView.hide());
			}).setOnShown(e -> {
				Guis.ifPresent(playlistView, t -> playlistView.attach());
			});
		}
		
		//TODO
		Guis.addHoverAction(e -> {
			if(isPlaying()) {
				boolean visible = metadataLyricPane.isVisible();
				metadataLyricPane.setVisible(!visible);
			}
		}, e-> {
			metadataLyricPane.setVisible(isPlaying());
		}, getMainStage().getScene().getRoot());
	}
	
	@Override
	protected void initDatas() {
		//TODO
		setAppTitle();
		addIcons(Images.LOGO);
		updateMetadata();
		initPlaylistView();
		Guis.setAlwaysOnTop(alwaysOnTop, getMainStage());
		updatePlayModeBtn();
	}
	
	private void updatePlayModeBtn() {
		PlayMode playMode = getMediaPlayer().getPlayMode();
		boolean shuffle = (playMode == PlayMode.SHUFFLE);
		if(shuffle) {
			Guis.setImage(playModeBtn, Images.SHUFFLE, shuffle);
		} else {
			Guis.setImage(playModeBtn, Images.REPEAT, playMode.ordinal());
		}
		Guis.setUserData(playMode.ordinal(), playModeBtn);
		
	}
	
	//TODO
	@Override
	protected void onDndWaiting() {
		showMetadataBox(true);
		setInfoTextStyle(true);
		doUpdateMetadata(null, false, 
				"正在努力加载，",
				"请耐心等待~");
	}
	
	@Override
	protected void onDndAudioFileDone() {
		showMetadataBox(true);
		setInfoTextStyle(true);
		doUpdateMetadata(null, false,
				"加载已完成，",
				"正在努力识别~");
	}
	
	@Override
	protected void onDndFailed(String url) {
		resetPlaylistView();
		getMediaPlayer().resetPlaybackQueue(true);
		updateDndFailedText();
	}
	
	@Override
	protected void onDndSuccess(Image image) {
		updateCoverArt(image, false);
	}
	
	@Override
	protected boolean onDndLyricDone(String url) {
		return loadLyric(url);
	}
	
	private void updateNoMediaText() {
		showMetadataBox(true);
		setInfoTextStyle(true);
		doUpdateMetadata(null, false,
				"暂时无法播放哦",
				"试一试拖拽其他吧~");
	}
	
	private void updateDndFailedText() {
		showMetadataBox(true);
		setInfoTextStyle(true);
		doUpdateMetadata(Images.DND_NOT_FOUND, true,
				"暂时无法识别哦，",
				"试一试拖拽其他吧~");
		rotateAnim.resetRotate();
		coverAperture.setVisible(false);
	}
	
	private void doUpdateMetadata(Image cover, boolean applyTheme, String title, String artist) {
		updateCoverArt(cover, applyTheme);
		System.out.println(title + " : " + artist);
		audioTitleLbl.setText(StringUtil.getDefault(title, Constants.UNKOWN_AUDIO));
		audioArtistLbl.setText(StringUtil.getDefault(artist, Constants.UNKOWN_ARTIST));
	}

	//TODO
	private void initPlaylistView() {
		if(playlistView == null) {
			playlistView = new PlaylistView(getMainStage(), getMediaPlayer());
		}
		Stage mainStage = getMainStage();
		Guis.ifPresent(playlistView, t -> {
			playlistView.setTopVisible(false);
			playlistView.setWidth(mainStage.getWidth());
			playlistView.setRowWidth(379); //TODO
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

	@Override
	protected void updatePlaylist() {
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
		current = (current > 0 ? current : 0);
		double percent = duration > 0 ? current/duration : 0;
		double r = getProgressArcRadius();
		Arc arc = new Arc();
		arc.setRadiusX(r);
		arc.setRadiusY(r);
		arc.setCenterX(r);
		arc.setCenterY(r);
		arc.setStartAngle(90);
		arc.setLength(-360 * percent);
//		arc.setFill(Paint.valueOf("#1ca388"));
		arc.setType(ArcType.ROUND);
		audioProgress.setClip(arc);
		Guis.addStyleClass("theme-fg", audioProgress);
	}
	
	private double getProgressArcRadius() {
		return audioProgress.getWidth()/2 + 0.5;
	}
	
	public void updateMetadata() {
		updateMetadata(getCurrentAudio(), getCurrentMetadata());
	}
	
	public void updateMetadata(Audio audio, Map<String, Object> metadata) {
		if(audio == null || metadata == null) {
			updateCoverArt(null, false);
			return ;
		}
		String title = Metadatas.getTitle(metadata);
		String artist = Metadatas.getArtist(metadata);
		String album = Metadatas.getAlbum(metadata);
		Image image = Metadatas.getCoverArtImage(metadata);
		String extra = Metadatas.getExtra(metadata);
		
		//TODO 元数据可能会出现乱码
		image = image != null ? image : audio.getCoverArtImage();
		title = StringUtil.getDefault(title, 
				StringUtil.getDefault(audio.getTitle(), Constants.UNKOWN_AUDIO));
		artist = StringUtil.getDefault(artist, 
				StringUtil.getDefault(audio.getArtist(), Constants.UNKOWN_ARTIST));
		album = !StringUtil.isBlank(album)  ? album : audio.getAlbum();
		//迷你模式显示额外信息
		artist = StringUtil.isBlank(extra) ? artist : extra;
		
		setInfoTextStyle(false);
		doUpdateMetadata(image, false, title, artist);
	}
	
	public void updateCoverArt(Image image, boolean applyTheme) {
		//TODO
		Guis.toggleStyleClass(applyTheme, "theme-fg", coverArtLbl);
		Guis.toggleStyleClass(applyTheme, "cover-art-fix", coverArtProgressBox);
		
		useDefaultCoverArt = (image == null || image.isError());
		image = useDefaultCoverArt ? Images.DEFAULT_COVER_ART : image;
		ImageView graphic = new ImageView(image);
		final double size = MiniSkin.COVER_ART_SIZE;
		graphic.setFitWidth(size);
		graphic.setFitHeight(size);
		graphic.setSmooth(true);
//		graphic.setPreserveRatio(true);
		final double r = size/2;
		Circle circle = new Circle(r);
		circle.setCenterX(r);
		circle.setCenterY(r);
		graphic.setClip(circle);
		Guis.setGraphic(graphic, coverArtLbl);
		
		updateCoverAperture();
		updatePlayAnim();
	}
	
	private void updateCoverAperture() {
		boolean visible = false;
		if(isEnableCoverAperture()) {
			visible = !useDefaultCoverArt;
		} 
		coverAperture.setVisible(visible);
	}
	
	public void updatePlayBtn() {
		Guis.setUserData(Guis.setImage(playBtn, Images.PLAY, isPlaying()), 
				playBtn);
	}
	
	public void updatePlayAnim() {
		if(!isEnableAnim() || useDefaultCoverArt) {
			return ;
		}
		if(isPlaying()) {
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
		updatePlayBtn();
		updatePlayAnim();
	}

	@Override
	protected void updateOnReady(Audio audio, Map<String, Object> metadata) {
		updateMetadata(audio, metadata);
		loadLyric(audio);
		rotateAnim.resetRotate();
	}
	
	private void loadLyric(Audio audio) {
		//TODO
		if(audio != null) {
			String source = audio.getSource();
			int index = source.lastIndexOf(".");
			String uri = source.substring(0, index) + Resources.LYRIC_SUFFIXES[0];
			boolean hasLyric = loadLyric(uri);
			lyricBox.setVisible(hasLyric);
			metadataBox.setVisible(!hasLyric);
		} else {
			lyricBox.setVisible(false);
			metadataBox.setVisible(true);
		}
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
		updateNoMediaText();
	}
	
	private void showMetadataBox(boolean value) {
		metadataLyricPane.setVisible(value);
		metadataBox.setVisible(true);
		lyricBox.setVisible(false);
	}
	
	private void setInfoTextStyle(boolean value) {
		Guis.toggleStyleClass(value, "info-line", audioTitleLbl, audioArtistLbl);
	}

	@Override
	public void updateProgress(double current, double duration) {
		updateProgressBar(current, duration);
		updateLyric(current);
	}

	private void updateLyric(double current) {
		lyricRenderer.render(current);
	}
	
}
