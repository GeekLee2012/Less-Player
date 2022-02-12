package xyz.less.media;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import xyz.less.async.AsyncServices;
import xyz.less.bean.Audio;

public final class MediaListenerManager {
	private final List<IMediaPlayerListener> listeners = new ArrayList<>();
	
	public void addListener(IMediaPlayerListener... listeners) {
		if(listeners != null) {
			addListeners(Arrays.asList(listeners));
		}
	}
	
	public void addListeners(Collection<IMediaPlayerListener> listeners) {
		if(listeners != null) {
			for(IMediaPlayerListener listener : listeners) {
				if(!this.listeners.contains(listener)) {
					this.listeners.add(listener);
				}
			}
		}
	}
	
	private void notifyAll(Consumer<? super IMediaPlayerListener> action) {
		try {
			AsyncServices.runLater(() -> listeners.forEach(action));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onReady(Audio audio, Map<String, Object> metadata) {
		notifyAll(e -> e.onReady(audio, metadata));
	}

	public void onPlaying() {
		notifyAll(e -> e.onPlaying());
	}

	public void onCurrentChanged(double currentMinutes, double durationMinutes) {
		notifyAll(e -> e.onCurrentChanged(currentMinutes, durationMinutes));
	}

	public void onSpectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		notifyAll(e -> e.onSpectrumDataUpdate(timestamp, duration, magnitudes, phases));
	}

	public void onPaused() {
		notifyAll(e -> e.onPaused());
	}

	public void onEndOfMedia() {
		notifyAll(e -> e.onEndOfMedia());
	}

	public void onError(Exception ex) {
		notifyAll(e -> e.onError(ex));
	}

	public void onReset() {
		notifyAll(e -> e.onReset());
	}

	public void onNoMedia() {
		notifyAll(e -> e.onNoMedia());
	}

	public void onNoPlayableMedia() {
		notifyAll(e -> e.onNoPlayableMedia());
	}
}
