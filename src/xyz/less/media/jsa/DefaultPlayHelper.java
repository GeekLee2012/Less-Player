package xyz.less.media.jsa;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import xyz.less.bean.Audio;

public final class DefaultPlayHelper implements IPlayHelper {
	private AudioInputStream stream;
	private byte[] buffer = new byte[65536];
	
	public DefaultPlayHelper(Audio audio) {
		try {
			stream = AudioSystem.getAudioInputStream(getAudioFile(audio));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public AudioFormat getAudioFormat() throws Exception {
		AudioFormat format = stream.getFormat();
		return format;
	}
	
	@Override
	public byte[] readNext() throws Exception {
		int len = stream.read(buffer, 0, buffer.length);
		if(len > 0) {
			byte[] dest = new byte[len];
			System.arraycopy(buffer, 0, dest, 0, len);
			return dest;
		}
		return null;
	}
	
}
