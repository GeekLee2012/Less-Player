package xyz.less.media;

import java.util.Set;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import xyz.less.bean.Audio;

/**
 * 基于JavaFX实现的MediaPlayer
 */
public final class DefaultFxPlayer implements IMediaPlayer {
	protected MediaListenerManager listenersMgr = new MediaListenerManager();
	protected AudioSuffixManager suffixMgr = new AudioSuffixManager();
	protected MediaPlayer player;
	protected MediaView mediaView;
	protected Audio audio;
	protected double volume;
	
	public DefaultFxPlayer() {
		suffixMgr.setSuffixes(".mp3", ".wav", ".aac", ".m4a");
	}
	
	@Override
	public boolean isPlaying() {
		return player != null && 
				player.getStatus() == Status.PLAYING;
	}

	@Override
	public void play() {
		if(player != null && !isPlaying()) {
			player.play();
		}
	}

	@Override
	public void pause() {
		if(isPlaying()) {
			player.pause();
		}
	}

	@Override
	public boolean isSeekable() {
		return player != null;
	}

	@Override
	public void seek(double percent) {
		if (isSeekable()) {
			Duration duration = getAdjustDuration();
			player.seek(duration.multiply(percent));
		}
	}

	private Duration getAdjustDuration() {
		Duration duration = player.getMedia().getDuration();
		try {
			double value = audio.getDuration();
			if (value > 0) {
				duration = duration.toMinutes() < 100 ? duration : Duration.minutes(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return duration;
	}

	@Override
	public void reset(boolean notify) {
		if(player != null) {
			player.dispose();
			player = null;
			if(notify) {
				listenersMgr.onReset();
			}
		}
	}

	@Override
	public void setVolume(double volume) {
		this.volume = volume;
		if(player != null) {
			player.setVolume(volume);
		}
	}

	@Override
	public double getVolume() {
		return volume;
	}

	@Override
	public Audio getCurrent() {
		return audio;
	}

	@Override
	public void setCurrent(Audio audio) {
		this.audio = audio;
		changeAudio(audio);
	}

	@Override
	public boolean isPlayable(Audio audio) {
		return suffixMgr.isPlayable(audio);
	}

	@Override
	public void addListener(IMediaPlayerListener... listeners) {
		listenersMgr.addListener(listeners);
	}
	
	private void changeAudio(Audio audio) {
		reset(true);
		try {
			Media media = new Media(audio.getSource());
			player = new MediaPlayer(media);
			bindMediaView(player);
			player.setVolume(volume);
			
			player.setAudioSpectrumListener((double timestamp, double duration, 
					float[] magnitudes, float[] phases) -> 
				listenersMgr.onSpectrumDataUpdate(timestamp, duration, magnitudes, phases));
			
			player.currentTimeProperty().addListener((o,ov,nv) -> {
				Duration duration = getAdjustDuration();
				listenersMgr.onCurrentChanged(nv.toMinutes(), duration.toMinutes());
			});

			player.setOnReady(() -> listenersMgr.onReady(audio, media.getMetadata()));
			player.setOnPlaying(() -> listenersMgr.onPlaying());
			player.setOnPaused(() -> listenersMgr.onPaused());
			player.setOnEndOfMedia(() -> listenersMgr.onEndOfMedia());
			player.setOnError(() -> listenersMgr.onError(new RuntimeException("未知异常")));
		} catch(Exception e) {
			e.printStackTrace();
			listenersMgr.onError(e);
		}
	}
	
	private void bindMediaView(MediaPlayer player) {
		if(mediaView != null) {
			mediaView.setMediaPlayer(player);
		}
	}

	@Override
	public Set<String> getSuffixSet() {
		return suffixMgr.getSuffixSet();
	}

	@Override
	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}
	
	@Override
	public MediaView getMediaView() {
		return mediaView;
	}
}
