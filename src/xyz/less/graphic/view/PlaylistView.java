package xyz.less.graphic.view;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import xyz.less.bean.AppContext;
import xyz.less.bean.Audio;
import xyz.less.bean.Resources.Images;
import xyz.less.graphic.Guis;
import xyz.less.graphic.control.DnmAction;
import xyz.less.graphic.control.PlaylistItem;
import xyz.less.graphic.skin.SimpleSkin;
import xyz.less.service.IMediaService;

public class PlaylistView extends StageView {
	private IMediaService mediaService;
	
//	private double openerX;
//	private double openerY;
	
	private Label logoSizeLbl;
	private AnchorPane topPane;
	private ListView<Audio> listView;

	private boolean lyricOn = false;
	private boolean autoTarget = true;
	private double rowWidth = 335;
	public double durationWidth = 72;
	
	private Consumer<DnmAction.Pos> attachAction;
	
	public PlaylistView(Stage opener, double width, double height) {
		super(opener, width, height);
		this.mediaService = AppContext.get().getMediaService();
		initAttachAction();
		initGraph();
		initEvents();
	}
	
	private void initAttachAction() {
		setAttachAction(pos -> {
			double heightDist1 = getHeight() - opener.getHeight();
			double heightDist2 = heightDist1 - SimpleSkin.LYRIC_HEIGHT - SimpleSkin.LYRIC_PADDING_Y;
//			double paddingY = lyricOn ? 18 : 88;
			double paddingY = lyricOn ? heightDist2 / 2 : heightDist1 / 2;

			setX(pos.getX() + opener.getWidth() + SimpleSkin.PLAYLIST_PADDING_X);
			setY(pos.getY() - paddingY);
			
			//TODO fix a UI bug
			listView.layout();
		});
	}

	private void setAttach(boolean attach) {
		this.attach = attach;
	}
	
	public void setAttachAction(Consumer<DnmAction.Pos> attachAction) {
		this.attachAction = attachAction;
	}

	private void setAutoTarget(boolean autoTarget) {
		this.autoTarget = autoTarget;
	}
	
	private void initEvents() {
		setOnShowing(e -> {
//			attach();
			highlightCurrentPlaying();
		});
//		setOnHiding(e -> {
//			opener.setX(openerX);
//		});
	}

	protected void initGraph() {
		setSceneRoot(Guis.loadFxml(SimpleSkin.PLAYLIST_VIEW_FXML));
		initStyles();
		initTop();
		initCenter();
	}

	private void initStyles() {
		addStyle(SimpleSkin.PLAYLIST_VIEW_STYLE);
	}

	public void setTopVisible(boolean visible) {
		BorderPane pane = byId("playlist_view");
		pane.setTop(visible ? topPane : null);
	}
	
	private void initTop() {
		topPane = byId("playlist_top");
		
		logoSizeLbl = byId("logo_size");
		Label attachBtn = byId("attach_btn");
		Label targetBtn = byId("target_btn");
		Label closeBtn = byId("close_btn");
		HBox winBtnsBox = byId("win_btns_box");
		
		AnchorPane.setLeftAnchor(logoSizeLbl, 6.0);
		AnchorPane.setRightAnchor(winBtnsBox, 2.0);
		
		ImageView logo = new ImageView(Images.PLAYLIST[1]);
		Guis.bind(logoSizeLbl.prefHeightProperty(), topPane.prefHeightProperty());
		Guis.bind(winBtnsBox.prefHeightProperty(), topPane.prefHeightProperty());
		
		Guis.setFitSize(24, logo);
		Guis.setAlignment(Pos.CENTER_LEFT, logoSizeLbl);
		Guis.setAlignment(Pos.CENTER, winBtnsBox);
		Guis.setPickOnBounds(true, attachBtn, targetBtn, closeBtn);
		
		Guis.setGraphic(logo, logoSizeLbl);
		Guis.setGraphic(Images.ATTACH[1], attachBtn);
		Guis.setGraphic(Images.TARGET[1], targetBtn);
		Guis.setGraphic(Images.CLOSE, closeBtn);
		Guis.setUserData(1, targetBtn, attachBtn);
		
		Guis.addStyleClass("bottom-border-dark", topPane);
		Guis.addStyleClass("logo-label", logoSizeLbl);
		Guis.addStyleClass("label-btn", attachBtn, targetBtn, closeBtn);
		Guis.addHoverStyleClass("label-hover", attachBtn, targetBtn);
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		
		Guis.addDnmAction(this, topPane, winBtnsBox);
		
		attachBtn.setOnMouseClicked(e -> {
			setAttach(!attach);
			Guis.toggleImage(attachBtn, Images.ATTACH);
		});
		
		targetBtn.setOnMouseClicked(e -> {
			setAutoTarget(!autoTarget);
			Guis.toggleImage(targetBtn, Images.TARGET);
			targetCurrentPlaying();
		});
		
		closeBtn.setOnMouseClicked(e -> hide());
	}

	private void initCenter() {
		listView = byId("list_view");
		listView.setCellFactory(lv -> {
			PlaylistItem item = new PlaylistItem();
			item.setItemWidth(rowWidth);
//			item.setPaddingLeft(5);
			item.setTitleWidth(rowWidth - durationWidth - 10);
			item.setDurationWidth(durationWidth);
			item.setOnMouseClicked(e -> {
				if(e.getClickCount() > 1) {
					mediaService.setCurrent(item.getItem());
					mediaService.play();
				}
			});
			return item;
		});
		updateGraph();
	}
	
	private int indexOf(Audio item) {
		return listView.getItems().indexOf(item);
	}
	
	private int currentIndex() {
		return mediaService.getCurrentIndex();
	}
	
	public void targetCurrentPlaying() {
		if(autoTarget) {
			listView.scrollTo(currentIndex());
		}
	}
 	
	public void highlightCurrentPlaying() {
		//TODO
		listView.getItems().forEach(item -> {
			item.setPlaying(indexOf(item) == currentIndex());
		});
		targetCurrentPlaying();
		listView.refresh();
	}

	public int size() {
		return listView.getItems().size();
	}
	
	public void resetGraph(boolean hide) {
		if(size() > 0) {
			listView.getItems().remove(0, size());
		}
		updateLogoSizeLabelText();
		if(hide) {
			hide();
		}
	}

	public void updateGraph() {
		resetGraph(false);
		List<Audio> datas = mediaService.getPlaylist();
		listView.getItems().addAll(datas);
		updateLogoSizeLabelText();
	}
	
	public void updateLogoSizeLabelText() {
		String size = size() > 0 ? size() + "首" : "";
		String text = String.format("当前播放 %1$s", size);
		logoSizeLbl.setText(text);
	}

	@Override
	public void attach() {
		attach(lyricOn);
	}
	
	public void attach(boolean lyricOn) {
		this.lyricOn = lyricOn;
		super.attach();
	}

	@Override
	public void doAttach() {
		if(attachAction != null) {
			attachAction.accept(new DnmAction.Pos(opener.getX(), opener.getY()));
		}
	}
	
	public void setRowWidth(double rowWidth) {
		this.rowWidth = rowWidth;
	}
	
}
