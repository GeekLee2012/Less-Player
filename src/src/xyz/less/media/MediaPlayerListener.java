package xyz.less.media;

import java.util.Map;

import xyz.less.bean.Audio;

public interface MediaPlayerListener {
	void onReady(Audio audio, Map<String, Object> metadata);
	void onPlaying();
	void onCurrentChanged(double currentMinutes, double durationMinutes);
	void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases);
	void onPaused();
	void onEndOfMedia();
	void onError();
	void onReset();
	void onNoMedia();
	void onNoPlayableMedia();
}
