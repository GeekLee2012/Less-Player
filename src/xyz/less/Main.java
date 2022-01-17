package xyz.less;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.apiprovider.Exporter;
import xyz.less.async.AsyncServices;
import xyz.less.bean.AppContext;
import xyz.less.bean.Configuration;
import xyz.less.bean.Constants;
import xyz.less.graphic.Guis;
import xyz.less.graphic.skin.Skin;
import xyz.less.graphic.skin.SkinManager;
import xyz.less.rpc.RpcServer;

public final class Main extends Application {
	
	public static void initConfig(String[] args) {
		Configuration cfg = Configuration.parseFrom(args);
		AppContext.get().setConfiguration(cfg);
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		AppContext.get().setMainStage(primaryStage);
		initStage();
	}

	private void initStage() {
		Stage mainStage = AppContext.get().getMainStage();
		SkinManager skinMgr = AppContext.get().getSkinManager();
		Skin skin = skinMgr.getSkin(AppContext.get().getSkinName());
		mainStage.setScene(skin.getRootScene());
		mainStage.initStyle(StageStyle.TRANSPARENT);
		mainStage.setOnCloseRequest(e -> Guis.exitApplication());
		mainStage.show();
		skin.init();
		startRpcService();
	}
	
	private void startRpcService() {
		RpcServer server = new RpcServer(Constants.RPC_PORT);
		Runtime.getRuntime().addShutdownHook(new Thread(()-> {
			server.close();
		}));
		
//		Exporter.unExport(PlaylistApiProvider.class);
		Exporter.printProviderKeys();
		AsyncServices.submit(() -> {
			server.start();
		});
	}

	public static void main(String[] args) {
		initConfig(args);
		launch(args);
	}

}
