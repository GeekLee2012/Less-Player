package xyz.less.graphic.skin;

import javafx.stage.Stage;

public final class SkinManager {
	private Stage mainStage;
	
	public SkinManager(Stage mainStage) {
		this.mainStage = mainStage;
	}
	
	//TODO
	public Skin getSkin(String name) {
		if(MiniSkin.NAME.equalsIgnoreCase(name)) {
			return new MiniSkin(mainStage);
		}
		return new SimpleSkin(mainStage);
	}
	
}
