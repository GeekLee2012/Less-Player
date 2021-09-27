package xyz.less.bean;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import xyz.less.async.AsyncServices;
import xyz.less.async.FileScanTask;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public class Playlist {
	private IntegerProperty sizeProperty = new SimpleIntegerProperty(0);
	private List<Audio> audioList = new CopyOnWriteArrayList<>();
	
	public List<Audio> get() {
		return audioList;
	}
	
	public void add(Audio t) {
		audioList.add(t);
		sizeProperty.set(size());
	}
	
	public void addAll(Collection<Audio> c) {
		audioList.addAll(c);
		sizeProperty.set(size());
	}
	
	public Future<?> loadFrom(File file) throws IOException {
		if(file == null || !file.exists()) {
			return null;
		}
		if(file.isDirectory()) {
			return loadFromDirectory(file);
		}
		return AsyncServices.submit(() -> {
			addFromFile(file);
		});
	}
	
	public Future<?> loadFromDirectory(File dir) throws IOException {
		FileScanTask task = new FileScanTask(StringUtil.toSlash(dir.getAbsolutePath()), 
				file -> addFromFile(file), ConfigConstant.AUDIO_SUFFIXES);
		return AsyncServices.submit(task);
	}
	
	public void loadFromUrl(String url) throws IOException {
		//TODO
	}
	
	private void addFromFile(File file) {
		if(!FileUtil.isAudio(file)) {
			return ;
		}
		try {
			String url = StringUtil.toSlash(file.toURI().toURL().toExternalForm(), 
										"file:///".length());
			String title = StringUtil.decodeNameFromUrl(url);
			add(new Audio(title, null, null, 0, url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isEmpty() {
		return audioList.isEmpty();
	}

	public int size() {
		return audioList.size();
	}

	public Audio get(int index) {
		return audioList.get(index);
	}
	
	public Playlist clear() {
		audioList.clear();
		sizeProperty.set(0);
		return this;
	}

	public IntegerProperty sizeProperty() {
		return sizeProperty;
	}
	
}
