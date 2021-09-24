package xyz.less.media;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.Future;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import xyz.less.bean.Playlist;
import xyz.less.engine.ResourcesEngine;
import xyz.less.graphic.PlayerView;

//TODO
public class FxMediaPlayer {
	private PlayerView playerView;
	private MediaPlayer delegatePlayer;
	private Media media;
	private Playlist<String> playlist = new Playlist<>();
	private double volume = 0.5; 
	private boolean nextAction = true;
	private boolean shuffleMode = false;
	private int repeatMode = 0;
	private static final int DEFAULT_CURRENT_INDEX = -1;
	private int currentIndex = DEFAULT_CURRENT_INDEX;
	
	private SecureRandom random = new SecureRandom();
	
	public void setPlayerView(PlayerView playerView) {
		this.playerView = playerView;
	}

	public Playlist<String> getPlaylist() {
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
			
			if(playlist.isEmpty()) {
				playerView.updatePlayBtn(false);
				playerView.updateProgressBar(0);
				playerView.updateTimeText(0, 0);
			}
		}
	}
	
	public void initDelegatePlayer() {
		resetPlayer();
		try {
			media = new Media(getCurrentSource());
			delegatePlayer = new MediaPlayer(media);
			playerView.getMediaView().setMediaPlayer(delegatePlayer);
			delegatePlayer.setVolume(volume);
			
			delegatePlayer.setOnReady(() -> {
				playerView.updateMetadata(media);
				playerView.highlightPlaylist();
			});
			
			delegatePlayer.setOnPlaying(() -> {
				playerView.updatePlayBtn(true);
			});
			
			delegatePlayer.setOnPaused(() -> {
				playerView.updatePlayBtn(false);
			});
			
			delegatePlayer.currentTimeProperty().addListener((o,ov,nv) -> {
				Duration duration = media.getDuration();
				playerView.updateProgressBar(nv.toSeconds() / duration.toSeconds());
				playerView.updateTimeText(nv.toMinutes(), duration.toMinutes());
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
	
	private String getCurrentSource() {
		if(currentIndex < 0) {
			nextIndex();
		}
		String currentUrl = playlist.get(currentIndex);
		if(currentUrl.startsWith("file:/")) {
			return currentUrl;
		}
		return ResourcesEngine.getAudio(currentUrl);
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
