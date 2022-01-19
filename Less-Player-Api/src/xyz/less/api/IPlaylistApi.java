package xyz.less.api;

import java.util.Collection;

import xyz.less.bean.Audio;

public interface IPlaylistApi extends IApi {
	void add(Audio audio);
	void addAll(Collection<Audio> c);
	void clear();
	int size();
	int indexOf(Audio audio);
	void loadFrom(String path);
}
