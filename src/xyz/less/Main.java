package xyz.less;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.bean.AppContext;
import xyz.less.bean.Configuration;
import xyz.less.graphic.Guis;
import xyz.less.rpc.RpcService;

public final class Main extends Application {
	
	@Override
	public void start(Stage mainStage) throws Exception {
		mainStage.initStyle(StageStyle.TRANSPARENT);
		mainStage.setOnCloseRequest(e -> Guis.exitApplication());
		AppContext context = AppContext.get();
		context.setMainStage(mainStage);
		context.switchToSkin(context.getSkinName());
		RpcService.start();
	}
	
	private static void initContext(String[] args) {
		Configuration cfg = Configuration.parseFrom(args);
		AppContext.get().setConfiguration(cfg);
	}
	
	public static void main(String[] args) {
		initContext(args);
		launch(args);
	}

}
