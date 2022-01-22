package xyz.less.service;

import java.net.URL;

import javafx.scene.image.Image;
import xyz.less.bean.Resources;

public final class ResourcesService {
	
	public static Image getImage(String name) {
		return new Image(getResourceUrl(Resources.IMG_SRC, name)
				.toExternalForm());
	}
	
	public static URL getFxml(String name) {
		return getResourceUrl(Resources.FXML_SRC, name, Resources.FXML);
	}
	
	public static String getStyle(String name) {
		return getResourceUrl(Resources.STYLE_SRC, name, Resources.CSS)
					.toExternalForm();
	}
	
	private static URL getResourceUrl(String srcRoot, String name, String ext) {
		name = name.endsWith(ext) ? name : name + ext;
		return getResourceUrl(srcRoot, name);
	}
	
	private static URL getResourceUrl(String srcRoot, String name) {
		return ResourcesService.class.getResource(srcRoot + name);
	}
	
}
