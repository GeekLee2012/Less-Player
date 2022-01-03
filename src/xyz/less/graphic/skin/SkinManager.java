package xyz.less.graphic.skin;

public final class SkinManager {
	
	//TODO
	public Skin getSkin(String name) {
		if(MiniSkin.NAME.equalsIgnoreCase(name)) {
			return new MiniSkin();
		}
		return new SimpleSkin();
	}
	
}
