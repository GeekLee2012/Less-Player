package xyz.less.media.jsa;

import javax.sound.sampled.AudioFormat;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.io.BitInputStream;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;

import xyz.less.bean.Audio;
import xyz.less.engine.MediaEngine;

public final class FlacPlayHelper implements IPlayHelper {
	private FLACDecoder decoder;
	private BitInputStream stream;
	
	public FlacPlayHelper(Audio audio) {
		try {
			decoder = new FLACDecoder(MediaEngine.getInputStream(audio));
			stream = decoder.getBitInputStream();		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public AudioFormat getAudioFormat() throws Exception {
		StreamInfo streamInfo = decoder.readStreamInfo();
		Metadata[] metadatas = decoder.readMetadata(streamInfo);
		if(metadatas != null) {
//			for(Metadata metadata : metadatas) {
//				System.out.println(metadata);
//			}
		}
		AudioFormat format = decoder.getStreamInfo().getAudioFormat();
		return format;
	}
	
	@Override
	public byte[] readNext() throws Exception {
		Frame frame = decoder.readNextFrame();
		if(frame == null) {
			return null;
		}
		ByteData byteData = new ByteData(1024);
		byte[] srcBytes = decoder.decodeFrame(frame, byteData).getData();
		return srcBytes;
	}
	
	@Override
	public boolean isEOF() {
		return decoder.isEOF();
	}

	@Override
	public void reset() throws Exception {
		if(stream != null) {
			decoder.flush();
			stream.reset();
		}
	}
	
}
