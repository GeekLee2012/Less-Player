package xyz.less.graphic.skin;

import java.util.HashMap;
import java.util.Map;

import javafx.stage.Stage;
import xyz.less.bean.AppContext;

//TODO
public final class SkinManager {
	private Map<String, Skin> cachedSkins = new HashMap<>();
	
	public Skin getSkin(String name) {
		Skin cached = cachedSkins.get(name);
		if(cached == null) {
			if(MiniSkin.NAME.equalsIgnoreCase(name)) {
				cached = new MiniSkin();
			} else {
				cached = new SimpleSkin(); 
			}
			cachedSkins.put(name, cached);
		}
		return cached;
	}
	
	//TODO
	public Skin switchToSkin(String name) {
		Skin skin = getSkin(name);
		return skin.load(reloadMainStage(skin));
	}
	
	
	private boolean reloadMainStage(Skin skin) {
		Stage mainStage = AppContext.get().getMainStage();
		if(mainStage.isShowing()) {
			mainStage.hide();
		}
		boolean isReload = (mainStage.getScene() != null);
		mainStage.setScene(skin.getRootScene());
		mainStage.centerOnScreen();
		mainStage.show();
		AppContext.get().setSkinName(skin.getName());
		return isReload;
	}
}
