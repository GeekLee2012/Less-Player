package xyz.less.graphic.skin;

import javafx.stage.Stage;
import xyz.less.bean.ConfigConstant;
import xyz.less.graphic.view.MainView;

/**
 * 普通风格
 */
public final class SimpleSkin extends PlayerSkin {
	public static final String NAME = "simple";
	
	public SimpleSkin(Stage mainStage) {
		super(new MainView(mainStage, 
				ConfigConstant.APP_WIDTH, ConfigConstant.APP_HEIGHT));
	}
	
}
