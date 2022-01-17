package xyz.less.graphic.skin;

import javafx.stage.Stage;
import xyz.less.bean.AppContext;

//TODO
public final class SkinManager {
	
	public Skin getSkin(String name) {
		if(MiniSkin.NAME.equalsIgnoreCase(name)) {
			return new MiniSkin();
		}
		return new SimpleSkin();
	}
	
	//TODO
	public Skin switchToSkin(String name) {
		getMainStage().hide();
		Skin skin = getSkin(name);
		AppContext.get().setSkinName(skin.getName());
		getMainStage().setScene(skin.createRootScene());
		getMainStage().centerOnScreen();
		getMainStage().show();
		skin.init();
		skin.restore();
		return skin;
	}
	
	
	private Stage getMainStage() {
		return AppContext.get().getMainStage();
	}
}
