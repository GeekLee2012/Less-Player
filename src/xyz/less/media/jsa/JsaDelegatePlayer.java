package xyz.less.media.jsa;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.SourceDataLine;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import xyz.less.Main;
import xyz.less.bean.Audio;
import xyz.less.engine.MediaEngine;
import xyz.less.media.AbstractDelegatePlayer;

/**
 * 基于Java Sound API的MediaPlayer
 * 现在暂时代码一团乱麻Orz
 */
//TODO
public final class JsaDelegatePlayer extends AbstractDelegatePlayer {
	private final PlayService playService = new PlayService();
	private volatile boolean paused;
	
	public JsaDelegatePlayer() {
		super(MediaEngine.SUFFIXES_2);
	}
	
	@Override
	public void play() {
		//TODO
		if(audioChanged) {
			playService.restart();
			setAudioChanged(false);
		}
		setPaused(false);
		playService.continuePlaying();
	}
	
	@Override
	public void pause() {
		setPaused(true);
	}
	
	@Override
	public void seek(double percent) {
		//Unsupported yet
		playService.doSeekPlay(percent);
	}
	
	@Override
	public boolean reset() {
		setPaused(false);
		playService.cancel();
		setCurrentAudio(null);
		onReset();
		return true;
	}
	
	@Override
	public void doSetVolume(double volume) {
		playService.doSetVolume();
	}
	
	private void setPaused(boolean value) {
		this.paused = value;
	}
	
	@Override
	public boolean isInit() {
		return currentAudio != null;
	}
	
	@Override
	public boolean isNotPlaying() {
		return paused || !playService.isTaskRunning();
	}
	
	private void onReady() {
		onReady(currentAudio, markUnsupported(copyMetadata(currentAudio)));
	}
	
	class PlayService extends Service<Void> {
		private PlayTask task;
		
		@Override
		public boolean cancel() {
			if(isTaskRunning()) {
				continuePlaying();
				task.cancel(true);
				task = null;
			}
			doSeekPlay(0);
			return super.cancel();
		}
		
		public void doSeekPlay(double percent) {
			double seekTime = currentAudio.getDuration() * percent;
			if(task != null) {
				task.setSeekTime(seekTime);
			}
		}

		//TODO
		public void doSetVolume() {
			if(Main.getAppContext().isMiniSkin()) { //Mini风格不设置volume
				return ;
			}
			try {
				if(task != null && task.line != null) {
					FloatControl ctrl = (FloatControl) task.line.getControl(Type.MASTER_GAIN);
					float max = ctrl.getMaximum();
					float min = ctrl.getMinimum();
					double limit = 0.15;
					float percent = (float)(Math.abs(volume - limit)/limit);
					float value = volume == limit ? 0 :
						(volume > limit ? max * percent : min * percent);
					value = value > max ? max : value;
					value = value < min ? min : value;
					ctrl.setValue(value);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		public boolean isTaskRunning() {
			return task != null && !task.stopped;
		}

		public void continuePlaying() {
			try {
				synchronized (task.lock) {
					task.lock.notifyAll();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		protected Task<Void> createTask() {
			if(task == null) {
				task = new PlayTask();
				doSetVolume();
			}
			return task;
		}
		
	}
	
	//TODO
	class PlayTask extends Task<Void> {
		private boolean stopped;
		private boolean end = false;
		private IPlayHelper playHelper;
		private final Object lock = new Object();
		private SourceDataLine line;
		private double seekTime;
		
		@Override
		protected Void call() throws Exception {
			doPlay(currentAudio);
			return null;
		}
		
		public void setSeekTime(double time) {
			this.seekTime = time;
		}
		
		public double getSeekTime() {
			return this.seekTime;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			stopped = true;
			stopLine();
			return super.cancel(mayInterruptIfRunning);
		}
		
		private void openLine(AudioFormat format) throws Exception {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open();
			onReady();
		}
		
		private void startLine() {
			if(line != null && !line.isActive()) {
				line.start();
				onPlaying();
			}
		}
		
		private void stopLine() {
			if(line != null && line.isActive()) {
				line.stop();
				if(!stopped) {
					onPaused();
				}
			}
		}
		
		private void closeLine() {
			if(line != null) {
				line.drain();
				line.close();
			}
		}
		
		//TODO
		private void doPlay(Audio audio) throws Exception {
			stopped = false;
			end = false;
			long totalWriteBytes = 0;
			
			playHelper = PlayerHelpers.select(audio);
			AudioFormat format = playHelper.getAudioFormat();
			openLine(format);
			while (!stopped) {
				checkPasued();
				startLine();
				byte[] srcBytes = playHelper.readNext();
				if(srcBytes == null) {
					end = true;
					break ;
				}
//				double seekTime = getSeekTime();
//				if(seekTime > 0) {
//					totalWriteBytes += srcBytes.length;
//					double current = bytes2Minutes(totalWriteBytes, format);
//					System.out.println(seekTime + " : " + current);
//					if(current < seekTime) {
//						continue ;
//					}
//				}
				int writeBytes = 0;
				if(srcBytes.length > 0) {
					writeBytes = line.write(srcBytes, 0, srcBytes.length);
				}
				totalWriteBytes += writeBytes;
				if(writeBytes > 0) {
					onCurrentChanged(bytes2Minutes(totalWriteBytes, format), currentAudio.getDuration());
				}
			}
			closeLine();
			if(end) {
				onEndOfMedia();
				stopped = true;
			}
		}

		private void checkPasued() {
			synchronized (lock) {
				while(paused) {
					try {
						stopLine();
						lock.wait();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
