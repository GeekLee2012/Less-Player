package xyz.less.graphic.skin;

import java.net.URL;

import xyz.less.bean.Resources;
import xyz.less.graphic.view.mini.MainView;

/**
 * Mini风格
 */
public class MiniSkin extends PlayerSkin {
	public static final String NAME = "mini";
	
	public static final URL MINI_MAIN_VIEW_FXML = Resources.fxml("mini/main_view");
	public static final String MINI_MAIN_VIEW_STYLE = Resources.css("mini/main_view");
	public static final double COVER_ART_SIZE = 87;
	
	public MiniSkin() {
		//TODO
		super(NAME, new MainView(410, 120));
	}
	
}
