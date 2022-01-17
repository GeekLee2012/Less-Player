package xyz.less.graphic.skin;

import javafx.scene.Scene;
import xyz.less.graphic.view.PlayerView;
import xyz.less.graphic.view.PlaylistView;

public abstract class PlayerSkin extends Skin {
	protected PlayerView playerView;
	protected PlaylistView playlistView;
	protected String name;
	private boolean inited;
	
	public PlayerSkin(String name, PlayerView playerView) {
		super(playerView.getMainStage());
		setName(name);
		setPlayerView(playerView);
		playerView.setSkin(this);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerView getPlayerView() {
		return playerView;
	}
	
	public void setPlayerView(PlayerView playerView) {
		this.playerView = playerView;
	}
	
	public PlaylistView getPlaylistView() {
		return playlistView;
	}

	public void setPlaylistView(PlaylistView playlistView) {
		this.playlistView = playlistView;
	}

	@Override
	public void init() {
		//TODO
		if(inited) {
			return ;
		}
		playerView.initGraph();
		playerView.playFromArgs();
		inited = true;
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
