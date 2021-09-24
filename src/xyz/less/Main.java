package xyz.less;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.engine.SkinEngine;
import xyz.less.graphic.MainView;
import xyz.less.graphic.PlayerView;
import xyz.less.media.FxMediaPlayer;

public class Main extends Application {
	Stage mainStage;
	SkinEngine skinEngine;
	
	private void setMainStage(Stage stage) {
		this.mainStage = stage;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		setMainStage(primaryStage);
		
		initSkins();
		initStage();
	}
	
	private void initSkins() {
		//TODO
	}

	private void initStage() {
		FxMediaPlayer mediaPlayer = new FxMediaPlayer();
		PlayerView mainView = new MainView(mainStage, 666, 333);
		mediaPlayer.setPlayerView(mainView);
		mainView.setMediaPlayer(mediaPlayer);
		
		Scene rootScene = new Scene(mainView, mainView.getWidth(), mainView.getHeight());
		mainStage.setScene(rootScene);
		mainStage.initStyle(StageStyle.TRANSPARENT);
		mainStage.show();
		
		mainView.initGraph();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
