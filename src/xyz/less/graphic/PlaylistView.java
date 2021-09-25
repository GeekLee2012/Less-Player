package xyz.less.graphic;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import xyz.less.bean.Resources.Fxmls;
import xyz.less.bean.Resources.Images;
import xyz.less.bean.Resources.Styles;
import xyz.less.media.FxMediaPlayer;
import xyz.less.util.StringUtil;

public class PlaylistView extends StageView {
	private FxMediaPlayer mediaPlayer;
	
	private double openerX;
	private double openerY;
	
	private Label logoSizeLbl;
	private ListView<Node> listView;
	
	private boolean attach = true;
	private boolean lyricOn = false;
	private boolean autoTarget = true;
	
	public PlaylistView(Stage opener, FxMediaPlayer mediaPlayer) {
		super(opener, 366, 520);
		this.mediaPlayer = mediaPlayer;
		
		initGraph();
		initEvents();
	}

	private void initEvents() {
		setOnShowing(e -> {
			locate2Opener();
			//TODO
			highlightCurrentPlaying();
		});
		setOnHiding(e -> {
			opener.setX(openerX);
		});
	}

	protected void initGraph() {
		setSceneRoot(Guis.loadFxml(Fxmls.PLAYLIST_VIEW));
		addStyle(Styles.PLAYLIST_VIEW);
		
		initTop();
		initCenter();
	}

	private void initTop() {
		AnchorPane pane = byId("playlist_top");
		
		logoSizeLbl = byId("logo_size");
		Label attachBtn = byId("attach_btn");
		Label targetBtn = byId("target_btn");
		Label closeBtn = byId("close_btn");
		HBox winBtnsBox = byId("win_btns_box");
		
		
		AnchorPane.setLeftAnchor(logoSizeLbl, 6.0);
		AnchorPane.setRightAnchor(winBtnsBox, 2.0);
		
		ImageView logo = new ImageView(Images.PLAYLIST[1]);
		Guis.bind(logoSizeLbl.prefHeightProperty(), pane.prefHeightProperty());
		Guis.bind(winBtnsBox.prefHeightProperty(), pane.prefHeightProperty());
		
		Guis.setFitSize(24, logo);
		Guis.setAlignment(Pos.CENTER_LEFT, logoSizeLbl);
		Guis.setAlignment(Pos.CENTER, winBtnsBox);
		Guis.setPickOnBounds(true, attachBtn, targetBtn, closeBtn);
		
		Guis.setGraphic(logo, logoSizeLbl);
		Guis.setGraphic(new ImageView(Images.ATTACH[1]), attachBtn);
		Guis.setGraphic(new ImageView(Images.TARGET[1]), targetBtn);
		Guis.setGraphic(new ImageView(Images.CLOSE), closeBtn);
		Guis.setUserData(1, targetBtn, attachBtn);
		
		Guis.addStyleClass("top-nav", pane);
		Guis.addStyleClass("logo-label", logoSizeLbl);
		Guis.addStyleClass("label-btn", attachBtn, targetBtn, closeBtn);
		Guis.addHoverStyleClass("label-hover", attachBtn, targetBtn);
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		
		Guis.addDnmAction(this, pane, winBtnsBox);
		
		attachBtn.setOnMouseClicked(e -> {
			attach = !attach;
			Guis.toggleImage(attachBtn, Images.ATTACH);
		});
		
		targetBtn.setOnMouseClicked(e -> {
			autoTarget = !autoTarget;
			Guis.toggleImage(targetBtn, Images.TARGET);
			targetCurrentPlaying();
		});
		
		closeBtn.setOnMouseClicked(e -> {
			hide();
		});
	}

	private void initCenter() {
		listView = byId("list_view");
		updateGraph();
	}
	
	private int indexOf(Node node) {
		return listView.getItems().indexOf(node);
	}

	private AnchorPane createDataRow(String url) {
		Label titleLbl = new Label(StringUtil.decodeNameFromUrl(url));
		Label durationLbl = new Label(getDurationForm(url));
		durationLbl.setAlignment(Pos.CENTER);
		
		AnchorPane box = new AnchorPane(titleLbl, durationLbl);
		box.setPrefWidth(335);
		durationLbl.setPrefWidth(60);
		titleLbl.setPrefWidth(box.getPrefWidth() - durationLbl.getPrefWidth());
		
		AnchorPane.setLeftAnchor(titleLbl, 3.0);
		AnchorPane.setRightAnchor(durationLbl, 3.0);
		return box;
	}

	private String getDurationForm(String url) {
		String duration = "00:00";
		try {
			//TODO
		} catch(Exception e) {
			e.printStackTrace();
		}
		return duration;
	}

	public void targetCurrentPlaying() {
		if(autoTarget) {
			listView.scrollTo(mediaPlayer.getCurrentIndex());
		}
	}
 	
	public void highlightCurrentPlaying() {
		startCount();
		String styleClass = "current";
		listView.getItems().forEach(node -> {
			if(getCount() == mediaPlayer.getCurrentIndex()) {
				Guis.addStyleClass(styleClass, node);
			} else {
				Guis.removeStyleClass(styleClass, node);
			}
			increaseCount();
		});
		targetCurrentPlaying();
	}

	public int size() {
		return listView.getItems().size();
	}
	
	public void resetGraph(boolean forceClose) {
		listView.getItems().remove(0, size());
		updateLogoSizeLabelText();
		if(forceClose) {
			hide();
		}
	}

	public void updateGraph() {
		//TODO
		resetGraph(false);
		List<String> datas = mediaPlayer.getPlaylist().get();
		datas.forEach(url -> {
			Node node = createDataRow(url);
			Guis.setPickOnBounds(true, node);
			node.setOnMouseClicked(e -> {
				if(e.getClickCount() > 1) {
					mediaPlayer.play(indexOf(node));
				}
			});
			listView.getItems().add(node);
		});
		updateLogoSizeLabelText();
	}
	
	public void updateLogoSizeLabelText() {
		String size = size() > 0 ? size() + "首" : "";
		String text = String.format("当前播放 %1$s", size);
		logoSizeLbl.setText(text);
	}

	public void attach() {
		if(attach) {
			locate2Opener();
		}
	}
	
	private void locate2Opener() {
		openerX = opener.getX();
		openerY = opener.getY();
		double paddingX = 6;
		double paddingY = lyricOn ? 10 : 88;
		if(!attach) {
			opener.setX(openerX - getWidth() / 2 - paddingX);
		}
		setX(opener.getX() + opener.getWidth() + paddingX);
		setY(openerY - paddingY);
	}

	public void setLyricOn(boolean value) {
		this.lyricOn = value;
	}
	
}
