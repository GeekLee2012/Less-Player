package xyz.less.media;

import java.io.File;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import xyz.less.util.StringUtil;

public class Jaudiotagger {

	public Map<String, Object> readMetadata(File file) {
		Map<String, Object> metadata = Metadatas.createDefaultMetadata();
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
			metadata.put(Metadatas.TITLE, StringUtil.iso88591ToUtf8(title));
			metadata.put(Metadatas.ARTIST, StringUtil.iso88591ToUtf8(artist));
			metadata.put(Metadatas.ALBUM, StringUtil.iso88591ToUtf8(album));
			
			Artwork artwork = tag.getFirstArtwork();
			if(artwork != null) {
				metadata.put(Metadatas.COVER_ART, artwork.getBinaryData());
			}
		}
		if(audioHeader != null) {
			double duration = audioHeader.getTrackLength() / 60D;
			metadata.put(Metadatas.DURATION, duration);
		}
//		System.out.println(String.format("title: %1$s, artist: %2$s, album: %3$s", title, artist, album));
		return metadata;
	}
	
}
