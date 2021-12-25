package xyz.less.bean;

import java.util.ArrayList;
import java.util.List;

import xyz.less.graphic.skin.MiniSkin;
import xyz.less.util.StringUtil;

public class ArgsBean {
	private String[] args;
	private String skinName;
	private boolean enableAnim;
	private List<String> dataArgs = new ArrayList<>(5);
	//TODO
	public static final String MINI_SKIN_FLAG = "-mini";
	public static final String ANIM_FLAG = "-anim";
	
	public ArgsBean(String[] args) {
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
	public String getPlaylistUri() {
		if(dataArgs.isEmpty()) {
			return null;
		}
		return dataArgs.get(0);
	}
	public boolean hasPlaylistUri() {
		return !StringUtil.isBlank(getPlaylistUri());
	}
	public List<String> getDataArgs() {
		return dataArgs;
	}

	public ArgsBean parse() {
		if(args == null || args.length <= 0) {
			return this;
		}
		try {
			for(String arg : args) {
				arg = StringUtil.trim(arg);
				if(StringUtil.isEmpty(arg)) {
					continue ;
				}
				//TODO
				if(MINI_SKIN_FLAG.equalsIgnoreCase(arg)) {
					setSkinName(MiniSkin.NAME);
				} else if(ANIM_FLAG.equalsIgnoreCase(arg) ) {
					setEnableAnim(true);
				} else {
					dataArgs.add(arg);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
}
