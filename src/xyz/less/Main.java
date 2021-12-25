package xyz.less;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.bean.ArgsBean;
import xyz.less.graphic.Guis;
import xyz.less.graphic.skin.Skin;
import xyz.less.graphic.skin.SkinManager;

public final class Main extends Application {
	private static ArgsBean argsBean;
	private Stage mainStage;
	
	public static void parseArgs(String[] args) {
		argsBean = new ArgsBean(args).parse();
	}

	private void setMainStage(Stage stage) {
		this.mainStage = stage;
		this.mainStage.setUserData(argsBean);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		setMainStage(primaryStage);
		initStage();
	}

	private void initStage() {
		SkinManager skinMgr = new SkinManager(mainStage);
		Skin skin = skinMgr.getSkin(argsBean.getSkinName());
		mainStage.setScene(skin.getRootScene());
		mainStage.initStyle(StageStyle.TRANSPARENT);
		mainStage.setOnCloseRequest(e -> Guis.exitApplication());
		mainStage.show();
		skin.init();
	}

	public static void main(String[] args) {
		parseArgs(args);
		launch(args);
	}
	
}
