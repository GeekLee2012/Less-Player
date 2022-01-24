package xyz.less.api.provider;

import xyz.less.api.IMediaPlayerApi;
import xyz.less.bean.Audio;
import xyz.less.service.IMediaService;

public class MediaPlayerApiProvider implements IMediaPlayerApi {
	private IMediaService mediaService;
	
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
		if(mediaService != null) {
			mediaService.setVolume(volume);
		}
	}

	@Override
	public Audio getCurrent() {
		if(mediaService != null) {
			return mediaService.getCurrent();
		}
		return null;
	}
	 
}
