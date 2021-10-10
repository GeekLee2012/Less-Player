package xyz.less.graphic.skins.simple;

import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.less.bean.ConfigConstant;
import xyz.less.graphic.PlayerView;
import xyz.less.graphic.skins.Skin;

//TODO
public final class SimpleSkin implements Skin {
	private Stage mainStage;
	private PlayerView playerView;
	
	public SimpleSkin(Stage mainStage) {
		this.mainStage = mainStage;
		setPlayerView(new MainView(this.mainStage, 
				ConfigConstant.APP_WIDTH, ConfigConstant.APP_HEIGHT));
	}
	
	public PlayerView getPlayerView() {
		return playerView;
	}
	
	public void setPlayerView(PlayerView playerView) {
		this.playerView = playerView;
	}

	@Override
	public void init() {
		playerView.initGraph();
	}

	@Override
	public Scene getRootScene() {
		return new Scene(playerView, 
				playerView.getWidth(), playerView.getHeight());
	}
}