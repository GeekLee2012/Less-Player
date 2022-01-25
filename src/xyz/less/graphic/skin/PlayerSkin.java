package xyz.less.graphic.skin;

import javafx.scene.Scene;
import xyz.less.graphic.view.PlayerView;

public abstract class PlayerSkin extends Skin {
	protected PlayerView playerView;
	
	public PlayerSkin(String name, PlayerView playerView) {
		super(name);
		setPlayerView(playerView);
	}
	
	public PlayerView getPlayerView() {
		return playerView;
	}
	
	public void setPlayerView(PlayerView playerView) {
		this.playerView = playerView;
		if(playerView != null) {
			this.playerView.setSkin(this);
		}
	}
	
	@Override
	protected void init() {
		//TODO
		playerView.initGraph();
		playerView.playFromArgs();
	}
	
	
	@Override
	public void restore() {
		playerView.restore();
	}
	
	@Override
	public Scene createRootScene() {
		return new Scene(playerView, 
				playerView.getWidth(), playerView.getHeight());
	}
	
}
