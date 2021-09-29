package xyz.less.graphic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.bean.Audio;
import xyz.less.bean.ConfigConstant;
import xyz.less.bean.Lyric;
import xyz.less.bean.Resources.Fxmls;
import xyz.less.bean.Resources.Images;
import xyz.less.bean.Resources.Styles;
import xyz.less.graphic.action.DnmAction;
import xyz.less.media.LyricParser;
import xyz.less.util.DateUtil;

public class LyricView extends StageView {
	private Stage owner;
//	private double openerX;
//	private double openerY;
	private Pane topNavBox;
	private Label line1;
	private Label line2;
	private Lyric lyric;
	private LyricParser lyricParser = new LyricParser();
	private boolean locked;
	private DnmAction dnmAction;
	private Label lockBtn;
	private Label highlightLbl; 
	private List<String> timeKeyList = new ArrayList<>();
	private String line1TimeKey;
	private String line2TimeKey;
	private String line3TimeKey;
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
//		setSceneTransparent();
		addStyle(Styles.LYRIC_VIEW);
		
		initTop();
		initCenter();
	}
	
	private void initEvents() {
		setOnShowing(e -> {
			attach();
		});
		//TODO
		getScene().setOnMouseEntered(e -> {
//			setLyricViewTransparent(false);
		});
		
		getScene().setOnMouseExited(e -> {
//			setLyricViewTransparent(true);
		});
	}

	private void initTop() {
		topNavBox = byId("top_nav");
		lockBtn = byId("lock_btn");
		Label attachBtn = byId("attach_btn");
		Label closeBtn = byId("close_btn");
		
		Guis.setGraphic(new ImageView(Images.ATTACH[1]), attachBtn);
		Guis.setGraphic(new ImageView(Images.LOCK[0]), lockBtn);
		Guis.setGraphic(new ImageView(Images.CLOSE), closeBtn);
		Guis.addStyleClass("label-btn", lockBtn, attachBtn, closeBtn);
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		Guis.setPickOnBounds(true, lockBtn, attachBtn, closeBtn);
		
		Guis.setUserData(1, attachBtn);
		
		dnmAction = Guis.addDnmAction(this, getScene().getRoot(), closeBtn);
		
		attachBtn.setOnMouseClicked(e -> {
			attach = !attach;
			Guis.toggleImage(attachBtn, Images.ATTACH);
		});
		
		lockBtn.setOnMouseClicked(e -> {
			locked = !locked;
			Guis.toggleImage(lockBtn, Images.LOCK);
			lockLyricView();
		});
		
		closeBtn.setOnMouseClicked(e -> {
			hide();
		});
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
		
		Guis.addStyleClass("lyric-line", line1, line2);
	}
	
	private void resetLyric() {
		line1TimeKey = null;
		line2TimeKey = null;
		line3TimeKey = null;
		timeKeyList.clear();
	}
	
	public void loadLyric(Audio audio) {
		if(audio != null) {
			setCurrentAudio(audio);
			loadLyric(currentAudio.getSource());
		}
	}
	
	public void loadLyric(String uri) {
		try {
			resetLyric();
			int index = uri.lastIndexOf(".");
			uri = uri.substring(0, index) + ".lrc";
			lyric = lyricParser.parse(uri);
			if(lyric != null) {
				timeKeyList.addAll(lyric.getDatas().keySet());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean hasLyricDatas() {
		return timeKeyList.size() > 0;
	}

	public void updateGraph(double currentMinutes) {
		if(!hasLyricDatas()) {
			setNoLyricText();
			return ;
		}
		highlightLbl = line1;
		if(line1TimeKey == null && line2TimeKey == null) {
			updateLyricText(currentMinutes);
		} else {
			double time1 = DateUtil.toMinutes(line1TimeKey);
			double time2 = DateUtil.toMinutes(line2TimeKey);
			double time3 = DateUtil.toMinutes(line3TimeKey);
			if(currentMinutes < time1) {
				updateLyricText(currentMinutes);
			} else if(currentMinutes < time2) {
				highlightLbl = line1;
			} else if(currentMinutes < time3) {
				highlightLbl = line2;
			} else {
				updateLyricText(currentMinutes);
			}
		}
		highlightLyric();
	}

	private void highlightLyric() {
		Label[] lines = { line1, line2 };
		Arrays.asList(lines).forEach(line -> {
			if(line == highlightLbl) {
				Guis.addStyleClass("lyric-line-current", line);
			} else {
				Guis.removeStyleClass("lyric-line-current", line);
			}
		});
	}

	private void updateLyricText(double currentMinutes) {
		int currentIndex = getLyricCurrentIndex(currentMinutes);
		line1TimeKey = timeKeyList.get(currentIndex);
		if(currentIndex <= timeKeyList.size() - 3) {
			line2TimeKey = timeKeyList.get(currentIndex + 1);
			line3TimeKey = timeKeyList.get(currentIndex + 2);
			
		} else if(currentIndex <= timeKeyList.size() - 2) {
			line2TimeKey = timeKeyList.get(currentIndex + 1);
			line3TimeKey = ConfigConstant.INFINITED_TIME_KEY;
		} else if(currentIndex <= timeKeyList.size() - 1) {
			line2TimeKey = ConfigConstant.INFINITED_TIME_KEY;
			line3TimeKey = ConfigConstant.INFINITED_TIME_KEY;
		}
		line1.setText(lyric.getLine(line1TimeKey));
		line2.setText(lyric.getLine(line2TimeKey));
	}

	private int getLyricCurrentIndex(double currentMinutes) {
		double offsetMinutes = lyric.getOffset() / 1000.0D / 60.0D;
		int size = timeKeyList.size();
		for(int i = 0; i < size; i++) {
			String key = timeKeyList.get(i);
			double minutes = DateUtil.toMinutes(key);
			double lyricTime = minutes + offsetMinutes;
			if (currentMinutes < lyricTime) {
				return i > 1 ? i - 1 : 0; 
			}
		}
		return size > 1 ? size - 1 : 0;
	}

	private void setNoLyricText() {
		line1.setText("ÔÝÊ±Ã»·¢ÏÖ¸è´Ê");
		line2.setText("Çë¼ÌÐøÐÀÉÍÒôÀÖ°É~");
		Guis.setUserData(null, line1, line2);
		
		Guis.addStyleClass("lyric-line-current", line1, line2);
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
	
	public void attach() {
		if(attach) {
			locate2Opener();
		}
	}
	
	private void locate2Opener() {
//		openerX = opener.getX();
		double padding = 6;
		setX(opener.getX());
		setY(opener.getY() + opener.getHeight() + padding);
	}
}
