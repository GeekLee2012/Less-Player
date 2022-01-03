package xyz.less.graphic.skin;

import javafx.scene.Scene;
import xyz.less.graphic.view.PlayerView;

public abstract class PlayerSkin extends Skin {
	protected PlayerView playerView;
	
	public PlayerSkin(PlayerView playerView) {
		super(playerView.getMainStage());
		setPlayerView(playerView);
	}
	
	public PlayerView getPlayerView() {
		return playerView;
	}
	
	public void setPlayerView(PlayerView playerView) {
		this.playerView = playerView;
	}
	
	@Override
	public void init() {
		//TODO
		playerView.initGraph();
		playerView.playFromArgs();
	}
	
	@Override
	public Scene createRootScene() {
		return new Scene(playerView, 
				playerView.getWidth(), playerView.getHeight());
	}
}
