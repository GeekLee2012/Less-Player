package xyz.less.graphic.skin;

import javafx.scene.Scene;
import javafx.stage.Stage;

//TODO 
//此Skin并非彼Skin啦，仅是个名字而已(吐个槽)
//有点粗糙，非常别扭
//起初想实现Skinable功能的，但能力有限，暂时搁置吧
public abstract class Skin {
	protected Stage mainStage;
	protected Scene scene;
	
	public Skin(Stage mainStage) {
		this.mainStage = mainStage;
	}
	
	public Scene getRootScene() {
		if(scene == null) {
			scene = createRootScene();
		}
		return scene;
	}
	
	public abstract void init();
	
	public abstract void restore();
	
	public abstract Scene createRootScene();

	public abstract String getName();

}
