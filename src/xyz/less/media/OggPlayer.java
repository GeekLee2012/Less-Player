package xyz.less.media;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

import xyz.less.bean.Audio;
import xyz.less.util.AudioUtil;

/**
 * 基于Java Sound API实现的MediaPlayer
 */
public final class OggPlayer extends AbstractJsaPlayer {
	/** Ogg packet */
	private Packet joggPacket = new Packet();
	/** Ogg Page */
	private Page joggPage = new Page();
	/** Ogg Stream State */
	private StreamState joggStreamState = new StreamState();
	/**  Ogg Sync State */
	private SyncState joggSyncState = new SyncState();
	/** Orbis DSP State */
	private DspState jorbisDspState = new DspState();
	/** Orbis Block  */
	private Block jorbisBlock = new Block(jorbisDspState);
	/** Orbis Comment */
	private Comment jorbisComment = new Comment();
	/** Orbis Info */
	private Info jorbisInfo = new Info();
	/** Buffered Input Stream for reading the Ogg Stream */
	private BufferedInputStream bis;
	/** Buffer size */
	private static final int BUFSIZE = 8192;
	/** Read buffer */
	private byte[] buffer = null;
	private static int convsize = BUFSIZE * 2;
	private static byte[] convbuffer = new byte[convsize];
	
	public OggPlayer() {
		super(".ogg");
	}

	@Override
	protected void changeAudio(Audio audio) throws Exception {
		doClose();
		bis = new BufferedInputStream(AudioUtil.getInputStream(audio));
		bis.mark(Integer.MAX_VALUE);
	}
	
