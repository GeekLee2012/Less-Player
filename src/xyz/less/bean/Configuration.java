package xyz.less.bean;

import java.util.ArrayList;
import java.util.List;

import xyz.less.util.StringUtil;

public final class Configuration {
	private String[] args;
	private List<String> dataArgs = new ArrayList<>(5);
	private String skinName;
	private boolean enableAnim = true;
	private boolean enableAutoDrawer = true;
	private boolean enableCoverAperture;
	private boolean argsPlaylistVisited;
	//TODO
	public static final String ANIM_FLAG = "-noAnim";
	//禁用抽屉效果(贴边自动隐藏)(仅适用MiniSkin)
	public static final String DRAWER_FLAG = "-noDrawer";
	//专辑封面中间显示小孔(仅适用MiniSkin)
	public static final String COVER_APERTURE_FLAG = "-aperture";
	
	private Configuration() {
		
	}
	
	private Configuration(String[] args) {
		setArgs(args);
	}
	public String[] getArgs() {
		return args;
	}
	private void setArgs(String[] args) {
		this.args = args;
	}
	public String getSkinName() {
		return skinName;
	}
	public void setSkinName(String skinName) {
		this.skinName = skinName;
	}
	public boolean isEnableAnim() {
		return enableAnim;
	}
	public void setEnableAnim(boolean enableAnim) {
		this.enableAnim = enableAnim;
	}
	public String getArgsPlaylistUri() {
		if(argsPlaylistVisited || dataArgs.isEmpty()) {
			return null;
		}
		setArgsPlaylistVisited(true);
		return dataArgs.get(0);
	}
	public boolean hasPlaylistUri() {
		return !argsPlaylistVisited && !dataArgs.isEmpty();
	}
	public List<String> getDataArgs() {
		return dataArgs;
	}
	public boolean isEnableAutoDrawer() {
		return enableAutoDrawer;
	}
	public void setEnableAutoDrawer(boolean enableAutoDrawer) {
		this.enableAutoDrawer = enableAutoDrawer;
	}
	public boolean isEnableCoverAperture() {
		return enableCoverAperture;
	}
	public void setEnableCoverAperture(boolean enableCoverAperture) {
		this.enableCoverAperture = enableCoverAperture;
	}
	private void setArgsPlaylistVisited(boolean value) {
		this.argsPlaylistVisited = value;
	}

	public static Configuration parseFrom(String[] args) {
		Configuration cfg = new Configuration(args);
		if(args == null || args.length <= 0) {
			return cfg;
		}
		try {
			for(String arg : args) {
				arg = StringUtil.trim(arg);
				if(StringUtil.isEmpty(arg)) {
					continue ;
				}
				//TODO
				if(ANIM_FLAG.equalsIgnoreCase(arg) ) {
					cfg.setEnableAnim(false);
				} else if(DRAWER_FLAG.equalsIgnoreCase(arg) ) {
					cfg.setEnableAutoDrawer(false);
				} else if(COVER_APERTURE_FLAG.equalsIgnoreCase(arg) ) {
					cfg.setEnableCoverAperture(true);
				} else {
					cfg.getDataArgs().add(arg);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return cfg;
	}

}
