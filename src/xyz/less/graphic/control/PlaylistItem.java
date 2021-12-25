package xyz.less.graphic.control;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import xyz.less.bean.Audio;
import xyz.less.graphic.Guis;
import xyz.less.util.StringUtil;

public class PlaylistItem extends ListCell<Audio> {
	private AnchorPane itemBox;
	private Label titleLbl;
	private Label durationLbl;
	private String styleClass = "current";
	
	public PlaylistItem() {
		titleLbl = new Label();
		durationLbl = new Label();
		titleLbl.setAlignment(Pos.CENTER_LEFT);
		durationLbl.setAlignment(Pos.CENTER_RIGHT);
		
		itemBox = new AnchorPane(titleLbl, durationLbl);
		Guis.addStyleClass("item-box", itemBox);
		AnchorPane.setLeftAnchor(titleLbl, 3D);
		AnchorPane.setRightAnchor(durationLbl, 3D);
	}
	
	public void setItemWidth(double width) {
		itemBox.setPrefWidth(width);
	}
	
	public void setTitleWidth(double width) {
		titleLbl.setPrefWidth(width);
	}
	
	public void setDurationWidth(double width) {
		durationLbl.setPrefWidth(width);
	}
	
	public void setPaddingX(double value) {
		AnchorPane.setLeftAnchor(titleLbl, value);
		AnchorPane.setRightAnchor(durationLbl, value);
	}
	
	public void setStyleClass(String value) {
		this.styleClass = value;
	}
	
	@Override
	protected void updateItem(Audio item, boolean empty) {
		super.updateItem(item, empty);
		if(empty || item == null) {
			setGraphic(null);
			setText(null);
		} else {
			setTitle(item.getTitle());
			setDuration(item.getDuration());
			setGraphic(itemBox);
			Guis.toggleStyleClass(item.isPlaying(), styleClass, this);
		}
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
	
}
