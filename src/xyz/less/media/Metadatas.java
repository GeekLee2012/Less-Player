package xyz.less.media;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import xyz.less.bean.Audio;
import xyz.less.util.StringUtil;

public final class Metadatas {
	public static final String TITLE = "title";
	public static final String ARTIST = "artist";
	public static final String ALBUM = "album";
	public static final String COVER_ART = "image";
	public static final String DURATION = "duration";
	public static final String EXTRA = "extra";

	public static Map<String, Object> readFrom(File file) {
		//TODO 可随意更换，Pluginable
		Jaudiotagger tagger = new Jaudiotagger();
		return tagger.readMetadata(file);
	}
	
	public static Map<String, Object> createDefault() {
		Map<String, Object> metadata = new HashMap<>();
		putTitle(metadata, null);
		putArtist(metadata, null);
		putAlbum(metadata, null);
		putCoverArt(metadata, (byte[])null);
		putDuration(metadata, 0);
		putExtra(metadata, null);
		return metadata;
	}
	
	public static Map<String, Object> copyFrom(Audio audio) {
		Map<String, Object> metadata = createDefault();
		putTitle(metadata, audio.getTitle());
		putArtist(metadata, audio.getArtist());
		putAlbum(metadata, audio.getAlbum());
		putCoverArt(metadata, audio.getCoverArt());
		putDuration(metadata, audio.getDuration());
		return metadata;
	}
	
	public static String getTitle(Map<String, Object> metadata) {
		return getString(metadata, TITLE);
	}
	
	public static String getArtist(Map<String, Object> metadata) {
		return getString(metadata, ARTIST);
	}
	
	public static String getAlbum(Map<String, Object> metadata) {
		return getString(metadata, ALBUM);
	}
	
	public static double getDuration(Map<String, Object> metadata) {
		return (double)getValue(metadata, DURATION);
	}
	
	public static byte[] getCoverArt(Map<String, Object> metadata) {
		return (byte[])getValue(metadata, COVER_ART);
	}
	
	public static Image getCoverArtImage(Map<String, Object> metadata) {
		Object coverArt = getValue(metadata, COVER_ART);
		if(coverArt == null) {
			return null;
		}
		if(coverArt instanceof Image) {
			return (Image)coverArt;
		}
		return new Image(new ByteArrayInputStream((byte[])coverArt));
	}
	
	public static String getExtra(Map<String, Object> metadata) {
		return getString(metadata, EXTRA);
	}
	
	public static void putTitle(Map<String, Object> metadata, String title) {
		putValue(metadata, TITLE, title);
	}
	
	public static void putArtist(Map<String, Object> metadata, String artist) {
		putValue(metadata, ARTIST, artist);
	}
	
	public static void putAlbum(Map<String, Object> metadata, String album) {
		putValue(metadata, ALBUM, album);
	}
	
	public static void putDuration(Map<String, Object> metadata, double duration) {
		putValue(metadata, DURATION, duration);
	}
	
	public static void putCoverArt(Map<String, Object> metadata, byte[] bytes) {
		putValue(metadata, COVER_ART, bytes);
	}
	
	public static void putCoverArt(Map<String, Object> metadata, Image image) {
		putValue(metadata, COVER_ART, image);
	}
	
	public static void putExtra(Map<String, Object> metadata, String extra) {
		putValue(metadata, EXTRA, extra);
	}
	
	private static String getString(Map<String, Object> metadata, String key) {
		Object value = getValue(metadata, key);
		if(value instanceof String) {
			return StringUtil.trim((String)value);
		}
		return (String)value;
	}
	
	public static Object getValue(Map<String, Object> metadata, String key) {
		return metadata.get(key);
	}
	
	public static void putValue(Map<String, Object> metadata, String key, Object value) {
		metadata.put(key, value);
	}

}
