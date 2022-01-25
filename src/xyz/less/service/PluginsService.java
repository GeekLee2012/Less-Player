package xyz.less.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javafx.scene.image.Image;
import xyz.less.async.AsyncServices;
import xyz.less.plugin.IDndListener;

//TODO
public final class PluginsService {
	private final static List<URL> PLUGIN_URLS = new ArrayList<>();
	private static URLClassLoader pluginsCL = null;
	private final static DndPlugin dndPlugin = new DndPlugin();
	private static int size = 0;
	
	public static void start() {
		RpcService.start();
		reload();
	}
	
	private static int reload() {
		//TODO
		System.out.println(">>>>>>Start Reload Plugins");
		size = 0;
		size += reloadDndPlugins();
		System.out.println(">>>>>>Total Loaded Plugins: " + size);
		return size;
	}
	
	public static int reloadDndPlugins() {
		return dndPlugin.reload();
	}
	
	public static DndPlugin getDndPlugin() {
		return dndPlugin;
	}
	
	private static URLClassLoader doGetClassLoader() {
		if(PLUGIN_URLS.size() < 1) {
			return null;
		}
		if(pluginsCL == null) {
			URL[] urls = PLUGIN_URLS.toArray(new URL[PLUGIN_URLS.size()]);
			ClassLoader parent = ClassLoader.getSystemClassLoader();
			pluginsCL = new URLClassLoader(urls, parent);
		}
		return pluginsCL;
	}

	public static boolean loadJar(File file) {
		try {
			URL url = file.toURI().toURL();
			PLUGIN_URLS.add(url);
			
			Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			addURL.setAccessible(true);
			addURL.invoke(doGetClassLoader(), url);
			
			reload();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void close() {
		try {
			if(pluginsCL != null) {
				pluginsCL.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class DndPlugin {
		private List<IDndListener> dndListeners = new ArrayList<>();
		
		public int reload() {
			dndListeners.clear();
			if(doGetClassLoader() != null) {
				ServiceLoader.load(IDndListener.class, pluginsCL).forEach(listener -> {
					System.out.println(">>>DndListener: " + listener.getClass().getName());
					dndListeners.add(listener);
				});
			}
			return dndListeners.size();
		}
		
		/**图片*/
		public void onDndSuccess(Image image) {
			dndListeners.forEach(e -> e.onDndSuccess(image));
		}
		
		/**未被识别(或未被支持)的其他文件*/
		public void onDndDone(File file) {
			dndListeners.forEach(e -> AsyncServices.submit(() -> e.onDndDone(file)));
		}
		
		/**超链接*/
		public void onDndLinkDone(String url) {
			dndListeners.forEach(e -> AsyncServices.submit(() -> e.onDndLinkDone(url)));
		}
	}
	 
}
