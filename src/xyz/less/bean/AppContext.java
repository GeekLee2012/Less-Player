package xyz.less.bean;

import javafx.stage.Stage;
import xyz.less.graphic.skin.MiniSkin;

public final class AppContext {
	private Stage mainStage;
	private ArgsBean argsBean;
	
	public AppContext() {
		
	}
	
	public AppContext(Stage mainStage) {
		setMainStage(mainStage);
	}
	
	public AppContext setMainStage(Stage stage) {
		this.mainStage = stage;
		return this;
	}
	
	public Stage getMainStage() {
		return mainStage;
	}

	public ArgsBean getArgsBean() {
		return argsBean;
	}

	public void setArgsBean(ArgsBean argsBean) {
		this.argsBean = argsBean;
	}

	public String getSkinName() {
		return argsBean.getSkinName();
	}
	
	public boolean isEnableAnim() {
		return argsBean.isEnableAnim();
	}
	
	public boolean hasArgsPlaylist() {
		return argsBean.hasPlaylistUri();
	}
	
	public String getArgsPlaylistUri() {
		return argsBean.getPlaylistUri();
	}
	
	public boolean isEnableAutoDrawer() {
		return argsBean.isEnableAutoDrawer();
	}

	public boolean isEnableCoverAperture() {
		return argsBean.isEnableCoverAperture();
	}
	
	public boolean isMiniSkin() {
		return MiniSkin.NAME.equalsIgnoreCase(getSkinName());
	}
}
