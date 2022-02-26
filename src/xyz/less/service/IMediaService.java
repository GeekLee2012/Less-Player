package xyz.less.service;

import xyz.less.bean.Audio;
import xyz.less.media.IMediaPlayer;
import xyz.less.media.PlaybackQueue.PlayMode;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface IMediaService extends IMediaPlayer {
	boolean isInit();
	void playNext();
	void playPrev();
	PlayMode getPlayMode();
	void setPlayMode(PlayMode mode);
	void addAll(Audio... audios);
	void addAll(Collection<Audio> audios);
	List<Audio> getPlaylist();
	void removeAll(boolean notify);
	int getCurrentIndex();
	Map<String, Object> getCurrentMetadata();
	Future<?> loadFrom(String url);
	Future<?> syncMetadatas();
}
