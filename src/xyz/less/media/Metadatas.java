package xyz.less.media;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import xyz.less.util.StringUtil;

public final class Metadatas {
	public static final String TITLE = "title";
	public static final String ARTIST = "artist";
	public static final String ALBUM = "album";
	public static final String COVER_ART = "image";
	public static final String DURATION = "duration";

	public static Map<String, Object> readFrom(File file) {
		Map<String, Object> metadata = new HashMap<>();
		String title = null;
		String artist = null;
		String album = null;
		Object coverArt = null;
		double duration = 0D;
		
		AudioHeader audioHeader = null;
		Tag tag = null;
		try {
			AudioFile audioFile = AudioFileIO.read(file);
			if(audioFile != null) {
				tag = audioFile.getTag();
				audioHeader = audioFile.getAudioHeader();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(tag != null) {
			title = tag.getFirst(FieldKey.TITLE);
			artist = tag.getFirst(FieldKey.ARTIST);
			album = tag.getFirst(FieldKey.ALBUM);
			Artwork artwork = tag.getFirstArtwork();
			if(artwork != null) {
				coverArt = artwork.getBinaryData();
			}
		}
		if(audioHeader != null) {
			duration = audioHeader.getTrackLength() / 60D;
		}
		metadata.put(TITLE, StringUtil.iso88591ToUtf8(title));
		metadata.put(ARTIST, StringUtil.iso88591ToUtf8(artist));
		metadata.put(ALBUM, StringUtil.iso88591ToUtf8(album));
		metadata.put(COVER_ART, coverArt);
		metadata.put(DURATION, duration);
//		System.out.println(String.format("title: %1$s, artist: %2$s, album: %3$s", title, artist, album));
		return metadata;
	}
	
}
