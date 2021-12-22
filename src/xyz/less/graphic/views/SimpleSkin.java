package xyz.less.graphic.views;

import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.less.bean.ConfigConstant;

public final class SimpleSkin extends Skin {
	private PlayerView playerView;
	
	public SimpleSkin(Stage mainStage) {
		super(mainStage);
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
		playerView.playFromArgs();
		//TODO
	}

	@Override
	public Scene createRootScene() {
		return new Scene(playerView, 
				playerView.getWidth(), playerView.getHeight());
	}
}
