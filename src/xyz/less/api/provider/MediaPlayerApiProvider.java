package xyz.less.api.provider;

import xyz.less.api.IMediaPlayerApi;
import xyz.less.bean.Audio;
import xyz.less.media.FxMediaPlayer;

public class MediaPlayerApiProvider implements IMediaPlayerApi {
	private FxMediaPlayer mediaPlayer;
	
	@Override
	public void play() {
		mediaPlayer.play();
	}

	@Override
	public void pause() {
		mediaPlayer.play();
	}

	@Override
	public void playNext() {
		mediaPlayer.playNext();
	}

	@Override
	public void playPrev() {
		mediaPlayer.playPrevious();
	}

	@Override
	public void setVolume(double volume) {
		mediaPlayer.setVolume(volume);
	}

	@Override
	public Audio getCurrentAudio() {
		return mediaPlayer.getCurrentAudio();
	}

}
