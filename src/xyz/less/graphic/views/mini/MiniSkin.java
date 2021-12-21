package xyz.less.graphic.views.mini;

import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.less.graphic.views.PlayerView;
import xyz.less.graphic.views.Skin;

public class MiniSkin extends Skin {
	private PlayerView playerView;
	
	public MiniSkin(Stage mainStage) {
		super(mainStage);
		setPlayerView(new MainView(this.mainStage, 410, 120));
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
		//TODO
	}
	
	@Override
	public Scene createRootScene() {
		return new Scene(playerView, 
				playerView.getWidth(), playerView.getHeight());
	}
}
