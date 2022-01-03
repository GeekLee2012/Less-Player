package xyz.less;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.bean.AppContext;
import xyz.less.bean.ArgsBean;
import xyz.less.graphic.Guis;
import xyz.less.graphic.skin.Skin;
import xyz.less.graphic.skin.SkinManager;

public final class Main extends Application {
	private static AppContext appContext;
	
	public static void initAppContext(String[] args) {
		getAppContext().setArgsBean(new ArgsBean(args).parse());
	}
	
	public static AppContext getAppContext() {
		if(appContext == null) {
			appContext = new AppContext();
		}
		return appContext;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		getAppContext().setMainStage(primaryStage);
		initStage();
	}

	private void initStage() {
		Stage mainStage = appContext.getMainStage();
		SkinManager skinMgr = new SkinManager();
		Skin skin = skinMgr.getSkin(appContext.getSkinName());
		mainStage.setScene(skin.getRootScene());
		mainStage.initStyle(StageStyle.TRANSPARENT);
		mainStage.setOnCloseRequest(e -> Guis.exitApplication());
		mainStage.show();
		skin.init();
	}

	public static void main(String[] args) {
		initAppContext(args);
		launch(args);
	}

}
