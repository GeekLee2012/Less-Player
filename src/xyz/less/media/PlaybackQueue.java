package xyz.less.media;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.Future;

import xyz.less.bean.Audio;

public final class PlaybackQueue {
	private Playlist playlist;
	private SecureRandom random = new SecureRandom();
	private PlayMode playMode;
	private static final int DEFAULT_CURRENT_INDEX = -1;
	private int currentIndex = DEFAULT_CURRENT_INDEX;
	private boolean nextAction = true;
	private boolean indexChanged = false;
	
	public PlaybackQueue() {
		this(null);
	}
	
	public PlaybackQueue(Playlist playlist) {
		setPlaylist(playlist);
	}
	
	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = (playlist == null ? new Playlist() : playlist);
	}

	public PlayMode getPlayMode() {
		return playMode;
	}

	public void setPlayMode(PlayMode playMode) {
		this.playMode = playMode;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}

	public int setCurrent(int index) {
		index = index < playlist.size() ? index : playlist.size() - 1;
		currentIndex = index > 0 ? index : 0;
		setIndexChanged(true);
		return currentIndex;
	}
	
	//TODO
	public int prev() {
		nextAction = false;
		if(!isEnable()) {
			return -1;
		}
		int index = currentIndex;
		switch (playMode) {
			case REPEAT_SELF:
				break;
			case SHUFFLE:
//				index = random.nextInt(playlist.size());
				break;
			case REPEAT_ALL:
				index = --currentIndex % playlist.size();
				break;
			case NO_REPEAT:
				index = --currentIndex;
				break;
			default:
				break;
		}
		return setCurrent(index);
	}
	
	public int next() {
		nextAction = true;
		if(!isEnable()) {
			return -1;
		}
		int index = currentIndex;
		switch (playMode) {
			case REPEAT_SELF:
				break;
			case SHUFFLE:
				index = random.nextInt(playlist.size());
				break;
			case REPEAT_ALL:
				index = ++index % playlist.size();
				break;
			case NO_REPEAT:
				++index;
				break;
			default:
				break;
		}
		return setCurrent(index);
	}
	
	public Audio getCurrent() {
		if (!isEnable()) {
			return null;
		}
		if(currentIndex < 0) {
			next();
		}
		return playlist.get(currentIndex);
	}
	
	public int indexOf(Audio audio) {
		return playlist.indexOf(audio);
	}
	
	public boolean isIndexChanged() {
		return indexChanged;
	}

	public void setIndexChanged(boolean value) {
		indexChanged = value;
	}

	public boolean isEnable() {
		return !playlist.isEmpty();
	}
	
	public boolean isCurrent(int index) {
		return currentIndex == index;
	}
	
	public void reset() {
		playlist.clear();
		currentIndex = DEFAULT_CURRENT_INDEX;
	}
	
	public boolean isNextAction() {
		return nextAction;
	}
	
	public Future<?> loadFrom(File file) throws IOException {
		return playlist.loadFrom(file);
	}
	
	public static enum PlayMode {
		NO_REPEAT, REPEAT_ALL, REPEAT_SELF, SHUFFLE;
		
		public static PlayMode valueOf(int index) {
			PlayMode[] values = values();
			index = (index > 0 && index < values.length) ? index : 0;
			return values[index];
		}
	}

}
