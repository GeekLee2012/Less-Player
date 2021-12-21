package xyz.less.graphic.views;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.bean.Audio;
import xyz.less.bean.ConfigConstant;
import xyz.less.bean.Resources.Fxmls;
import xyz.less.bean.Resources.Images;
import xyz.less.bean.Resources.Styles;
import xyz.less.graphic.Guis;
import xyz.less.graphic.action.DnmAction;
import xyz.less.media.LyricParser;

public class LyricView extends StageView {
	private Stage owner;
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
	private boolean attach = true;
	private Audio currentAudio;
	
	public LyricView(Stage opener) {
		super(opener, ConfigConstant.LYRIC_WIDTH, ConfigConstant.LYRIC_HEIGHT);
		hideTaskBarIcon();
		initGraph();
		initEvents();
	}
	
	public void setCurrentAudio(Audio currentAudio) {
		this.currentAudio = currentAudio;
	}

	private void hideTaskBarIcon() {
		//TODO Bugs
		owner = new Stage();
		owner.initStyle(StageStyle.UTILITY);
		owner.setOpacity(0);
		owner.show();
		initOwner(owner);
	}

	@Override
	protected void initGraph() {
		setSceneRoot(Guis.loadFxml(Fxmls.LYRIC_VIEW));
		addStyle(Styles.LYRIC_VIEW);
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
		Label attachBtn = byId("attach_btn");
		Label closeBtn = byId("close_btn");
		
		Guis.setGraphic(Images.ATTACH[1], attachBtn);
		Guis.setGraphic(Images.LOCK[0], lockBtn);
		Guis.setGraphic(Images.CLOSE, closeBtn);
		Guis.addStyleClass("label-btn", lockBtn, attachBtn, closeBtn);
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		Guis.setPickOnBounds(true, lockBtn, attachBtn, closeBtn);
		
		Guis.setUserData(1, attachBtn);
		
		dnmAction = Guis.addDnmAction(this, topNavBox, closeBtn);
		
		attachBtn.setOnMouseClicked(e -> {
			attach = !attach;
			Guis.toggleImage(attachBtn, Images.ATTACH);
		});
		
		lockBtn.setOnMouseClicked(e -> {
			locked = !locked;
			Guis.toggleImage(lockBtn, Images.LOCK);
			lockLyricView();
		});
		
		closeBtn.setOnMouseClicked(e -> hide());
		//TODO
		Guis.addHoverStyleClass("theme-bg", getScene().getRoot());
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
		}
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

	public void updateGraph(double currentMinutes) {
		lyricRenderer.render(currentMinutes);
	}
	
	//TODO
	@SuppressWarnings("unused")
	private void setLyricViewTransparent(boolean value) {
		if(value) {
			getScene().setFill(null);
			Guis.applyChildren(node -> {
				node.setVisible(false);
			}, topNavBox);
			if(locked) {
				lockBtn.setVisible(true);
			}
		} else if(!locked){
			getScene().setFill(Paint.valueOf("#313131"));
			Guis.applyChildren(node -> {
				node.setVisible(true);
			}, topNavBox);
			Guis.addHoverStyleClass("label-hover", lockBtn);
		}
	}
	
	private void lockLyricView() {
		if(locked) {
//			setLyricViewTransparent(true);
			dnmAction.enable(false);
		} else {
			dnmAction.enable(true);
		}
	}
	
	@Override
	public void attach() {
		if(attach) {
			locate2Opener();
		}
	}
	
	private void locate2Opener() {
//		openerX = opener.getX();
		setX(opener.getX());
		setY(opener.getY() + opener.getHeight() + ConfigConstant.LYRIC_PADDING_Y);
	}
}
