package xyz.less.media.jsa;

import javax.sound.sampled.AudioFormat;

public interface IPlayHelper {
	AudioFormat getAudioFormat() throws Exception;
	byte[] readNext() throws Exception;
	void reset() throws Exception;
	boolean isEOF();
}
