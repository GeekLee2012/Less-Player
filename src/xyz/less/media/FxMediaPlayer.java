package xyz.less.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javafx.application.Platform;
import javafx.scene.media.MediaView;
import xyz.less.bean.Audio;
import xyz.less.bean.Constants;
import xyz.less.engine.MediaEngine;
import xyz.less.media.PlaybackQueue.PlayMode;

//TODO 不知道写成了个啥！！！
public final class FxMediaPlayer implements IMediaPlayerListener {
	private MediaView mediaView;
	private IDelegatePlayer delegatePlayer;
	private List<IMediaPlayerListener> playerListeners = new ArrayList<>();
	private PlaybackQueue playbackQueue = new PlaybackQueue();
	
	private boolean audioChanged;
	private double volume = 0.5; 
	private int retry = 0;
	
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
		if(delegatePlayer.isNotPlaying())  {
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
	public void retryPlay() {
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
	
	private Audio getCurrentAudio() {
		return playbackQueue.getCurrentAudio();
	}

	public void setVolume(double value) {
		this.volume = value >= 0 ? value : 0;
		doSetDelegateVolume();
	}

	public Future<?> loadFrom(File file) {
		try {
			resetPlaybackQueue();
			return playbackQueue.loadFrom(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void resetPlaybackQueue() {
		playbackQueue.reset();
		onNoMedia();
		doDelegateReset();
	}

	public int getCurrentIndex() {
		return playbackQueue.getCurrentIndex();
	}

	public void setPlayMode(PlayMode playMode) {
		playbackQueue.setPlayMode(playMode);
	}

	public Future<?> updateMetadatas() {
		return playbackQueue.getPlaylist().updateMetadatas();
	}

	public boolean isPlaylistEmpty() {
		return getPlaylist().isEmpty();
	}
	
	//TODO 测试一下而已
	//http://music.163.com/song/media/outer/url?id=1822834628
	public void playUrl(String url) {
		resetPlaybackQueue();
		Audio audio = new Audio();
		audio.setTitle(Constants.UNKOWN_AUDIO_ONLINE);
		audio.setSource(url);
		playbackQueue.getPlaylist().add(audio);
		
		play();
	}
	
	/****** DelegatePlayer ******/
	public boolean isInit() {
		return delegatePlayer != null && delegatePlayer.isInit();
	}
	
	//TODO
	private void doSelectDelegate() {
		if(delegatePlayer != null) {
			delegatePlayer.reset();
		}
		delegatePlayer = MediaEngine.select(getCurrentAudio());
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
	
	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}
	
	/****** MediaPlayerListener ******/
	@Override
	public void onReady(Audio audio, Map<String, Object> metadata) {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.onReady(audio, metadata));
		});
	}

	@Override
	public void onPlaying() {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.onPlaying());
		});
	}

	@Override
	public void onCurrentChanged(double currentMinutes, double durationMinutes) {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.onCurrentChanged(currentMinutes, durationMinutes));
		});
	}

	@Override
	public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.spectrumDataUpdate(timestamp, duration, magnitudes, phases));
		});
	}

	@Override
	public void onPaused() {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.onPaused());
		});
	}

	@Override
	public void onEndOfMedia() {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.onEndOfMedia());
			playNext();
		});
	}

	@Override
	public void onError() {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.onError());
			retryPlay();
		});
	}

	@Override
	public void onReset() {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.onReset());
		});
	}

	@Override
	public void onNoMedia() {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.onNoMedia());
		});
	}

	@Override
	public void onNoPlayableMedia() {
		Platform.runLater(() -> {
			playerListeners.forEach(e -> e.onNoPlayableMedia());
		});
	}
}
