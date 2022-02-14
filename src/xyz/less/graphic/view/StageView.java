package xyz.less.graphic.view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.graphic.Guis;

public abstract class StageView extends Stage implements Attachable {
	protected Stage opener;
	private int counter;
	protected boolean attach = true;
	
	public StageView(Stage opener) {
		this(opener, -1, -1);
	}
	
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
	}
	
	public void setSceneTransparent() {
		getScene().setFill(null);
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
			locate2Opener();
		}
	}

	protected void locate2Opener() {
		//TODO
	}
}
