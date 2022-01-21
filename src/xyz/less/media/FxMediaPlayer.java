package xyz.less.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javafx.scene.media.MediaView;
import xyz.less.api.provider.IExportable;
import xyz.less.api.provider.MediaPlayerApiProvider;
import xyz.less.api.provider.PlaylistApiProvider;
import xyz.less.async.AsyncServices;
import xyz.less.bean.Audio;
import xyz.less.bean.Constants;
import xyz.less.media.PlaybackQueue.PlayMode;
import xyz.less.service.MediaService;

//TODO 不知道写成了个啥！！！
public final class FxMediaPlayer implements IMediaPlayerListener, IExportable {
	private MediaView mediaView;
	private IDelegatePlayer delegatePlayer;
	private List<IMediaPlayerListener> playerListeners = new ArrayList<>();
	private PlaybackQueue playbackQueue = new PlaybackQueue();
	
	private boolean audioChanged;
	private double volume; 
	private int retry;
	private Map<String, Object> cachedMetadata;
	
	public FxMediaPlayer() {
		//TODO
		exportFor(MediaPlayerApiProvider.class);
		exportObjectsFor(PlaylistApiProvider.class, getPlaylist());
	}
	
	public void addPlayerListener(IMediaPlayerListener listener) {
		this.playerListeners.add(listener);
	}

	public Playlist getPlaylist() {
		return playbackQueue.getPlaylist();
	}

	public void play() {
		//TODO 拆分: New->Play->Pause->Play
		if(!playbackQueue.isEnable()) {
			return ;
		}
		//Audio已被切换
		if(delegatePlayer == null || isAudioChanged()) {
			doSelectDelegate();
			setAudioChanged(false);
		}
		//未初始化
		if(!isInit()) {
			if(!doDelegateInit()) {
				return ;
			}
		}
		if(isNotPlaying())  {
			delegatePlayer.play();
		} else {
			delegatePlayer.pause();
		}
	}
	
	private boolean isAudioChanged() {
		return audioChanged || !getCurrentAudio().equals(delegatePlayer.getCurrentAudio());
	}
	
	private void setAudioChanged(boolean value) {
		this.audioChanged = value;
	}

	public void play(int index) {
		if(!playbackQueue.isCurrentIndex(index)) {
			playbackQueue.setCurrentIndex(index);
		}
		play();
	}
	
	public void playNext() {
		if(!playbackQueue.isEnable()) {
			return ;
		}
		playbackQueue.next();
		setAudioChanged(true);
		play();
	}
	
	public void playPrevious() {
		if(!playbackQueue.isEnable()) {
			return ;
		}
		playbackQueue.prev();
		setAudioChanged(true);
		play();
	}
	
	public void seek(double percent) {
		delegatePlayer.seek(percent);
	}
	
	//TODO
	private void retryPlay() {
		++retry;
		if(retry < playbackQueue.getPlaylist().size()) {
			if(playbackQueue.isNextAction()) {
				playNext();
			} else {
				playPrevious();
			}
		} else {
			retry = 0;
			onNoPlayableMedia();
		}
	}
	
	public Audio getCurrentAudio() {
		if(!playbackQueue.isEnable()) {
			return null;
		}
		return playbackQueue.getCurrentAudio();
	}
	
	public boolean isCurrentAudioExist() {
		return getCurrentAudio() != null;
	}

	public void setVolume(double value) {
		this.volume = (value > 0 ? value : 0);
		doSetDelegateVolume();
	}
	
	public double getVolume() {
		return volume;
	}

