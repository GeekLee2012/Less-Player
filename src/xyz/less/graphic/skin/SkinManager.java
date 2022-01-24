package xyz.less.graphic.skin;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javafx.stage.Stage;
import xyz.less.bean.AppContext;
import xyz.less.util.StringUtil;

//TODO
public final class SkinManager {
	private Map<String, Skin> cachedSkins = new HashMap<>();
	private Map<String, Class<? extends Skin>> skinClsMap = new TreeMap<>();
	
	public SkinManager() {
		skinClsMap.put(SimpleSkin.NAME, SimpleSkin.class);
		skinClsMap.put(MiniSkin.NAME, MiniSkin.class);
	}
	
	public Skin getSkin(String name) {
		name = transformName(name);
		Skin cached = cachedSkins.get(name);
		if(cached == null) {
			cached = getInstance(name);
			cachedSkins.put(cached.getName(), cached);
//			cachedSkins.put(cached.getClass().getName(), cached);
		}
		return cached;
	}
	
	/** 转换为合法名称 */
	private String transformName(String name) {
		name = StringUtil.trim(name);
		for(String key : skinClsMap.keySet()) {
			if(key.equalsIgnoreCase(name)) {
				return key;
			}
		}
		return SimpleSkin.NAME;
	}

	private Skin getInstance(String name) {
		Class<? extends Skin> skinCls = skinClsMap.get(name);
		try {
			return skinCls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
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
