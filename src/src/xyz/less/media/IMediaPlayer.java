package xyz.less.media;

public interface IMediaPlayer {
	void init();
	void play();
	void pause();
	void stop();
	void seek(long secords);
	void setVolume(double value);
	double getVolume();
	double getDuration();
	void setStatus();
	void getStatus();
	void addListener(MediaPlayerListener listener);
}
