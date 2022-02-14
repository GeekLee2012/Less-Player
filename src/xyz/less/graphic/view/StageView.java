package xyz.less.graphic.view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.graphic.Guis;
import xyz.less.graphic.skin.Skin;

public abstract class StageView extends Stage implements Attachable {
	protected Stage opener;
	private int counter;
	protected boolean attach = true;
	
	public StageView(Stage opener, double width, double height) {
		this.opener = opener;
		setWidth(width);
		setMaxWidth(width);
		setHeight(height);
		setMaxHeight(height);
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
		initCommonStyle();
	}

	//TODO
	private void initCommonStyle() {
		addStyle(Skin.COMMON_STYLE);
		Parent root = getScene().getRoot();
		if(root instanceof Region) {
			Region rootRegion = (Region)root;
			rootRegion.getStyleClass().add("theme-bg");
		}
	}
	
	public void addStyle(String stylesheet) {
		Guis.addStylesheet(stylesheet, this);
	}
	
	/** Counter: start from 0 */
	protected void startCount() {
		startCount(0);
	}
	
	/** Counter: start from n */
	protected void startCount(int n) {
		this.counter = n;
	}
	
	/** Counter: current */
	protected int current() {
		return counter;
	}
	
	/** Counter: increase */
	protected void incCount() {
		++counter;
	}
	
	/** Counter: decrease */
	protected void decCount() {
		--counter;
	}
	
	/** Counter: end */
	protected void endCount() {
		counter = -1;
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

	@Override
	public void attach() {
		if(attach) {
			if(Guis.isMacOS()) {
				opener.sizeToScene(); //Fix a bug, also create another same bug
			}
			doAttach();
		}
	}

	protected void doAttach() {
		//TODO
	}
}
