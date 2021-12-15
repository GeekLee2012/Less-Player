package xyz.less.media;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public final class Metadatas {
	public static final String TITLE = "title";
	public static final String ARTIST = "artist";
	public static final String ALBUM = "album";
	public static final String COVER_ART = "image";
	public static final String DURATION = "duration";

	public static Map<String, Object> readFrom(File file) {
		//TODO Pluginable
		Jaudiotagger tagger = new Jaudiotagger();
		return tagger.readMetadata(file);
	}
	
	public static Map<String, Object> createDefaultMetadata() {
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
