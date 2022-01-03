package xyz.less.media.jsa;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;

import xyz.less.bean.Audio;

public interface IPlayHelper {
	AudioFormat getAudioFormat() throws Exception;
	byte[] readNext() throws Exception;
	
	default File getAudioFile(Audio audio) {
		return Paths.get(URI.create(audio.getSource())).toFile();
	}
}
