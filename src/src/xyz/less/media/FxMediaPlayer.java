package xyz.less.media;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import xyz.less.bean.Audio;
import xyz.less.bean.ConfigConstant;
import xyz.less.media.PlaybackQueue.PlayMode;

public class FxMediaPlayer {
	private MediaView mediaView;
	private List<MediaPlayerListener> playerListeners = new ArrayList<>();
	private MediaPlayer delegatePlayer;
	private PlaybackQueue playbackQueue = new PlaybackQueue();
	private double volume = 0.5; 
	private int retry = 0;
	
	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}

	public void addPlayerListener(MediaPlayerListener listener) {
		this.playerListeners.add(listener);
	}

	public Playlist getPlaylist() {
		return playbackQueue.getPlaylist();
	}

	public void play() {
		doPlay(false);
	}
	
	private void doPlay(boolean force) {
		if(!playbackQueue.isEnable()) {
			return ;
		}
		if(delegatePlayer == null || force) {
			initDelegatePlayer(getCurrentSource());
		}
		if(delegatePlayer == null) {
			return ;
		}
		if(delegatePlayer.getStatus() == Status.UNKNOWN 
				|| delegatePlayer.getStatus() == Status.PAUSED) {
			delegatePlayer.play();
		} else {
			delegatePlayer.pause();
		}
	}
	
	public void play(int index) {
		if(playbackQueue.isCurrentIndex(index)) {
			return ;
		}
		playbackQueue.setCurrentIndex(index);
		doPlay(true);
	}
	
	public void playNext() {
		if(!playbackQueue.isEnable()) {
			return ;
		}
		playbackQueue.next();
		doPlay(true);
	}
	
	public void playPrevious() {
		if(!playbackQueue.isEnable()) {
			return ;
		}
		playbackQueue.prev();
		doPlay(true);
	}
	
	public void seek(double percent) {
		if(delegatePlayer != null) {
			Duration duration = delegatePlayer.getMedia().getDuration();
			delegatePlayer.seek(duration.multiply(percent));
		}
	}
	
	public void resetPlayer() {
		if(delegatePlayer != null) {
			delegatePlayer.dispose();
			delegatePlayer = null;
			
			playerListeners.forEach(listener -> {
				listener.onReset();
			});
		}
	}
	
	public void initDelegatePlayer(String source) {
		resetPlayer();
		try {
			Media media = new Media(source);
			delegatePlayer = new MediaPlayer(media);
			bindMediaView(delegatePlayer);
			delegatePlayer.setVolume(volume);
			
			delegatePlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
				playerListeners.forEach(listener -> {
					listener.spectrumDataUpdate(timestamp, duration, magnitudes, phases);
				});
			});
			
			delegatePlayer.setOnReady(() -> {
				playerListeners.forEach(listener -> {
					listener.onReady(playbackQueue.getCurrentAudio(), media.getMetadata());
				});
			});
			
			delegatePlayer.setOnPlaying(() -> {
				playerListeners.forEach(listener -> {
					listener.onPlaying();
				});
			});
			
			delegatePlayer.setOnPaused(() -> {
				playerListeners.forEach(listener -> {
					listener.onPaused();
				});
			});
			
			delegatePlayer.currentTimeProperty().addListener((o,ov,nv) -> {
				Duration duration = media.getDuration();
				playerListeners.forEach(listener -> {
					listener.onCurrentChanged(nv.toMinutes(), duration.toMinutes());
				});
			});
			delegatePlayer.setOnEndOfMedia(() -> {
				resetPlayer();
				playNext();
			});
		} catch(Exception e) {
			//TODO
			++retry;
			if(retry < playbackQueue.getPlaylist().size()) {
				if(playbackQueue.isNextAction()) {
					playNext();
				} else {
					playPrevious();
				}
			} else {
				retry = 0;
				playerListeners.forEach(listener -> {
					listener.onNoPlayableMedia();
				});
			}
		}
	}
	
	private void bindMediaView(MediaPlayer delegatePlayer2) {
		if(mediaView != null) {
			mediaView.setMediaPlayer(delegatePlayer);
		}
	}

	private String getCurrentSource() {
		Audio audio = playbackQueue.getCurrentAudio();
		return audio != null ? audio.getSource() : null; 
	}

	public void setVolumn(double value) {
		this.volume = value;
		if(delegatePlayer != null) {
			delegatePlayer.setVolume(value);
		}
	}

	public Future<?> loadFrom(File file) {
		resetPlaybackQueue();
		try {
			return playbackQueue.loadFrom(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void resetPlaybackQueue() {
		playbackQueue.reset();
		playerListeners.forEach(listener -> {
			listener.onNoMedia();
		});
		resetPlayer();
	}

	public boolean isInit() {
		return delegatePlayer != null;
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

	//http://music.163.com/song/media/outer/url?id=1822834628
	public void playUrl(String url) {
		Audio audio = new Audio();
		audio.setTitle(ConfigConstant.UNKOWN_AUDIO_ONLINE);
		audio.setSource(url);
		playbackQueue.getPlaylist().add(audio);
		initDelegatePlayer(url);
		delegatePlayer.play();
	}

	public boolean isPlaylistEmpty() {
		return getPlaylist().isEmpty();
	}
	
}
