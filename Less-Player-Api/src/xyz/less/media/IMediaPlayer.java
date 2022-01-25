package xyz.less.media;

import java.util.Set;

import javafx.scene.media.MediaView;
import xyz.less.bean.Audio;

public interface IMediaPlayer {
	boolean isPlaying();
	void play();
	void pause();
	boolean isSeekable();
	void seek(double percent);
	void reset(boolean notify);
	void setVolume(double volume);
	double getVolume();
	Audio getCurrent();
	void setCurrent(Audio audio);
	boolean isPlayable(Audio audio);
	void addListener(IMediaPlayerListener... listeners);
	Set<String> getSuffixSet();
	void setMediaView(MediaView mediaView);
	MediaView getMediaView();
}
