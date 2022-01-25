package xyz.less.api.provider;

import java.util.Collection;

import xyz.less.api.IMediaPlayerApi;
import xyz.less.api.IPlaylistApi;
import xyz.less.bean.Audio;
import xyz.less.graphic.control.SliderBar;
import xyz.less.service.IMediaService;

public class MediaPlayerApiProvider implements IMediaPlayerApi, IPlaylistApi {
	private IMediaService mediaService;
	private SliderBar volumeSlider;
	
	@Override
	public void play() {
		if(mediaService != null) {
			mediaService.play();
		} 
	}

	@Override
	public void pause() {
		if(mediaService != null) {
			mediaService.pause();
		}
	}

	@Override
	public void playNext() {
		if(mediaService != null) {
			mediaService.playNext();
		}
	}

	@Override
	public void playPrev() {
		if(mediaService != null) {
			mediaService.playPrev();
		}
	}

	@Override
	public void setVolume(double volume) {
		if(volumeSlider != null) {
			volumeSlider.setValue(volume);
		}
	}

	@Override
	public Audio getCurrent() {
		if(mediaService == null) {
			return null;
		}
		return mediaService.getCurrent();
	}

	@Override
	public void add(Audio o) {
		if(mediaService != null) {
			mediaService.addAll(o);
		}
	}

	@Override
	public void addAll(Collection<Audio> c) {
		if(mediaService != null) {
			mediaService.addAll(c);
		}
	}

	@Override
	public void clear() {
		if(mediaService != null) {
			mediaService.removeAll(true);
		}
	}

	@Override
	public int size() {
		if(mediaService == null) {
			return 0;
		}
		return mediaService.getPlaylist().size();
	}

	@Override
	public int indexOf(Audio audio) {
		if(mediaService == null) {
			return -1;
		}
		return mediaService.getPlaylist().indexOf(audio);
	}

	@Override
	public void loadFrom(String path) {
		if(mediaService != null) {
			//TODO
		}
	}
	 
}
