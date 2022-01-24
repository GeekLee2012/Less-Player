package xyz.less.api;

import xyz.less.bean.Audio;

public interface IMediaPlayerApi extends IApi {
	void play();
	void pause();
	void playNext();
	void playPrev();
	void setVolume(double volume);
	Audio getCurrent();
}
