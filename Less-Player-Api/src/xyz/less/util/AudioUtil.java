package xyz.less.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;

import xyz.less.bean.Audio;

public final class AudioUtil {
	
	public static double bytes2Minutes(long bytes, AudioFormat format) {
		int channs = format.getChannels();
		double rate = format.getSampleRate();
		double sampleSizeInByte = format.getSampleSizeInBits() / 8;
		double bps = channs * rate * sampleSizeInByte;
		double secs = bytes / bps;
		double minutes = secs / 60D;
//		System.out.println("Minutes: " + StringUtil.toMmss(minutes));
		return minutes;
	}
	
	public static File toFile(Audio audio) {
		return Paths.get(URI.create(audio.getSource())).toFile();
	}
	
	public static InputStream getInputStream(Audio audio) {
		try {
			return new FileInputStream(toFile(audio));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
