package xyz.less.media;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import xyz.less.bean.Audio;
import xyz.less.util.AudioUtil;

/**
 * 基于Java Sound API实现的MediaPlayer
 */
public final class DefaultJsaPlayer extends AbstractJsaPlayer {
	private AudioInputStream audioStream;
	private BufferedInputStream bis;
	private byte[] buffer = new byte[65536];
	
	public DefaultJsaPlayer() {
		super(".wav");
		suffixMgr.addSuffixes(getDefaultSuffixSet());
	}
	
	protected void changeAudio(Audio audio) throws Exception {
		bis = new BufferedInputStream(AudioUtil.getInputStream(audio));
		audioStream = AudioSystem.getAudioInputStream(bis);
	}

	@Override
	protected boolean isEOF() {
		try {
			return audioStream.available() <= 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected byte[] readNext() throws Exception {
		int len = audioStream.read(buffer, 0, buffer.length);
		if(len > 0) {
			byte[] dest = new byte[len];
			System.arraycopy(buffer, 0, dest, 0, len);
			return dest;
		}
		return null;
	}

	@Override
	protected AudioFormat getAudioFormat() throws Exception {
		return audioStream.getFormat();
	}

}
