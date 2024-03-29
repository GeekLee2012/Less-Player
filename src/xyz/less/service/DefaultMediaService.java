package xyz.less.service;

import javafx.scene.media.MediaView;
import xyz.less.bean.Audio;
import xyz.less.media.*;
import xyz.less.media.PlaybackQueue.PlayMode;
import xyz.less.util.FileUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.Future;

public final class DefaultMediaService implements IMediaService, IMediaPlayerListener {
	private PlaybackQueue playbackQueue = new PlaybackQueue();
	private MediaListenerManager listenersMgr = new MediaListenerManager();
	private MediaPlayerManager playerMgr = new MediaPlayerManager();
	private IMediaPlayer delegate;
	private MediaView mediaView;
	private double volume = 1.0;
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
		if(!playbackQueue.isEnable()) {
			return ;
		}
		if(!isInit() || isAudioChanged()) {
			Audio audio = getCurrent();
			onInit(audio); //TODO
			delegate = selectDelegate(audio);
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
		System.out.println("[Audio Changed] " + audio.getTitle() + ", Source: " + audio.getSource());
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
//		playNextAction = true;
		if(playbackQueue.next() >= 0) {
			play();
		}
	}
	
	@Override
	public void playPrev() {
//		playNextAction = false;
		if(playbackQueue.prev() >= 0) {
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
			onPlaylistUpdated();
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

	@Override
	public Future<?> loadFrom(String url) {
		try {
			return loadFrom(FileUtil.toFile(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Future<?> loadFrom(File file) {
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
//		return playbackQueue.getPlaylist().sort().get();
		//暂时不排序，排序规则由上层调用方决定
		return playbackQueue.getPlaylist().get();
	}

	@Override
	public int getCurrentIndex() {
		return playbackQueue.getCurrentIndex();
	}

	@Override
	public void remove(Audio audio) {
		//TODO
		//是否正在播放
		boolean isCurrent = (getCurrent() == audio);
		playbackQueue.getPlaylist().remove(audio);
		if (playbackQueue.isEnable()) {
			//更新索引
			int index = getCurrentIndex() - 1;
			if (index >= 0) {
				playbackQueue.setCurrent(index);
			} else {
				playbackQueue.resetIndex();
			}
			//当正在播放歌曲被删除时，自动切到下一曲
			if (isCurrent) {
				playNext();
			}
		} else { //当前播放列表为空
			if(isInit()) {
				delegate.reset(true);
				delegate = null;
			}
			onNoMedia();
		}

	}

	@Override
	public void removeAll(boolean notify) {
		do {
			playbackQueue.reset();
		} while(playbackQueue.isEnable());
		if(isInit()) {
			delegate.reset(true);
			delegate = null;
		}
		if(notify) {
			onNoMedia();
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
		retrySet.add(getCurrentIndex());
		if(playbackQueue.isNextAction()) {
			playNext();
		} else {
			playPrev();
		}
	}

	@Override
	public void onInit(Audio audio) {
		listenersMgr.onInit(audio);
	}

	/* Listeners */
	@Override
	public void onReady(Audio audio, Map<String, Object> metadata) {
		this.cachedMetadata = metadata;
//		this.retrySet.clear();
		listenersMgr.onReady(audio, metadata);
		System.out.println("[Current Playing]" + audio.getTitle() + ", Source：" + audio.getSource());
	}

	@Override
	public void onPlaying() {
//		this.retrySet.clear();
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
		this.retrySet.clear();
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

	@Override
	public void onPlaylistUpdated() {
		listenersMgr.onPlaylistUpdated();
	}
}
