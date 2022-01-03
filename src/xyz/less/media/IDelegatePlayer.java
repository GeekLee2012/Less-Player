package xyz.less.media;

import javafx.scene.media.MediaView;
import xyz.less.bean.Audio;

public interface IDelegatePlayer {
	boolean init(Audio audio);
	void play();
	void pause();
	void seek(double percent);
	boolean reset();
	boolean isPlayable(Audio audio);
	boolean isInit();
	boolean isNotPlaying();
	void setVolume(double volume);
	void addListener(IMediaPlayerListener... listeners);
	void setMediaView(MediaView mediaView);
	Audio getCurrentAudio();
	void setAudioChanged(boolean audioChanged);
}
