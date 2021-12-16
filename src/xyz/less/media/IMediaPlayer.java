package xyz.less.media;

//TODO 仅仅画个饼而已
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
