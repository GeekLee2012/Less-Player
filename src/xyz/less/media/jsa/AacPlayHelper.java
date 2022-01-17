package xyz.less.media.jsa;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;
import xyz.less.bean.Audio;
import xyz.less.engine.MediaEngine;

/**
 * 备用AAC Player
 */
public final class AacPlayHelper implements IPlayHelper {
	private ADTSDemultiplexer adts;
	private Decoder decoder;
	private InputStream stream;
	private SampleBuffer buffer = new SampleBuffer();
	private byte[] firstBytes;
	private boolean eof;
	
	public AacPlayHelper(Audio audio) {
		try {
			stream = MediaEngine.getInputStream(audio);
			adts = new ADTSDemultiplexer(stream);
			decoder = new Decoder(adts.getDecoderSpecificInfo());
			firstBytes = readNext();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public AudioFormat getAudioFormat() throws Exception {
		AudioFormat format = new AudioFormat(buffer.getSampleRate(), buffer.getBitsPerSample(), buffer.getChannels(), true, true);
		return format;
	}
	
	@Override
	public byte[] readNext() throws Exception {
		byte[] bytes = null;
		if(firstBytes != null) {
			bytes = firstBytes;
			firstBytes = null;
		} else {
			byte[] frameDatas = adts.readNextFrame();
			if(frameDatas == null) {
				eof = true;
				return null;
			}
			decoder.decodeFrame(frameDatas, buffer);
			bytes = buffer.getData();
		}
		return bytes;
	}
	
	@Override
	public void reset() throws Exception {
		if(stream != null) {
			stream.reset();
		}
	}

	@Override
	public boolean isEOF() {
		return eof;
	}
}
