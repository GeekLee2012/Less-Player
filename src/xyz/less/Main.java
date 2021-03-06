package xyz.less;

import javafx.application.Application;
import javafx.stage.Stage;
import xyz.less.bean.AppContext;
import xyz.less.service.PluginsService;

public final class Main extends Application {
	
	@Override
	public void start(Stage mainStage) throws Exception {
		AppContext context = AppContext.get().initMainStage(mainStage);
		context.switchToSkin(context.getSkinName());
		PluginsService.start();
	}
	
	public static void main(String[] args) {
		AppContext.init(args);
		launch(args);
	}

}
