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
		reloadMainStage(skin);
		skin.init();
		skin.restore();
		return skin;
	}
	
	
	private void reloadMainStage(Skin skin) {
		Stage mainStage = AppContext.get().getMainStage();
		mainStage.hide();
		mainStage.setScene(skin.getRootScene());
		mainStage.centerOnScreen();
		mainStage.show();
		AppContext.get().setSkinName(skin.getName());
	}
}