	public Future<?> loadFrom(File file) {
		try {
			resetPlaybackQueue(false);
			return playbackQueue.loadFrom(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void resetPlaybackQueue(boolean notify) {
		playbackQueue.reset();
		if(notify) {
			onNoMedia();
		}
		doDelegateReset();
	}

	public int getCurrentIndex() {
		return playbackQueue.getCurrentIndex();
	}
	
	public void setPlayMode(PlayMode playMode) {
		playbackQueue.setPlayMode(playMode);
	}
	
	public PlayMode getPlayMode() {
		return playbackQueue.getPlayMode();
	}

	public Future<?> updateMetadatas() {
		return playbackQueue.getPlaylist().syncMetadatas();
	}

	public boolean isPlaylistEmpty() {
		return getPlaylist().isEmpty();
	}
	
	//TODO Just Testing
	//http://music.163.com/song/media/outer/url?id=1822834628
	public void playLink(String url) {
		resetPlaybackQueue(true);
		Audio audio = new Audio();
		audio.setTitle(Constants.UNKOWN_AUDIO_ONLINE);
		audio.setSource(url);
		playbackQueue.getPlaylist().add(audio);
		
		play();
	}
	
	
	public void destroy() {
		//TODO
	}
	
	/****** DelegatePlayer ******/
	public boolean isInit() {
		return delegatePlayer != null && delegatePlayer.isInit();
	}
	
	public boolean isNotPlaying() {
		return !isInit() || delegatePlayer.isNotPlaying();
	}
	
	//TODO
	private void doSelectDelegate() {
		if(delegatePlayer != null) {
			delegatePlayer.reset();
		}
		delegatePlayer = MediaService.select(getCurrentAudio());
		doSetDelegateVolume();
		delegatePlayer.addListener(this);
		delegatePlayer.setMediaView(mediaView);
		delegatePlayer.setAudioChanged(true);
		
//		System.out.println("SOURCE: " + getCurrentAudio().getSource());
	}
	
	private boolean doDelegateInit() {
		return delegatePlayer.init(getCurrentAudio());
	}
	
	private void doDelegateReset() {
		if(delegatePlayer != null) {
			delegatePlayer.reset();
		}
	}
	
	private void doSetDelegateVolume() {
		if(delegatePlayer != null) {
			delegatePlayer.setVolume(volume);
		}
	}
	
	public boolean isSeekable() {
		return isInit() && delegatePlayer.isSeekable();
	}
	
	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}
	
	public MediaView getMediaView() {
		return mediaView;
	}
	
	public Map<String, Object> getCurrentMetadata() {
		return cachedMetadata;
	}

	private void setCachedMetadata(Map<String, Object> cachedMetadata) {
		this.cachedMetadata = cachedMetadata;
	}

	/****** MediaPlayerListener ******/
	protected void notifyAllListeners(Consumer<? super IMediaPlayerListener> action) {
		AsyncServices.runLater(() -> playerListeners.forEach(action));
	}
	
	@Override
	public void onReady(Audio audio, Map<String, Object> metadata) {
		setCachedMetadata(metadata);
		notifyAllListeners(e -> e.onReady(audio, metadata));
	}

	@Override
	public void onPlaying() {
		notifyAllListeners(e -> e.onPlaying());
	}

	@Override
	public void onCurrentChanged(double currentMinutes, double durationMinutes) {
		notifyAllListeners(e -> e.onCurrentChanged(currentMinutes, durationMinutes));
	}

	@Override
	public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		notifyAllListeners(e -> e.spectrumDataUpdate(timestamp, duration, magnitudes, phases));
	}

	@Override
	public void onPaused() {
		notifyAllListeners(e -> e.onPaused());
	}

	@Override
	public void onEndOfMedia() {
		notifyAllListeners(e -> e.onEndOfMedia());
		playNext();
	}

	@Override
	public void onError() {
		notifyAllListeners(e -> e.onError());
		retryPlay();
	}

	@Override
	public void onReset() {
		setCachedMetadata(null);
		notifyAllListeners(e -> e.onReset());
	}

	@Override
	public void onNoMedia() {
		setCachedMetadata(null);
		notifyAllListeners(e -> e.onNoMedia());
	}

	@Override
	public void onNoPlayableMedia() {
		notifyAllListeners(e -> e.onNoPlayableMedia());
	}

}
