package xyz.less.media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;

import javafx.scene.media.MediaView;
import xyz.less.bean.AppContext;
import xyz.less.bean.Audio;
import xyz.less.util.StringUtil;

public abstract class AbstractDelegatePlayer implements IDelegatePlayer {
	protected List<String> suffixes = new ArrayList<>();
	protected List<IMediaPlayerListener> listeners = new ArrayList<>();
	protected MediaView mediaView;
	protected Audio currentAudio;
	protected boolean audioChanged;
	protected double volume;
	
	public AbstractDelegatePlayer(String... suffixes) {
		addSuffixes(suffixes);
	}
	
	protected void addSuffixes(String... suffixes) {
		if(suffixes != null) {
			addSuffixes(Arrays.asList(suffixes));
		}
	}
	
	protected void addSuffixes(Collection<String> suffixes) {
		if(suffixes != null) {
			for(String suffix : suffixes) {
				this.suffixes.add(suffix);
			}
		}
	}
	
	@Override
	public boolean init(Audio audio) {
		setCurrentAudio(audio);
		return true;
	}
	
	@Override
	public boolean isInit() {
		return currentAudio != null;
	}
	
	@Override
	public boolean isPlayable(Audio audio) {
		String source = StringUtil.trim(audio.getSource());
		for(String suffix : suffixes) {
			if(source.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}
	
	public void setCurrentAudio(Audio audio) {
		 this.currentAudio = audio;
	}
	
	@Override
	public Audio getCurrentAudio() {
		return currentAudio;
	}
	
	@Override
	public void setVolume(double volume) {
		this.volume = volume;
		doSetVolume(this.volume);
	}
	
	protected abstract void doSetVolume(double volume);

	@Override
	public void setAudioChanged(boolean audioChanged) {
		this.audioChanged = audioChanged;
	}
	
	@Override
	public void setMediaView(MediaView mediaView) {
		this.mediaView = mediaView;
	}
	
	protected Map<String, Object> copyMetadata(Audio audio) {
		Map<String, Object> metadata = new HashMap<>();
		Metadatas.putTitle(metadata, audio.getTitle());
		Metadatas.putArtist(metadata, audio.getArtist());
		Metadatas.putAlbum(metadata, audio.getAlbum());
		Metadatas.putCoverArt(metadata, audio.getCoverArt());
		Metadatas.putDuration(metadata, audio.getDuration());
		return metadata;
	}
	
	//TODO
	protected Map<String, Object> markUnsupported(Map<String, Object> metadata) {
		if(!AppContext.get().isMiniSkin()) {
			Metadatas.putExtra(metadata, "<暂不支持: 进度控制、频谱>");
		}
		return metadata;
	}
	
	public static double bytes2Minutes(long bytes, AudioFormat format) {
		int channs = format.getChannels();
		double rate = format.getSampleRate();
		double sampleSizeInByte = format.getSampleSizeInBits() / 8;
		double bps = channs * rate * sampleSizeInByte;
		double secs = bytes / bps;
		double minutes = secs / 60D;
//		System.out.println("Minutes: " + StringUtil.toMmss(minutes));
		return minutes;
	}
	
	@Override
	public void addListener(IMediaPlayerListener... listeners) {
		if(listeners != null) {
			for(IMediaPlayerListener listener : listeners) {
				if(!this.listeners.contains(listener)) {
					this.listeners.add(listener);
				}
			}
		}
	}
	
	/****** NotifyAll MediaPlayerListeners *****/
	protected void notifyAllListeners(Consumer<? super IMediaPlayerListener> action) {
		listeners.forEach(action);
	}
	
	public void onReady(Audio audio, Map<String, Object> metadata) {
		notifyAllListeners(e -> e.onReady(audio, metadata));
	}

	public void onPlaying() {
		notifyAllListeners(e -> e.onPlaying());
	}

	public void onCurrentChanged(double currentMinutes, double durationMinutes) {
		notifyAllListeners(e -> e.onCurrentChanged(currentMinutes, durationMinutes));
	}

	public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		notifyAllListeners(e -> e.spectrumDataUpdate(timestamp, duration, magnitudes, phases));
	}

	public void onPaused() {
		notifyAllListeners(e -> e.onPaused());
	}

	public void onEndOfMedia() {
		notifyAllListeners(e -> e.onEndOfMedia());
	}

	public void onError() {
		notifyAllListeners(e -> e.onError());
	}

	public void onReset() {
		notifyAllListeners(e -> e.onReset());
	}

	public void onNoMedia() {
		notifyAllListeners(e -> e.onNoMedia());
	}

	public void onNoPlayableMedia() {
		notifyAllListeners(e -> e.onNoPlayableMedia());
	}
	
}
