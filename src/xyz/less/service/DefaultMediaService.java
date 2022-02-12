package xyz.less.service;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javafx.scene.media.MediaView;
import xyz.less.bean.Audio;
import xyz.less.media.IMediaPlayer;
import xyz.less.media.IMediaPlayerListener;
import xyz.less.media.MediaListenerManager;
import xyz.less.media.MediaPlayerManager;
import xyz.less.media.PlaybackQueue;
import xyz.less.media.PlaybackQueue.PlayMode;

public final class DefaultMediaService implements IMediaService, IMediaPlayerListener {
	private PlaybackQueue playbackQueue = new PlaybackQueue();
	private MediaListenerManager listenersMgr = new MediaListenerManager();
	private MediaPlayerManager playerMgr = new MediaPlayerManager();
	private IMediaPlayer delegate;
	private MediaView mediaView;
	private double volume;
	private boolean playNextAction = true;
	private Map<String, Object> cachedMetadata;
	private Set<Integer> retrySet = new HashSet<>();
	
	public DefaultMediaService() {
		playerMgr.printAllSuffixes();
	}
	
	@Override
	public boolean isInit() {
		return delegate != null && 
				delegate.getCurrent() != null;
	}
	
	@Override
	public boolean isPlaying() {
		return isInit() && delegate.isPlaying();
	}
	
	@Override
	public void play() {
		//TODO 拆分: New->Play->Pause->Play
		if(!playbackQueue.isEnable()) {
			return ;
		}
		if(!isInit() || isAudioChanged()) {
			delegate = selectDelegate(getCurrent());
			setAudioChanged(false);
		}
		delegate.play();
	}
	
	private boolean isAudioChanged() {
		return playbackQueue.isIndexChanged();
	}

	private void setAudioChanged(boolean value) {
		playbackQueue.setIndexChanged(value);
	}
	
	private IMediaPlayer selectDelegate(Audio audio) {
		if(delegate != null) {
			delegate.reset(true);
		}
		delegate = playerMgr.select(audio);
		delegate.setCurrent(audio);
		delegate.addListener(this);
		delegate.setVolume(volume);
		System.out.println("[Delegate Player] " + delegate.getClass());
		return delegate;
	}

	@Override
	public void pause() {
		if(isInit()) {
			delegate.pause();
		}
	}
	
	@Override
	public void playNext() {
		playNextAction = true;
		if(playbackQueue.next() > 0) {
			play();
		}
	}
	
	@Override
	public void playPrev() {
		playNextAction = false;
		if(playbackQueue.prev() > 0) {
			play();
		}
	}
	
	@Override
	public boolean isSeekable() {
		return isInit() && delegate.isSeekable();
	}
	
	@Override
	public void seek(double percent) {
		if(isSeekable()) {
			delegate.seek(percent);
		}
	}
	
	@Override
	public void reset(boolean notify) {
		if(isInit()) {
			delegate.reset(notify);
		}
	}
	
	@Override
	public PlayMode getPlayMode() {
		return playbackQueue.getPlayMode();
	}
	
	@Override
	public void setPlayMode(PlayMode mode) {
		playbackQueue.setPlayMode(mode);
	}
	
	@Override
	public void setVolume(double volume) {
		this.volume = volume;
		if(isInit()) {
			delegate.setVolume(volume);
		}
	}
	
	@Override
	public double getVolume() {
		return volume;
	}
	
	@Override
	public Audio getCurrent() {
		return playbackQueue.getCurrent();
	}
	
	@Override
	public void setCurrent(Audio audio) {
		int index = playbackQueue.indexOf(audio);
		playbackQueue.setCurrent(index);
	}
	
	@Override
	public boolean isPlayable(Audio audio) {
		return delegate.isPlayable(audio);
	}
	
	@Override
	public void addAll(Audio... audios) {
		if(audios != null) {
			addAll(Arrays.asList(audios));
		}
	}
	
	@Override
	public void addAll(Collection<Audio> audios) {
		if(audios != null) {
			playbackQueue.getPlaylist().addAll(audios);
		}
	}
	
	@Override
	public void addListener(IMediaPlayerListener... listeners) {
		listenersMgr.addListener(listeners);
	}
	
	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}
	
	public MediaView getMediaView() {
		return mediaView;
	}

	@Override
	public Set<String> getSuffixSet() {
		return playerMgr.getAllSuffixes();
	}
	
	public Future<?> loadFrom(File file) {
		try {
			removeAll(false);
			return playbackQueue.loadFrom(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Future<?> syncMetadatas() {
		return playbackQueue.getPlaylist().syncMetadatas();
	}

	@Override
	public List<Audio> getPlaylist() {
		return playbackQueue.getPlaylist().sort().get();
	}

	@Override
	public int getCurrentIndex() {
		return playbackQueue.getCurrentIndex();
	}

	@Override
	public void removeAll(boolean notify) {
		do {
			playbackQueue.reset();
		} while(playbackQueue.isEnable());
		if(notify) {
			onNoMedia();
		}
		if(isInit()) {
			delegate.reset(true);
			delegate = null;
		}
	}
	
	@Override
	public Map<String, Object> getCurrentMetadata() {
		return cachedMetadata;
	}
	
	//PS: 避免无限循环尝试
	private void retryPlay() {
		//当前播放列表是否都已被尝试过
		int retryMax = playbackQueue.getPlaylist().size();
		if(retrySet.size() >= retryMax) {
			onNoPlayableMedia();
			return ;
		}
		if(playNextAction) {
			playNext();
		} else {
			playPrev();
		}
		retrySet.add(getCurrentIndex());
	}
	
	/* Listeners */
	@Override
	public void onReady(Audio audio, Map<String, Object> metadata) {
		this.cachedMetadata = metadata;
		this.retrySet.clear();
		listenersMgr.onReady(audio, metadata);
		System.out.println("[Current Playing]" + audio.getTitle() + ", Source：" + audio.getSource());
	}

	@Override
	public void onPlaying() {
		listenersMgr.onPlaying();
	}

	@Override
	public void onCurrentChanged(double currentMinutes, double durationMinutes) {
		listenersMgr.onCurrentChanged(currentMinutes, durationMinutes);
	}

	@Override
	public void onSpectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		listenersMgr.onSpectrumDataUpdate(timestamp, duration, magnitudes, phases);
	}

	@Override
	public void onPaused() {
		listenersMgr.onPaused();
	}

	@Override
	public void onEndOfMedia() {
		listenersMgr.onEndOfMedia();
		playNext();
	}

	@Override
	public void onError(Exception ex) {
		listenersMgr.onError(ex);
		retryPlay();
	}

	@Override
	public void onReset() {
		listenersMgr.onReset();
	}

	@Override
	public void onNoMedia() {
		listenersMgr.onNoMedia();
	}

	@Override
	public void onNoPlayableMedia() {
		listenersMgr.onNoPlayableMedia();
	}

}
