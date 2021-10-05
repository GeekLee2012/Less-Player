package xyz.less.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.util.ByteData;

import xyz.less.bean.Audio;

/**
 * Java Sound API Media Player
 */
public class JsaMediaPlayer {
	private List<MediaPlayerListener> playerListeners = new ArrayList<>();
	private PlaybackQueue playbackQueue;
	private double volume = 0.5; 
	private boolean reset;
	
	public JsaMediaPlayer(PlaybackQueue playbackQueue) {
		setPlaybackQueue(playbackQueue);
	}

	public void addPlayerListener(MediaPlayerListener listener) {
		this.playerListeners.add(listener);
	}

	public void setPlaybackQueue(PlaybackQueue playbackQueue) {
		this.playbackQueue = playbackQueue;
	}
	
	public void play() {
		if(!playbackQueue.isEnable()) {
			return ;
		}
		try {
			playFlac(getCurrentSource());
			playerListeners.forEach(listener -> {
				listener.onEndOfMedia();
			});
		} catch (Exception e) {
			e.printStackTrace();
			playerListeners.forEach(listener -> {
				listener.onError();
			});
		}
	}
	
	public void pause() {
		//Unsupported yet!
	}
	
	public void seek() {
		//Unsupported yet!
	}

	public void playWav(String source) throws Exception {
		File file = new File(source);
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);
		AudioFormat format = stream.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		int bufferSize = 65536;
		byte[] buffer = new byte[bufferSize];
		line.open();
		line.start();
		int len = 0;
		
		while (len != -1) {
			len = stream.read(buffer, 0, buffer.length);
			if(len > 0) {
				line.write(buffer, 0, len);
			}
		}
		line.drain();
		line.close();
	}
	
	public void playFlac(String source) throws Exception {
		File file = Paths.get(URI.create(source)).toFile();
		FLACDecoder decoder = new FLACDecoder(new FileInputStream(file));
		decoder.readStreamInfo();
		playerListeners.forEach(listener -> {
			listener.onReady(playbackQueue.getCurrentAudio(), copyMetadata());
		});
		AudioFormat format = decoder.getStreamInfo().getAudioFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		byte[] buf = new byte[65536];
		line.open();
		line.start();
		playerListeners.forEach(listener -> {
			listener.onPlaying();
		});
		while (true && !reset) {
			Frame frame = decoder.readNextFrame();
			if(frame == null) {
				break ;
			}
			ByteData byteData = new ByteData(1024);
			byteData = decoder.decodeFrame(frame, byteData);
			int len = byteData.getData().length;
			if(len > 0) {
				System.arraycopy(byteData.getData(), 0, buf, 0, len);
				line.write(buf, 0, len);
			}
		}
		line.drain();
		line.close();
	}
	
	//TODO
	private Map<String, Object> copyMetadata() {
		Map<String, Object> metadata = new HashMap<>();
		Audio audio = playbackQueue.getCurrentAudio();
		metadata.put(Metadatas.TITLE, audio.getTitle());
		metadata.put(Metadatas.ARTIST, audio.getArtist());
		metadata.put(Metadatas.ALBUM, audio.getAlbum());
		metadata.put(Metadatas.COVER_ART, audio.getCoverArt());
		metadata.put(Metadatas.DURATION, audio.getDuration());
		return metadata;
	}

	private String getCurrentSource() {
		Audio audio = playbackQueue.getCurrentAudio();
		return audio != null ? audio.getSource() : null; 
	}
	
	public Future<?> loadFrom(File file) throws IOException {
		resetPlaybackQueue();
		return playbackQueue.loadFrom(file);
	}
	
	public void resetPlaybackQueue() {
		playbackQueue.reset();
		playerListeners.forEach(listener -> {
			listener.onNoMedia();
		});
		resetPlayer();
	}

	private void resetPlayer() {
		reset = true;
	}
	
	public double getVolume() {
		return volume;
	}

	public void setVolume(double value) {
		this.volume = value;
	}
}
