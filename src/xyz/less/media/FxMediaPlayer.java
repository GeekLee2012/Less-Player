package xyz.less.media;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import xyz.less.bean.Audio;
import xyz.less.bean.Playlist;

public class FxMediaPlayer {
	private MediaView mediaView;
	private List<MediaPlayerListener> playerListeners = new ArrayList<>();
	private MediaPlayer delegatePlayer;
	private Media media;
	private Playlist playlist = new Playlist();
	private double volume = 0.5; 
	private boolean nextAction = true;
	private boolean shuffleMode = false;
	private int repeatMode = 0;
	private static final int DEFAULT_CURRENT_INDEX = -1;
	private int currentIndex = DEFAULT_CURRENT_INDEX;
	private SecureRandom random = new SecureRandom();
	
	public FxMediaPlayer() {
		//TODO
		playlist.sizeProperty().addListener((o,ov,nv) -> {
			if(playlist.isEmpty()) {
				playerListeners.forEach(listener -> {
					listener.onNoMedia();
				});
//				playerView.updatePlayBtn(false);
//				playerView.updateProgress(0, 0);
			}
		});
	}
	
	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}

	public void addPlayerListener(MediaPlayerListener listener) {
		this.playerListeners.add(listener);
	}

	public Playlist getPlaylist() {
		return playlist;
	}

	public void play() {
		doPlay(false);
	}
	
	private void doPlay(boolean force) {
		if(playlist.isEmpty()) {
			return ;
		}
		if(delegatePlayer == null || force) {
			initDelegatePlayer();
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
		if(currentIndex != index) {
			setCurrentIndex(index);
			doPlay(true);
		}
	}
	
	public void setCurrentIndex(int index) {
		if(index >= 0 && index < playlist.size()) {
			currentIndex = index;
		}
	}
	
	private void resetCurrentIndex() {
		currentIndex = DEFAULT_CURRENT_INDEX;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	private void nextIndex() {
		if(isRepeatOnceMode()) {
			return ;
		}
		if(isShuffleMode()) {
			currentIndex = random.nextInt(playlist.size());
			return ;
		}
		++currentIndex;
		if(isRepeatMode()){
			currentIndex = currentIndex % playlist.size();
		} else {
			if(currentIndex >= playlist.size()) {
				currentIndex = playlist.size() - 1;
			}
		}
	}
	
	public void playNext() {
		if(playlist.isEmpty()) {
			return ;
		}
		nextIndex();
		nextAction = true;
		doPlay(true);
	}
	
	private void prevIndex() {
		if(isRepeatOnceMode() || isShuffleMode()) {
			return ;
		}
		--currentIndex;
		if(isRepeatMode()) {
			currentIndex = currentIndex >= 0 ? currentIndex : playlist.size() - 1;
		} else {
			currentIndex = currentIndex > 0 ? currentIndex : 0;
		}
	}

	public void playPrevious() {
		if(playlist.isEmpty()) {
			return ;
		}
		prevIndex();
		nextAction = false;
		doPlay(true);
	}
	
	public void seek(double percent) {
		if(delegatePlayer != null) {
			Duration duration = media.getDuration();
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
	
	public void initDelegatePlayer() {
		resetPlayer();
		try {
			media = new Media(getCurrentSource());
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
					listener.onReady(getCurrentAudio(), media.getMetadata());
				});
//				playerView.updateMetadata(media);
//				playerView.highlightPlaylist();
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
//			e.printStackTrace();
			if(nextAction) {
				playNext();
			} else {
				playPrevious();
			}
		}
	}
	
	private void bindMediaView(MediaPlayer delegatePlayer2) {
		if(mediaView != null) {
			mediaView.setMediaPlayer(delegatePlayer);
		}
	}

	private String getCurrentSource() {
		Audio audio = getCurrentAudio();
		return audio != null ? audio.getSource() : null; 
	}
	
	private Audio getCurrentAudio() {
		if(currentIndex < 0) {
			nextIndex();
		}
		return playlist.get(currentIndex);
	}

	public void setVolumn(double value) {
		this.volume = value;
		if(delegatePlayer != null) {
			delegatePlayer.setVolume(value);
		}
	}

	public boolean isShuffleMode() {
		return shuffleMode;
	}

	public void setShuffleMode(boolean shuffleMode) {
		this.shuffleMode = shuffleMode;
	}

	public void setRepeatMode(int repeatMode) {
		this.repeatMode = repeatMode;
	}
	
	public boolean isRepeatMode() {
		return repeatMode == 1;
	}

	public boolean isRepeatOnceMode() {
		return repeatMode == 2;
	}
	
	public Future<?> loadFrom(File file) throws IOException {
		clearPlaylist();
		return playlist.loadFrom(file);
	}

	public void clearPlaylist() {
		playlist.clear();
		resetCurrentIndex();
		resetPlayer();
	}

	public boolean isInit() {
		return delegatePlayer != null;
	}
	
}
