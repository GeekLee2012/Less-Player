package xyz.less.graphic.views;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.graphic.Guis;

public abstract class StageView extends Stage implements Attachable {
	protected Stage opener;
	private int count;
	
	public StageView(Stage opener) {
		this(opener, -1, -1);
	}
	
	public StageView(Stage opener, double width, double height) {
		this.opener = opener;
		setWidth(width);
		setHeight(height);
		initOwner(opener);
		initStyle(StageStyle.TRANSPARENT);
	}
	
	public <T> T byId(String id) {
		return Guis.byId(id, this);
	}
	
	public <T> T bySelector(String selector) {
		return Guis.bySelector(selector, this);
	}
	
	public void setSceneRoot(Parent parent) {
		setScene(new Scene(parent));
	}
	
	public void setSceneTransparent() {
		getScene().setFill(null);
	}
	
	public void addStyle(String stylesheet) {
		Guis.addStylesheet(stylesheet, this);
	}
	
	protected void startCount() {
		count = 0;
	}
	
	protected int getCount() {
		return count;
	}
	
	protected void increaseCount() {
		++count;
	}
	
	protected abstract void initGraph();
	
	public boolean toggle() {
		if(isShowing()) {
			hide();
		} else {
			show();
		}
		return isShowing();
	}
	
	public void attach() {
		
	}
}
