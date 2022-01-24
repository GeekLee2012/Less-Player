package xyz.less.media;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.SourceDataLine;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.media.MediaView;
import xyz.less.bean.AppContext;
import xyz.less.bean.Audio;
import xyz.less.util.AudioUtil;

/**
 * 基于Java Sound API实现的MediaPlayer
 */
public abstract class AbstractJsaPlayer extends Service<Audio> implements IMediaPlayer {
	protected MediaListenerManager listenersMgr = new MediaListenerManager();
	protected AudioSuffixManager suffixMgr = new AudioSuffixManager();
	protected Audio audio;
	protected volatile boolean paused = true;
	protected volatile boolean stopped;
	protected volatile boolean end = false;
	protected double volume;
	protected SourceDataLine line;
	protected AudioFormat audioFormat;
	protected final Object lock = new Object();
	
	public AbstractJsaPlayer(String... suffixes) {
		suffixMgr.setSuffixes(suffixes);
	}
	
	public void setCurrent(Audio audio) {
		this.audio = audio;
		try {
			if(audio != null) {
				reset(true);
				changeAudio(audio);
			}
		} catch (Exception e) {
			e.printStackTrace();
			listenersMgr.onError();
		}
	}
	
	protected SourceDataLine openLine(AudioFormat format) throws Exception {
		this.audioFormat = format; 
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open();
		listenersMgr.onReady(audio, markUnsupported());
		return line;
	}
	
	protected boolean isLineActive() {
		return line != null && line.isActive();
	}
	
	protected void startLine() {
		if(!isLineActive()) {
			line.start();
			listenersMgr.onPlaying();
		}
	}
	
	protected void stopLine() {
		if(isLineActive()) {
			line.stop();
			if(paused) {
				listenersMgr.onPaused();
			}
		}
	}
	
	protected void closeLine() {
		if(line != null) {
			line.drain();
			line.close();
			line = null;
			if(end) {
				listenersMgr.onEndOfMedia();
				end = false;
			}
		}
	}
	
	public Audio getCurrent() {
		return audio;
	}
	
	protected void doStart() throws Exception {
		stopped = false;
		end = false;
		paused = false;
		long totalWriteBytes = 0;
		
		openLine(getAudioFormat());
		doSetVolume(volume);
		while (!stopped) {
			checkPasued();
			startLine();
			byte[] srcBytes = readNext();
			if(isEOF() || srcBytes == null) {
				stopped = true;
				end = true;
				break ;
			}
			totalWriteBytes += srcBytes.length;
			double current = AudioUtil.bytes2Minutes(totalWriteBytes, audioFormat);
			int writeBytes = line.write(srcBytes, 0, srcBytes.length);
			if(writeBytes > 0) {
				listenersMgr.onCurrentChanged(current, audio.getDuration());
			}
		}
		closeLine();
	}
	
	public void play() {
		if(audio == null) {
			return ;
		}
		if(line == null) {
			restart();
		} else if(!isPlaying()) {
			paused = false;
			try {
				synchronized (lock) {
					lock.notifyAll();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void pause() {
		if(isPlaying()) {
			paused = true;
		}
	}
	
	@Override
	public double getVolume() {
		return volume;
	}
	
	@Override
	public void setVolume(double volume) {
		this.volume = volume;
		doSetVolume(volume);
	}
	
	protected void doSetVolume(double volume) {
		try {
			if(line != null) {
				FloatControl ctrl = (FloatControl) line.getControl(Type.MASTER_GAIN);
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
	
	@Override
	public boolean isSeekable() {
		//由子类实现
		return false;
	}
	
	@Override
	public void seek(double percent) {
		//由子类实现
	}
	
	@Override
	public boolean isPlaying() {
		return audio != null && isLineActive();
	}
	
	@Override
	public boolean isPlayable(Audio audio) {
		return suffixMgr.isPlayable(audio);
	}

	@Override
	public void reset(boolean notify) {
		stopped = true;
		paused = false;
		stopLine();
		closeLine();
		if(notify) {
			listenersMgr.onReset();
		}
	}
	
	@Override
	public void addListener(IMediaPlayerListener... listeners) {
		listenersMgr.addListener(listeners);
	}
	
	@Override
	public Set<String> getSuffixSet() {
		return suffixMgr.getSuffixSet();
	}
	
	protected static Set<String> getDefaultSuffixSet() {
		Set<String> types = new HashSet<>();
		Arrays.asList(AudioSystem.getAudioFileTypes()).forEach(t -> {
			types.add("." + t.getExtension());
		});
		return types;
	}
	
	protected void checkPasued() {
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
	
	protected Map<String, Object> markUnsupported() {
		return markUnsupported(Metadatas.copyFrom(audio));
	}
	
	//TODO
	protected Map<String, Object> markUnsupported(Map<String, Object> metadata) {
		if(!AppContext.get().isMiniSkin()) {
			Metadatas.putExtra(metadata, "<暂不支持: 进度控制、频谱>");
		}
		return metadata;
	}
	
	@Override
	public void setMediaView(MediaView mediaView) {
	}
	
	@Override
	public MediaView getMediaView() {
		return null;
	}
	
	@Override
	protected Task<Audio> createTask() {
		return  new Task<Audio>() {
			@Override
			protected Audio call() throws Exception {
				try {
					doStart();
				} catch (Exception e) {
					e.printStackTrace();
					listenersMgr.onError();
				}
				return null;
			}
		};
	}
	
	protected abstract void changeAudio(Audio audio) throws Exception;
	
	protected abstract AudioFormat getAudioFormat() throws Exception;

	protected abstract boolean isEOF();

	protected abstract byte[] readNext() throws Exception;
	
}
