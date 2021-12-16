package xyz.less.graphic.views;

import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.less.bean.ConfigConstant;

//TODO 
//此Skin并非彼Skin啦，仅是个名字而已(吐个槽)
//起初想实现Skinable功能的，但能力有限，暂时搁置吧
public final class SimpleSkin {
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

	public void init() {
		playerView.initGraph();
		//TODO
	}

	public Scene getRootScene() {
		return new Scene(playerView, 
				playerView.getWidth(), playerView.getHeight());
	}
}
