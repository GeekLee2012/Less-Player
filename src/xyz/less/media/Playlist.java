package xyz.less.media;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import xyz.less.async.AsyncServices;
import xyz.less.async.FileScanTask;
import xyz.less.bean.Audio;
import xyz.less.bean.ConfigConstant;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public class Playlist {
	private IntegerProperty sizeProperty = new SimpleIntegerProperty(0);
	private CopyOnWriteArrayList<Audio> audioList = new CopyOnWriteArrayList<>();
	
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
		return AsyncServices.submit(new FileScanTask(
				StringUtil.toSlash(dir.getAbsolutePath()), 
				file -> addFromFile(file), 
				ConfigConstant.AUDIO_SUFFIXES));
	}
	
	private void addFromFile(File file) {
		if(!FileUtil.isAudio(file)) {
			return ;
		}
		try {
			String url = StringUtil.toSlash(file.toURI().toURL().toExternalForm(), 
										"file:///".length());
			String title = StringUtil.decodeNameFromUrl(url);
			add(new Audio(title, null, null, 0, null, url));
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

	//TODO
	public Future<?> updateMetadatas() {
		return AsyncServices.submit(() -> {
			audioList.forEach(audio -> {
				try {
					File file = Paths.get(URI.create(audio.getSource())).toFile();
					Map<String, Object> metadata = Metadatas.readFrom(file);
					audio.setArtist(Metadatas.getArtist(metadata));
					audio.setAlbum(Metadatas.getAlbum(metadata));
					audio.setDuration(Metadatas.getDuration(metadata));
					audio.setCoverArt(Metadatas.getCoverArtImage(metadata));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
	}

	public void remove(int index) {
		audioList.remove(index);
	}
	
}
