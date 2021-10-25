package xyz.less.media;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import javafx.scene.image.Image;
import xyz.less.util.StringUtil;

public final class Metadatas {
	public static final String TITLE = "title";
	public static final String ARTIST = "artist";
	public static final String ALBUM = "album";
	public static final String COVER_ART = "image";
	public static final String DURATION = "duration";

	public static Map<String, Object> readFrom(File file) {
		Map<String, Object> metadata = createDefaultMetadata();
		
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
			String title = tag.getFirst(FieldKey.TITLE);
			String artist = tag.getFirst(FieldKey.ARTIST);
			String album = tag.getFirst(FieldKey.ALBUM);
			metadata.put(TITLE, StringUtil.iso88591ToUtf8(title));
			metadata.put(ARTIST, StringUtil.iso88591ToUtf8(artist));
			metadata.put(ALBUM, StringUtil.iso88591ToUtf8(album));
			
			Artwork artwork = tag.getFirstArtwork();
			if(artwork != null) {
				metadata.put(COVER_ART, artwork.getBinaryData());
			}
		}
		if(audioHeader != null) {
			double duration = audioHeader.getTrackLength() / 60D;
			metadata.put(DURATION, duration);
		}
//		System.out.println(String.format("title: %1$s, artist: %2$s, album: %3$s", title, artist, album));
		return metadata;
	}
	
	private static Map<String, Object> createDefaultMetadata() {
		Map<String, Object> metadata = new HashMap<>();
		metadata.put(TITLE, null);
		metadata.put(ARTIST, null);
		metadata.put(ALBUM, null);
		metadata.put(COVER_ART, null);
		metadata.put(DURATION, 0D);
		return metadata;
	}
	
	public static String getTitle(Map<String, Object> metadata) {
		return (String)getValue(metadata, TITLE);
	}
	
	public static String getArtist(Map<String, Object> metadata) {
		return (String)getValue(metadata, ARTIST);
	}
	
	public static String getAlbum(Map<String, Object> metadata) {
		return (String)getValue(metadata, ALBUM);
	}
	
	public static double getDuration(Map<String, Object> metadata) {
		return (double)getValue(metadata, DURATION);
	}
	
	public static byte[] getCoverArt(Map<String, Object> metadata) {
		return (byte[])getValue(metadata, COVER_ART);
	}
	
	public static Image getCoverArtImage(Map<String, Object> metadata) {
		byte[] coverArt = getCoverArt(metadata);
		return coverArt == null ? null : 
			new Image(new ByteArrayInputStream(coverArt));
	}
	
	public static Object getValue(Map<String, Object> metadata, String key) {
		return metadata.get(key);
	}
	
}
