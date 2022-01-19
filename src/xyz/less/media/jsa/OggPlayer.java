package xyz.less.media.jsa;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.FloatControl.Type;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

import xyz.less.bean.Audio;
import xyz.less.media.AbstractDelegatePlayer;
import xyz.less.media.IMediaPlayerListener;
import xyz.less.service.MediaService;

/**
 * 
 * JHeroes CRPG Engine and Game Copyright (C) 2014 Tuomo Untinen
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see http://www.gnu.org/licenses/
 * 
 *
 * Ogg Player for playing Ogg files. Uses JOrbis library which is under LGPL.
 *
 */
public class OggPlayer {

	/**
	 * Ogg packet
	 */
	private Packet joggPacket = new Packet();
	/**
	 * Ogg Page
	 */
	private Page joggPage = new Page();
	/**
	 * Ogg Stream State
	 */
	private StreamState joggStreamState = new StreamState();
	/**
	 * Ogg Sync State
	 */
	private SyncState joggSyncState = new SyncState();

	/**
	 * Orbis DSP State
	 */
	private DspState jorbisDspState = new DspState();
	/**
	 * Orbis Block
	 */
	private Block jorbisBlock = new Block(jorbisDspState);
	/**
	 * Orbis Comment
	 */
	private Comment jorbisComment = new Comment();
	/**
	 * Orbis Info
	 */
	private Info jorbisInfo = new Info();

	/**
	 * Buffered Input Stream for reading the Ogg Stream
	 */
	private BufferedInputStream bis;

	/**
	 * Buffer size
	 */
	static final int BUFSIZE = 8192;

	/**
	 * Read buffer
	 */
	private byte[] buffer = null;

	/**
	 * 
	 */
	static int convsize = BUFSIZE * 2;
	/**
	 * 
	 */
	static byte[] convbuffer = new byte[convsize];

	private boolean needToStop = false;

	/**
	 * Music volume, default is 50%
	 */
	private static double musicVolume = 0.5;
	
	//TODO
	private volatile boolean paused = true;
	private final Object lock = new Object();
	private SourceDataLine outputLine;
	private AudioFormat audioFormat;
	private Audio audio;
	protected List<IMediaPlayerListener> listeners = new ArrayList<>();
	
	public OggPlayer() {

	}

	/**
	 * Initializes new OggPlayer from Inputstream
	 * 
	 * @param is
	 * @throws IOException
	 */
	public OggPlayer(InputStream is) throws IOException {
		if (is == null) {
			throw new IOException("Ogg Input Stream is null!");
		}
		bis = new BufferedInputStream(is);
		bis.mark(Integer.MAX_VALUE);
	}

	public void changeStream(InputStream is) throws IOException {
		needToStop = true;
		close();
		if (is == null) {
			throw new IOException("Ogg Input Stream is null!");
		}
		bis = new BufferedInputStream(is);
		bis.mark(Integer.MAX_VALUE);
	}

