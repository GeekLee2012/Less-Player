package xyz.less.media;

import javax.sound.sampled.AudioFormat;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.frame.Frame;
import org.kc7bfi.jflac.metadata.Metadata;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;

import xyz.less.bean.Audio;
import xyz.less.util.AudioUtil;

/**
 * 基于Java Sound API实现的MediaPlayer
 */
public final class FlacPlayer extends AbstractJsaPlayer {
	protected FLACDecoder decoder;
	
	public FlacPlayer() {
		super(".flac");
	}
	
	@Override
	protected void changeAudio(Audio audio) throws Exception{
		decoder = new FLACDecoder(AudioUtil.getInputStream(audio));
	}
	
	@Override
	protected AudioFormat getAudioFormat() throws Exception {
		StreamInfo streamInfo = decoder.readStreamInfo();
		Metadata[] metadatas = decoder.readMetadata(streamInfo);
		if(metadatas != null) {
//			for(Metadata metadata : metadatas) {
//				System.out.println(metadata);
//			}
		}
		return streamInfo.getAudioFormat();
	}

	@Override
	protected boolean isEOF() {
		return decoder.isEOF();
	}

	@Override
	protected byte[] readNext() throws Exception {
		Frame frame = decoder.readNextFrame();
		if(frame == null) {
			return null;
		}
		ByteData byteData = new ByteData(1024);
		byte[] srcBytes = decoder.decodeFrame(frame, byteData).getData();
		return srcBytes;
	}

}
