package xyz.less.graphic.skin;

import javafx.scene.Scene;

//TODO 
//此Skin并非彼Skin啦，仅是个名字而已(吐个槽)
//有点粗糙，非常别扭
//起初想实现Skinable功能的，但能力有限，暂时搁置吧
public abstract class Skin {
	private String name;
	protected boolean isInit;
	protected Scene scene;
	
	public Skin(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Scene getRootScene() {
		if(scene == null) {
			scene = createRootScene();
		}
		return scene;
	}
	
	public boolean isInit() {
		return isInit;
	}
	
	public Skin load() {
		return load(false);
	}
	
	public Skin load(boolean needRestore) {
		if(!isInit()) {
			init();
		}
		if(needRestore) {
			restore();
		}
		return this;
	}
	
	protected abstract void init();
	
	protected abstract void restore();
	
	protected abstract Scene createRootScene();

}