	/**
	 * Play the Ogg
	 */
	public void play() throws IOException {
		bis.reset();
		initializeJOrbis();
		needToStop = false;
		
		setPaused(false);
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
			outputLine = getOutputLine(jorbisInfo.channels, jorbisInfo.rate);
			// Volume set for OGG
			setVolume(getMusicVolume());
			while (eos == 0) {
				while (eos == 0) {
					checkPasued();
					if (needToStop) {
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
									
									outputLine.write(convbuffer, 0, len);
									jorbisDspState.synthesis_read(bout);
									
									//TODO
									onCurrentChanged(AbstractDelegatePlayer.bytes2Minutes(totalWriteBytes, audioFormat), audio.getDuration());
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

			outputLine.close();
			joggStreamState.clear();
			jorbisBlock.clear();
			jorbisDspState.clear();
			jorbisInfo.clear();
			if (needToStop) {
				break;
			} else {
				// 循环播放
//        bis.reset();
				onEndOfMedia();
			}
		}

		joggSyncState.clear();

		try {
			if (bis != null) {
				bis.close();
				bis = null;
			}
		} catch (Exception e) {
		}
	}

	public boolean isStopped() {
		if (bis == null) {
			return true;
		} else {
			return false;
		}
	}

	public void stop() {
		needToStop = true;
		setPaused(false);
	}

	/**
	 * Open Audio Output line with number of channels and sampling rate
	 * 
	 * @param channels
	 * @param rate
	 * @return SourceDataLine
	 * @throws IOException if fails
	 */
	private SourceDataLine getOutputLine(int channels, int rate) throws IOException {
//		AudioFormat audioFormat = new AudioFormat((float) rate, 16, channels, true, false);
		audioFormat = new AudioFormat((float) rate, 16, channels, true, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
		if (!AudioSystem.isLineSupported(info)) {
			throw new IOException("Audio line is not supported!");
		}

		SourceDataLine outputLine;
		try {
			outputLine = (SourceDataLine) AudioSystem.getLine(info);
			outputLine.open(audioFormat);
		} catch (LineUnavailableException ex) {
			throw new IOException("Audio line cannot be opened: " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			throw new IOException("Illegal argument:" + ex.getMessage());
		}
		outputLine.start();
		return outputLine;
	}

	/**
	 * Close the Ogg stream
	 */
	public void close() {
		if (bis != null) {
			try {
				bis.close();
				bis = null;
			} catch (IOException e) {
				// DO nothing
			}
		}
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

	public static double getMusicVolume() {
		return musicVolume;
	}

	public static void setMusicVolume(double musicVolume) {
		OggPlayer.musicVolume = musicVolume;
	}

	public void setPaused(boolean value) {
		this.paused = value;
		if(!paused) {
			synchronized (lock) {
				lock.notifyAll();
			}
		}
	}
	
	private void startLine() {
		if(outputLine != null && !outputLine.isActive()) {
			outputLine.start();
		}
	}
	
	private void stopLine() {
		if(outputLine != null && outputLine.isActive()) {
			outputLine.stop();
		}
	}
	
//	public void setVolume() {
//		FloatControl gainControl = null;
//		try {
//			gainControl = (FloatControl) outputLine.getControl(FloatControl.Type.VOLUME);
//			gainControl.setValue(gainControl.getMaximum() * getMusicVolume() / 100);
//		} catch (IllegalArgumentException e) {
//			try {
//				// TODO
//				gainControl = (FloatControl) outputLine.getControl(FloatControl.Type.MASTER_GAIN);
////				gainControl.setValue(gainControl.getMinimum()*((100-SoundPlayer.getSoundVolume())/100));
//			} catch (IllegalArgumentException e2) {
//				System.out.println(
//						"Your audio system does not support VOLUME or MASTER_GAIN control...Playing default volume");
//			}
//		}
//	}
	
	public void setVolume(double volume) {
		if(outputLine == null) {
			return ;
		}
		boolean success = false;
		try {
			FloatControl ctrl = (FloatControl) outputLine.getControl(Type.VOLUME);
			float max = ctrl.getMaximum();
			ctrl.setValue((float) (max * volume));
			success = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if(!success) {
				FloatControl ctrl = (FloatControl) outputLine.getControl(Type.MASTER_GAIN);
				float max = ctrl.getMaximum();
				float min = ctrl.getMinimum();
				double limit = 0.15;
				float percent = (float)(Math.abs(volume - limit)/limit);
				float value = volume == limit ? 0 :
					(volume > limit ? max * percent : min * percent);
				value = value > max ? max : value;
				value = value < min ? min : value;
				ctrl.setValue(value);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkPasued() {
		synchronized (lock) {
			while(paused) {
				try {
					stopLine();
					lock.wait();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean isNotPlaying() {
		return isStopped() || paused;
	}

	public void setAudio(Audio audio) {
		this.audio = audio;
		try {
			changeStream(MediaService.getInputStream(audio));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addListener(IMediaPlayerListener... listeners) {
		addListeners(Arrays.asList(listeners));
	}
	
	public void addListeners(Collection<IMediaPlayerListener> listeners) {
		if(listeners != null) {
			for(IMediaPlayerListener listener : listeners) {
				if(!this.listeners.contains(listener)) {
					this.listeners.add(listener);
				}
			}
		}
	}
	
	protected void notifyAllListeners(Consumer<? super IMediaPlayerListener> action) {
		listeners.forEach(action);
	}
	
	private void onCurrentChanged(double current, double duration) {
		notifyAllListeners(e -> e.onCurrentChanged(current, duration));
	}
	
	private void onEndOfMedia() {
		notifyAllListeners(e -> e.onEndOfMedia());
	}
}
