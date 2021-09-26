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

public class Playlist<T> {
	private IntegerProperty sizeProperty = new SimpleIntegerProperty(0);
	private List<T> audioList = new CopyOnWriteArrayList<T>();
	
	public List<T> get() {
		return audioList;
	}
	
	public void add(T t) {
		audioList.add(t);
		sizeProperty.set(size());
	}
	
	public void addAll(Collection<T> c) {
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
	
	@SuppressWarnings("unchecked")
	private void addFromFile(File file) {
		if(!FileUtil.isAudio(file)) {
			return ;
		}
		try {
//			System.out.println(file.toURI().toURL().toExternalForm());
			String url = StringUtil.toSlash(file.toURI().toURL().toExternalForm(), 
										"file:///".length());
			add((T)url);
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

	public T get(int index) {
		return audioList.get(index);
	}
	
	public Playlist<T> clear() {
		audioList.clear();
		sizeProperty.set(0);
		return this;
	}

	public IntegerProperty sizeProperty() {
		return sizeProperty;
	}
	
}
