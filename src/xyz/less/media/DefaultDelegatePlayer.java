package xyz.less.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import xyz.less.bean.Audio;
import xyz.less.engine.MediaEngine;

/**
 * 基于JavaFX MediaPlayer简单封装
 */
public final class DefaultDelegatePlayer extends AbstractDelegatePlayer {
	private MediaPlayer mediaPlayer;
	
	public DefaultDelegatePlayer() {
		super(MediaEngine.SUFFIXES_1);
	}
	
	@Override
	public boolean init(Audio audio) {
		super.init(audio);
		reset();
		try {
			Media media = new Media(audio.getSource());
			mediaPlayer = new MediaPlayer(media);
			bindMediaView(mediaPlayer);
			mediaPlayer.setVolume(volume);
			
			mediaPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> 
				spectrumDataUpdate(timestamp, duration, magnitudes, phases));
			
			mediaPlayer.currentTimeProperty().addListener((o,ov,nv) -> {
				Duration duration = media.getDuration();
				onCurrentChanged(nv.toMinutes(), duration.toMinutes());
			});
			
			mediaPlayer.setOnReady(() -> onReady(currentAudio, media.getMetadata()));
			mediaPlayer.setOnPlaying(() -> onPlaying());
			mediaPlayer.setOnPaused(() -> onPaused());
			mediaPlayer.setOnEndOfMedia(() -> onEndOfMedia());
			mediaPlayer.setOnError(() -> onError());
		} catch(Exception e) {
			onError();
		}
		return isInit();
	}

	@Override
	public void play() {
		if(isInit()) {
			mediaPlayer.play();
		}
	}
	
	@Override
	public void pause() {
		if(isInit()) {
			mediaPlayer.pause();
		}
	}
	
	@Override
	public void seek(double percent) {
		if(isInit()) {
			Duration duration = mediaPlayer.getMedia().getDuration();
			mediaPlayer.seek(duration.multiply(percent));
		}
	}

	@Override
	public boolean reset() {
		if(isInit()) {
			mediaPlayer.dispose();
			mediaPlayer = null;
			onReset();
			return true;
		}
		return false;
	}

	@Override
	public boolean isInit() {
		return mediaPlayer != null;
	}
	
	@Override
	public void doSetVolume(double volume) {
		if(isInit()) {
			mediaPlayer.setVolume(volume);
		}
	}
	
	private void bindMediaView(MediaPlayer player) {
		if(mediaView != null) {
			mediaView.setMediaPlayer(player);
		}
	}

	@Override
	public boolean isNotPlaying() {
		if(!isInit()){
			return true;
		}
		return mediaPlayer.getStatus() == Status.UNKNOWN 
				|| mediaPlayer.getStatus() == Status.PAUSED;
	}

}
