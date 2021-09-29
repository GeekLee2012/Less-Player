package xyz.less.media;

import java.io.File;
import java.io.FileInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.util.ByteData;

/**
 * Java Sound API Media Player
 */
public class JsaMediaPlayer {
	
	public JsaMediaPlayer() {
		
	}
	
	public void play() {
		
	}
	
	public void pause() {
		
	}
	
	public void seek() {
		
	}

	public static void testWav(String source) throws Exception {
		File file = new File(source);
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);
		AudioFormat format = stream.getFormat();
//		System.out.println(format);
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
		System.out.println("音乐播放结束~");
	}
	
	public static void testFlac(String source) throws Exception {
		File file = new File(source);
		FLACDecoder decoder = new FLACDecoder(new FileInputStream(file));
		decoder.readStreamInfo();
		AudioFormat format = decoder.getStreamInfo().getAudioFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		byte[] buf = new byte[65536];
		line.open();
		line.start();
		while (true) {
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
		System.out.println("音乐播放结束~");
	}
	
	public static void main(String[] args) throws Exception {
		String[] source = { 
				"E:/MyMusics/久违的旋律/A Little Love.mp3",
				"E:/MyWorkspaces/Fxspace/audio/黄磊 - 年华似水.wav",
				"E:/MyWorkspaces/Fxspace/audio/谭维维 - 如果有来生.flac",
				"E:/MyWorkspaces/Fxspace/audio/CD1 - 13 我的歌声里.wav"
			};
//		testWav(source[1]);
		testFlac(source[2]);
	}
}
