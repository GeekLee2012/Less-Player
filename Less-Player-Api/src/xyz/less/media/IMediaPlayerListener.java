package xyz.less.media;

import java.util.Map;

import xyz.less.bean.Audio;

public interface IMediaPlayerListener {
	void onInit(Audio audio);
	void onReady(Audio audio, Map<String, Object> metadata);
	void onPlaying();
	void onCurrentChanged(double currentMinutes, double durationMinutes);
	void onSpectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases);
	void onPaused();
	void onEndOfMedia();
	void onError(Exception ex);
	void onReset();
	void onNoMedia();
	void onNoPlayableMedia();
	void onPlaylistUpdated();
}
