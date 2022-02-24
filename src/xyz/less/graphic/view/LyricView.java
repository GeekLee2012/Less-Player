package xyz.less.graphic.view;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.bean.Audio;
import xyz.less.bean.Resources.Images;
import xyz.less.graphic.Guis;
import xyz.less.graphic.TwoLinesLyricRenderer;
import xyz.less.graphic.control.DnmAction;
import xyz.less.graphic.skin.SimpleSkin;
import xyz.less.media.LyricParser;

public class LyricView extends StageView {
//	private double openerX;
//	private double openerY;
	private Pane topNavBox;
	private Label line1;
	private Label line2;
	private LyricParser lyricParser = new LyricParser();
	private TwoLinesLyricRenderer lyricRenderer = new TwoLinesLyricRenderer();
	private boolean locked;
	private DnmAction dnmAction;
	private Label lockBtn;
	private Audio currentAudio;
	
	public LyricView(Stage opener, double width, double height) {
		super(opener, width, height);
		initGraph();
		initEvents();
	}
	
	public void setCurrentAudio(Audio currentAudio) {
		this.currentAudio = currentAudio;
	}

	@Override
	protected void initGraph() {
		setSceneRoot(Guis.loadFxml(SimpleSkin.LYRIC_VIEW_FXML));
		addStyle(SimpleSkin.LYRIC_VIEW_STYLE);
//		setSceneTransparent();

		initTop();
		initCenter();
	}
	
	private void initEvents() {
		setOnShowing(e -> attach());
	}

	private void initTop() {
		topNavBox = byId("top_nav");
		lockBtn = byId("lock_btn");
		Label pinBtn = byId("pin_btn");
		Label attachBtn = byId("attach_btn");
		Label closeBtn = byId("close_btn");

		Guis.setGraphic(Images.PIN[1], pinBtn);
		Guis.setGraphic(Images.ATTACH[1], attachBtn);
		Guis.setGraphic(Images.LOCK[0], lockBtn);
		Guis.setGraphic(Images.CLOSE, closeBtn);
		Guis.addStyleClass("label-btn", lockBtn, attachBtn, closeBtn);
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		Guis.setPickOnBounds(true, pinBtn, lockBtn, attachBtn, closeBtn);

		Guis.setAlwaysOnTop(true, this);
		Guis.setUserData(1, pinBtn, attachBtn);

		pinBtn.setOnMouseClicked(e -> {
			boolean alwaysOnTop = (Guis.toggleImage(pinBtn, Images.PIN) == 1);
			Guis.setAlwaysOnTop(alwaysOnTop, this);
		});

		attachBtn.setOnMouseClicked(e -> {
			toggleAttach();
			Guis.toggleImage(attachBtn, Images.ATTACH);
		});
		
		lockBtn.setOnMouseClicked(e -> {
			locked = !locked;
			Guis.toggleImage(lockBtn, Images.LOCK);
			lockLyricView();
		});
		
		closeBtn.setOnMouseClicked(e -> hide());

		dnmAction = Guis.addDnmAction(this, topNavBox, pinBtn, attachBtn, lockBtn, closeBtn);
	}

	private void initCenter() {
		Pane pane = byId("center_content");
		
		line1 = byId("line_1");
		line2 = byId("line_2");
		
		Guis.applyChildren(node -> {
			Label lbl = (Label) node;
			Guis.bind(lbl.prefWidthProperty(), pane.widthProperty());
			Guis.bind(lbl.prefHeightProperty(), pane.heightProperty().divide(2D));
		}, pane);
		
		//TODO
		lyricRenderer.setLines(line1, line2);
	}
	
	public void loadLyric(Audio audio) {
		if(audio != null) {
			setCurrentAudio(audio);
			loadLyric(currentAudio.getSource());
		} else {
			lyricRenderer.reset();
			lyricRenderer.showNoLyric();
		}
	}
	
	public boolean loadLyric(String uri) {
		try {
			//TODO
			lyricRenderer.reset();
			int index = uri.lastIndexOf(".");
			uri = uri.substring(0, index) + ".lrc";
			return lyricRenderer.setLyric(lyricParser.parse(uri));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void updateGraph(double currentMinutes) {
		lyricRenderer.render(currentMinutes);
	}
	
	private void lockLyricView() {
		dnmAction.enable(!locked);
	}

	@Override
	public void doAttach() {
		double y = opener.getY() + opener.getHeight() + SimpleSkin.LYRIC_PADDING_Y;
		setX(opener.getX());
		setY(y);
//		if (Guis.isMacOS()) {
//			sizeToScene();
//		}
	}
}
