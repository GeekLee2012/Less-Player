package xyz.less.media;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;
import xyz.less.bean.Audio;
import xyz.less.util.AudioUtil;

/**
 * 基于Java Sound API实现的MediaPlayer <br>
 * JAAD.jar目前不太好用，存在兼容性问题(与其他发生冲突) <br>
 * 引入后DefaultJsaPlayer(Java原生支持的API)连wav格式都无法正常播放 <br>
 */
@Deprecated
public final class AacPlayer extends AbstractJsaPlayer {
	private ADTSDemultiplexer adts;
	private Decoder decoder;
	private InputStream stream;
	private SampleBuffer buffer = new SampleBuffer();
	private byte[] firstBytes;
	private boolean eof;
	
	public AacPlayer() {
		super(".aac");
	}

	@Override
	protected void changeAudio(Audio audio) throws Exception {
		stream = AudioUtil.getInputStream(audio);
		adts = new ADTSDemultiplexer(stream);
		decoder = new Decoder(adts.getDecoderSpecificInfo());
		firstBytes = readNext();
	}
	
	@Override
	protected AudioFormat getAudioFormat() throws Exception {
		AudioFormat format = new AudioFormat(buffer.getSampleRate(), buffer.getBitsPerSample(), 
				buffer.getChannels(), true, true);
		return format;
	}

	@Override
	protected boolean isEOF() {
		return eof;
	}

	@Override
	protected byte[] readNext() throws Exception {
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

}
