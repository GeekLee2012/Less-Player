package xyz.less.media;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import xyz.less.async.AsyncServices;
import xyz.less.async.FileScanTask;
import xyz.less.bean.AppContext;
import xyz.less.bean.Audio;
import xyz.less.bean.Resources;
import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public final class Playlist {
	private IntegerProperty sizeProp = new SimpleIntegerProperty(0);
	private CopyOnWriteArrayList<Audio> audioList = new CopyOnWriteArrayList<>();
	
	public List<Audio> get() {
		return audioList;
	}
	
	public Playlist sort() {
		audioList.sort((o1, o2)-> o1.compareTo(o2));
//		audioList.forEach(o -> System.out.println(o.getSource()));
		return this;
	}
	
	//TODO 暂时不去重
	public void add(Audio t) {
		audioList.add(t);
		sizeProp.set(size());
	}
	
	//TODO 暂时不去重
	public void addAll(Collection<Audio> c) {
		audioList.addAll(c);
		sizeProp.set(size());
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
				AppContext.get().getSuffixes()));
	}
	
	public void addFromFile(File file) {
		if(!AppContext.get().isFileSupported(file)) {
			return ;
		}
		try {
			String url = StringUtil.toSlash(file.toURI().toURL().toExternalForm(), 
										Resources.FILE_PREFIX_2.length());
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
	
	public int indexOf(Audio audio) {
		return audioList.indexOf(audio);
	}

	public Audio get(int index) {
		return audioList.get(index);
	}
	
	public Playlist clear() {
		audioList.clear();
		sizeProp.set(0);
		return this;
	}

	public IntegerProperty sizeProperty() {
		return sizeProp;
	}

	public Future<?> syncMetadatas() {
		return AsyncServices.submit(() -> {
			audioList.forEach(audio -> syncMetadataFromFile(audio));
		});
	}
	
	private void syncMetadataFromFile(Audio audio) {
		try {
			//TODO
			File file = FileUtil.toFile(audio.getSource());
			Map<String, Object> metadata = Metadatas.readFrom(file);
			audio.setArtist(Metadatas.getArtist(metadata));
			audio.setAlbum(Metadatas.getAlbum(metadata));
			audio.setDuration(Metadatas.getDuration(metadata));
			audio.setCoverArt(Metadatas.getCoverArt(metadata));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void remove(int index) {
		audioList.remove(index);
	}

}
