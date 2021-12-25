package xyz.less.graphic.skin;

import javafx.stage.Stage;
import xyz.less.graphic.view.mini.MainView;

/**
 * Mini风格
 */
public class MiniSkin extends PlayerSkin {
	public static final String NAME = "mini";
	
	public MiniSkin(Stage mainStage) {
		//TODO
		super(new MainView(mainStage, 410, 120));
	}
	
}
