package xyz.less.graphic;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class StageView extends Stage {
	protected Stage opener;
	private int count;
	
	public StageView(Stage opener) {
		this.opener = opener;
		initOwner(opener);
		initStyle(StageStyle.TRANSPARENT);
	}
	
	public StageView(Stage owner, double width, double height) {
		this.opener = owner;
		setWidth(width);
		setHeight(height);
		initOwner(owner);
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
	
	public void startCount() {
		count = 0;
	}
	
	public int getCount() {
		return count;
	}
	
	public void increaseCount() {
		++count;
	}
	
	protected abstract void initGraph();
}
