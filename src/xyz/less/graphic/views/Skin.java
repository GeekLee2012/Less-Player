package xyz.less.graphic.views;

import javafx.scene.Scene;
import javafx.stage.Stage;

//TODO 
//此Skin并非彼Skin啦，仅是个名字而已(吐个槽)
//有点粗糙，非常别扭
//起初想实现Skinable功能的，但能力有限，暂时搁置吧
public abstract class Skin {
	protected Stage mainStage;
	
	public Skin(Stage mainStage) {
		this.mainStage = mainStage;
	}
	
	public Scene getRootScene() {
		if(mainStage.getScene() != null) {
			return mainStage.getScene();
		}
		return createRootScene();
	}
	
	public abstract void init();
	
	protected abstract Scene createRootScene();

}
