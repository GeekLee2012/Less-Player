package xyz.less;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.graphic.Guis;
import xyz.less.graphic.views.SimpleSkin;
import xyz.less.graphic.views.Skin;
import xyz.less.graphic.views.mini.MiniSkin;

public final class Main extends Application {
	private static String[] args;
	private Stage mainStage;
	
	public static void setArgs(String[] args) {
		Main.args = args;
	}

	private void setMainStage(Stage stage) {
		this.mainStage = stage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		setMainStage(primaryStage);
		initStage();
	}

	private void initStage() {
		Skin skin = new SimpleSkin(mainStage);
		//Skin skin = new MiniSkin(mainStage);
		mainStage.setUserData(args);
		mainStage.setScene(skin.getRootScene());
		mainStage.initStyle(StageStyle.TRANSPARENT);
		mainStage.setOnCloseRequest(e -> Guis.exitApplication());
		mainStage.show();
		skin.init();
	}
	
	public static void main(String[] args) {
		setArgs(args);
		launch(args);
	}
	
}
