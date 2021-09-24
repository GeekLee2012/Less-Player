package xyz.less.engine;

import java.util.HashMap;
import java.util.Map;

import javafx.stage.Stage;

public class SkinEngine {
	Stage stage;
	Map<String, Skin> skinMap = new HashMap<>();
	
	public SkinEngine() {
		
	}
	
	public void addSkin(String id, Skin skin) {
		skinMap.put(id, skin);
	}
	
	public Skin loadSkin(String id) {
		return skinMap.get(id);
	}
}
