package xyz.less.graphic.control;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import xyz.less.bean.Audio;
import xyz.less.graphic.Guis;
import xyz.less.util.StringUtil;

//TODO
public class PlaylistItem extends ListCell<Audio> {
	private Node graphicNode;
	private AnchorPane itemBox;
	private Label titleLbl;
	private Label durationLbl;
	
	private String styleClass = "current";
	private double paddingLeft = 1;
	private double paddingRight = 3;
	
	public PlaylistItem() {
		initGraph();
	}
	
	protected void initGraph() {
		titleLbl = new Label();
		durationLbl = new Label();
		titleLbl.setAlignment(Pos.CENTER_LEFT);
		durationLbl.setAlignment(Pos.CENTER_RIGHT);
		
		itemBox = new AnchorPane(titleLbl, durationLbl);
		Guis.addStyleClass("item-box", itemBox);
		layoutGraph();
		setGraphicNode(itemBox);
	}
	
	public void setGraphicNode(Node node) {
		this.graphicNode = node;
	}
	
	protected void layoutGraph() {
		AnchorPane.setLeftAnchor(titleLbl, paddingLeft);
		AnchorPane.setRightAnchor(durationLbl, paddingRight);
	}
	
	protected void updateGraph(Audio item) {
		setTitle(item.getTitle());
		setDuration(item.getDuration());
		Guis.toggleStyleClass(item.isPlaying(), styleClass, this);
	}
	
	public void setItemWidth(double width) {
		itemBox.setPrefWidth(width);
	}
	
	public void setTitleWidth(double width) {
		titleLbl.setMaxWidth(width);
	}
	
	public void setDurationWidth(double width) {
		durationLbl.setPrefWidth(width);
	}
	
	public void setPaddingX(double padding) {
		paddingLeft = padding;
		paddingRight = padding;
		layoutGraph();
	}
	
	public void setPaddingLeft(double left) {
		paddingLeft = left;
		layoutGraph();
	}
	
	public void setPaddingRight(double right) {
		paddingRight = right;
		layoutGraph();
	}
	
	public void setStyleClass(String value) {
		this.styleClass = value;
	}
	
	public void setTitle(String title) {
		titleLbl.setText(title);
	}
	
	public void setDuration(double duration) {
		setDuration(StringUtil.toMmss(duration));
	}
	
	public void setDuration(String duration) {
		durationLbl.setText(duration);
	}
	
	@Override
	protected void updateItem(Audio item, boolean empty) {
		super.updateItem(item, empty);
		if(empty || item == null) {
			setGraphic(null);
			setText(null);
		} else {
			updateGraph(item);
			setGraphic(graphicNode);
		}
	}

}