	@Override
	protected void doStart() throws Exception {
		bis.reset();
		initializeJOrbis();
		stopped = false;
		paused = false;
		int totalWriteBytes = 0;

		boolean chained = false;
		int bytes;
		
		// retry=RETRY;
		while (true) {
			int eos = 0;

			int index = joggSyncState.buffer(BUFSIZE);
			buffer = joggSyncState.data;
			try {
				bytes = bis.read(buffer, index, BUFSIZE);
			} catch (IOException e) {
				eos = 1;
				break;
			}
			joggSyncState.wrote(bytes);

			if (chained) {
				chained = false;
			} else {
				if (joggSyncState.pageout(joggPage) != 1) {
					if (bytes < BUFSIZE) {
						break;
					} else {
						throw new IOException("Not Ogg stream!");
					}
				}
			}
			joggStreamState.init(joggPage.serialno());
			joggStreamState.reset();

			jorbisInfo.init();
			jorbisComment.init();

			if (joggStreamState.pagein(joggPage) < 0) {
				throw new IOException("Error while reading the first page of Ogg stream!");
			}

//      retry=RETRY;

			if (joggStreamState.packetout(joggPacket) != 1) {
				throw new IOException("Error while reading the first Ogg header packet!");
			}

			if (jorbisInfo.synthesis_headerin(jorbisComment, joggPacket) < 0) {
				throw new IOException("Error while reading the first Ogg stream doest not" + "contain audio data!");
			}

			int i = 0;

			while (i < 2) {
				while (i < 2) {
					int result = joggSyncState.pageout(joggPage);
					if (result == 0)
						break; // Need more data
					if (result == 1) {
						joggStreamState.pagein(joggPage);
						while (i < 2) {
							result = joggStreamState.packetout(joggPacket);
							if (result == 0)
								break;
							if (result == -1) {
								throw new IOException("Secondary header is corrupted!");
							}
							jorbisInfo.synthesis_headerin(jorbisComment, joggPacket);
							i++;
						}
					}
				}

				index = joggSyncState.buffer(BUFSIZE);
				buffer = joggSyncState.data;
				bytes = bis.read(buffer, index, BUFSIZE);
				if (bytes == 0 && i < 2) {
					throw new IOException("Ogg file ended before all Vorbis headers!");
				}
				joggSyncState.wrote(bytes);
			}

			convsize = BUFSIZE / jorbisInfo.channels;

			jorbisDspState.synthesis_init(jorbisInfo);
			jorbisBlock.init(jorbisDspState);

			float[][][] _pcmf = new float[1][][];
			int[] _index = new int[jorbisInfo.channels];

//			SourceDataLine outputLine = getOutputLine(jorbisInfo.channels, jorbisInfo.rate);
			audioFormat = new AudioFormat(jorbisInfo.rate, 16, jorbisInfo.channels, true, false);
			openLine(audioFormat);
			// Volume set for OGG
			setVolume(volume);
			while (eos == 0) {
				while (eos == 0) {
					checkPasued();
					if (stopped) {
						eos = 1;
						break;
					}
					startLine();
					int result = joggSyncState.pageout(joggPage);
					if (result == 0)
						break; // need more data
					if (result == -1) {
						// missing or corrupt data at this page position, just continuing...
					} else {
						joggStreamState.pagein(joggPage);

						if (joggPage.granulepos() == 0) {
							chained = true;
							eos = 1;
							break;
						}

						while (true) {
							result = joggStreamState.packetout(joggPacket);
							if (result == 0)
								break; // need more data
							if (result == -1) {
								// missing or corrupt data at this page position, just continuing...
							} else {
								// we have a packet. Decode it
								int samples;
								if (jorbisBlock.synthesis(joggPacket) == 0) {
									jorbisDspState.synthesis_blockin(jorbisBlock);
								}
								while ((samples = jorbisDspState.synthesis_pcmout(_pcmf, _index)) > 0) {
									float[][] pcmf = _pcmf[0];
									int bout = (samples < convsize ? samples : convsize);

									// convert doubles to 16 bit signed ints (host order) and
									// interleave
									for (i = 0; i < jorbisInfo.channels; i++) {
										int ptr = i * 2;
										// int ptr=i;
										int mono = _index[i];
										for (int j = 0; j < bout; j++) {
											int val = (int) (pcmf[i][mono + j] * 32767.);
											if (val > 32767) {
												val = 32767;
											}
											if (val < -32768) {
												val = -32768;
											}
											if (val < 0)
												val = val | 0x8000;
											convbuffer[ptr] = (byte) (val);
											convbuffer[ptr + 1] = (byte) (val >>> 8);
											ptr += 2 * (jorbisInfo.channels);
										}
									}
//									if (gainControl != null) {
//										gainControl.setValue(gainControl.getMaximum() * getMusicVolume() / 100);
//									}
									int len = 2 * jorbisInfo.channels * bout;
									totalWriteBytes += len;
									
									line.write(convbuffer, 0, len);
									jorbisDspState.synthesis_read(bout);
									
									//TODO
									listenersMgr.onCurrentChanged(AudioUtil.bytes2Minutes(totalWriteBytes, audioFormat), audio.getDuration());
								}
							}
						}
						if (joggPage.eos() != 0)
							eos = 1;
					}
				}

				if (eos == 0) {
					index = joggSyncState.buffer(BUFSIZE);
					buffer = joggSyncState.data;
					try {
						bytes = bis.read(buffer, index, BUFSIZE);
					} catch (IOException e) {
						eos = 1;
						break;
					}
					if (bytes == -1) {
						break;
					}
					joggSyncState.wrote(bytes);
					if (bytes == 0)
						eos = 1;
				}
			}

			closeLine();
			joggStreamState.clear();
			jorbisBlock.clear();
			jorbisDspState.clear();
			jorbisInfo.clear();
			if (stopped) {
				break;
			} 
//			else { // 循环播放
//       		bis.reset();
//			}
		}
		
		joggSyncState.clear();
		doClose();
		listenersMgr.onEndOfMedia();
	}

	/**
	 * Initializes JOrbis.
	 */
	private void initializeJOrbis() {
		joggPacket = new Packet();
		joggPage = new Page();
		joggStreamState = new StreamState();
		joggSyncState = new SyncState();
		jorbisDspState = new DspState();
		jorbisBlock = new Block(jorbisDspState);
		jorbisComment = new Comment();
		jorbisInfo = new Info();

		// Initialize SyncState
		joggSyncState.init();
	}
	
	private void doClose() {
		try {
			if (bis != null) {
				bis.close();
				bis = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected AudioFormat getAudioFormat() {
		return audioFormat;
	}

	/**
	 * 固定返回false
	 */
	@Override
	protected boolean isEOF() {
		return false;
	}

	@Override
	protected byte[] readNext() throws Exception {
		throw new Exception("Unsupport method");
	}
	 
}
