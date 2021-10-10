package xyz.less.graphic;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;

public final class GuiRegistry {
	private final Map<String, Node> nodeRegistryMap = new HashMap<>();
	
	public Node register(String id, Node node) {
		return nodeRegistryMap.putIfAbsent(id, node);
	}
	
	public Node find(String id) {
		return nodeRegistryMap.get(id);
	}
	
	public <T> T find(String id, Class<T> type) {
		try {
			return type.cast(find(id));
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
