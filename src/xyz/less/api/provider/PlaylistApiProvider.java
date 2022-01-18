package xyz.less.api.provider;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Future;

import xyz.less.api.IPlaylistApi;
import xyz.less.async.AsyncServices;
import xyz.less.bean.Audio;
import xyz.less.media.Playlist;

public class PlaylistApiProvider implements IPlaylistApi {
	private Playlist playlist;
	private Runnable onChanged;
	
	@Override
	public void add(Audio audio) {
		playlist.add(audio);
		notifyChanged();
	}

	@Override
	public void clear() {
		playlist.clear();
		notifyChanged();
	}

	@Override
	public void addAll(Collection<Audio> c) {
		playlist.addAll(c);
		notifyChanged();
	}

	@Override
	public int size() {
		return playlist.size();
	}

	@Override
	public int indexOf(Audio audio) {
		return playlist.indexOf(audio);
	}

	@Override
	public void loadFrom(String path) {
		try {
			Future<?> future = playlist.loadFrom(new File(path));
			AsyncServices.submitFxTaskOnFutureDone(future, onChanged);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private void notifyChanged() {
		AsyncServices.runLater(onChanged);
	}
}
