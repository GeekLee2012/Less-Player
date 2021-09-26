package xyz.less.media;

import javafx.scene.media.Media;

//TODO «ø“¿¿µMedia
public interface MediaPlayerListener {
	void onReady(Media Media);
	void onPlaying();
	void onCurrentChanged(double currentMinutes, double durationMinutes);
	void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases);
	void onPaused();
	void onEndOfMedia();
	void onError();
	void onReset();
	void onNoMedia();
}
