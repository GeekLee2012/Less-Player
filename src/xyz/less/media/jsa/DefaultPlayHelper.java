package xyz.less.media.jsa;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import xyz.less.bean.Audio;
import xyz.less.service.MediaService;

public final class DefaultPlayHelper implements IPlayHelper {
	private AudioInputStream audioStream;
	private InputStream stream;
	private byte[] buffer = new byte[65536];
	
	public DefaultPlayHelper(Audio audio) {
		try {
			stream = MediaService.getInputStream(audio);
			audioStream = AudioSystem.getAudioInputStream(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public AudioFormat getAudioFormat() throws Exception {
		AudioFormat format = audioStream.getFormat();
		return format;
	}
	
	@Override
	public byte[] readNext() throws Exception {
		int len = audioStream.read(buffer, 0, buffer.length);
		if(len > 0) {
			byte[] dest = new byte[len];
			System.arraycopy(buffer, 0, dest, 0, len);
			return dest;
		}
		return null;
	}

	@Override
	public void reset() throws Exception {
		if(stream != null) {
			stream.reset();
		}
	}

	@Override
	public boolean isEOF() {
		try {
			return audioStream.available() <= 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
