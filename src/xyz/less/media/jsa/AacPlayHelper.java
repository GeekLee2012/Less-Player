package xyz.less.media.jsa;

import java.io.FileInputStream;

import javax.sound.sampled.AudioFormat;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;
import xyz.less.bean.Audio;

/**
 * 备用AAC Player
 */
public final class AacPlayHelper implements IPlayHelper {
	private ADTSDemultiplexer adts;
	private Decoder decoder;
	private SampleBuffer buffer = new SampleBuffer();
	private byte[] firstBytes;
	
	public AacPlayHelper(Audio audio) {
		try {
			adts = new ADTSDemultiplexer(new FileInputStream(getAudioFile(audio)));
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
			decoder.decodeFrame(adts.readNextFrame(), buffer);
			bytes = buffer.getData();
		}
		return bytes;
	}
	
}
