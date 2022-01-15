package xyz.less.media.jsa;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import xyz.less.bean.Audio;
import xyz.less.engine.MediaEngine;
import xyz.less.media.AbstractDelegatePlayer;

public class OggDelegatePlayer extends AbstractDelegatePlayer {
	private final PlayService playService = new PlayService();
	private OggPlayer player = new OggPlayer();
	
	public OggDelegatePlayer() {
		super(MediaEngine.SUFFIXES_3);
	}
	
	@Override
	public boolean init(Audio audio) {
		super.init(audio);
		player.addListeners(listeners);
		player.setAudio(audio);
		onReady(getCurrentAudio(), markUnsupported(copyMetadata(getCurrentAudio())));
		return true;
	}
	
	@Override
	public void play() {
		//TODO
		if(audioChanged) {
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
	public void seek(double percent) {
		
	}

	@Override
	public boolean reset() {
		player.stop();
		player.close();
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
