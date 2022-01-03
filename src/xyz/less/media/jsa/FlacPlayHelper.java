package xyz.less.media.jsa;

import java.io.FileInputStream;

import javax.sound.sampled.AudioFormat;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;

import xyz.less.bean.Audio;

public final class FlacPlayHelper implements IPlayHelper {
	private FLACDecoder decoder;
	
	public FlacPlayHelper(Audio audio) {
		try {
			decoder = new FLACDecoder(new FileInputStream(getAudioFile(audio)));
		} catch (Exception e) {
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
	
}
