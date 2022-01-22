package xyz.less.media.jsa;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import xyz.less.bean.Audio;
import xyz.less.media.AbstractDelegatePlayer;
import xyz.less.service.MediaService;

public class OggDelegatePlayer extends AbstractDelegatePlayer {
	private final PlayService playService = new PlayService();
	private OggPlayer player = new OggPlayer();
	
	public OggDelegatePlayer() {
		super(MediaService.SUFFIXES_3);
	}
	
	@Override
	public boolean init(Audio audio) {
		super.init(audio);
		player.addListeners(listeners);
		onReady(getCurrentAudio(), markUnsupported(copyMetadata(getCurrentAudio())));
		return true;
	}
	
	@Override
	public void play() {
		//TODO
		if(audioChanged) {
			player.setAudio(getCurrentAudio());
			playService.restart();
			setAudioChanged(false);
		}
		player.setPaused(false);
		onPlaying();
	}

	@Override
	public void pause() {
		player.setPaused(true);
		onPaused();
	}
	
	@Override
	public boolean isSeekable() {
		return false;
	}

	@Override
	public void seek(double percent) {
		//Unsupported yet
	}

	@Override
	public boolean reset() {
		player.stop();
		playService.cancel();
		setCurrentAudio(null);
		onReset();
		return false;
	}

	@Override
	public boolean isNotPlaying() {
		return player.isNotPlaying();
	}

	@Override
	protected void doSetVolume(double volume) {
		player.setVolume(volume);
	}
	
	class PlayService extends Service<Void> {
		private PlayTask task;
		
		@Override
		public boolean cancel() {
			if(task != null) {
				task.cancel(true);
				task = null;
			}
			return super.cancel();
		}
		
		@Override
		protected Task<Void> createTask() {
			if(task == null) {
				task = new PlayTask();
			}
			return task;
		}
		
	}
	
	//TODO
	class PlayTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			player.play();
			return null;
		}
		
	}

}
