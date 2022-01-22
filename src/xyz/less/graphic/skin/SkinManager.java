package xyz.less.graphic.skin;

import java.util.HashMap;
import java.util.Map;

import javafx.stage.Stage;
import xyz.less.bean.AppContext;
import xyz.less.util.StringUtil;

//TODO
public final class SkinManager {
	private Map<String, Skin> cachedSkins = new HashMap<>();
	
	public Skin getSkin(String name) {
		name = tranformName(name);
		Skin cached = cachedSkins.get(name);
		if(cached == null) {
			cached = getInstance(name);
			cachedSkins.put(cached.getName(), cached);
		}
		return cached;
	}
	
	/**
	 * 转换为合法的SkinName
	 */
	private String tranformName(String name) {
		name = StringUtil.trim(name);
		if(MiniSkin.NAME.equalsIgnoreCase(name)) {
			return MiniSkin.NAME;
		}
		return SimpleSkin.NAME;
	}

	private Skin getInstance(String name) {
		if(MiniSkin.NAME.equalsIgnoreCase(name)) {
			return new MiniSkin();
		}
		return new SimpleSkin();
	}

	public Skin switchToSkin(String name) {
		Skin skin = getSkin(name);
		return skin.load(reloadMainStage(skin));
	}
	
	private boolean reloadMainStage(Skin skin) {
		Stage mainStage = AppContext.get().getMainStage();
		boolean isReload = (mainStage.getScene() != null);
		if(isReload) {
			mainStage.hide();
		}
		mainStage.setScene(skin.getRootScene());
		mainStage.centerOnScreen();
		mainStage.show();
		AppContext.get().setSkinName(skin.getName());
		return isReload;
	}
}
