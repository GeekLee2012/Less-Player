package xyz.less.graphic.skin;

import javafx.scene.Scene;
import xyz.less.graphic.view.PlayerView;
import xyz.less.graphic.view.PlaylistView;

public abstract class PlayerSkin extends Skin {
	protected PlayerView playerView;
	protected PlaylistView playlistView;
	
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
	
	public PlaylistView getPlaylistView() {
		return playlistView;
	}

	public void setPlaylistView(PlaylistView playlistView) {
		this.playlistView = playlistView;
	}

	@Override
	protected void init() {
		//TODO
		playerView.initGraph();
		playerView.playFromArgs();
		isInit = true;
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
