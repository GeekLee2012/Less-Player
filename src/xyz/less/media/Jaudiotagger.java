package xyz.less.media;

import java.io.File;
import java.util.Map;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;

import xyz.less.util.StringUtil;

public final class Jaudiotagger {

	public Map<String, Object> readMetadata(File file) {
		Map<String, Object> metadata = Metadatas.createDefault();
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
			Metadatas.putTitle(metadata, StringUtil.iso88591ToUtf8(title));
			Metadatas.putArtist(metadata, StringUtil.iso88591ToUtf8(artist));
			Metadatas.putAlbum(metadata, StringUtil.iso88591ToUtf8(album));
			
			byte[] imageData = null;
			try {
				if(tag instanceof FlacTag) {
					FlacTag flacTag = (FlacTag)tag;
					if(flacTag.getImages() != null 
							&& !flacTag.getImages().isEmpty()) {
						imageData = flacTag.getImages().get(0).getImageData();
					}
				} else if(tag instanceof VorbisCommentTag) {
					imageData = ((VorbisCommentTag)tag).getArtworkBinaryData();
				} else {
					Artwork artwork = tag.getFirstArtwork();
					imageData = artwork != null ? artwork.getBinaryData(): null;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(imageData != null) {
				Metadatas.putCoverArt(metadata, imageData);
			}
		}
		if(audioHeader != null) {
			double duration = audioHeader.getTrackLength() / 60D;
			Metadatas.putDuration(metadata, duration);
		}
//		System.out.println(String.format("title: %1$s, artist: %2$s, album: %3$s", title, artist, album));
		return metadata;
	}
	
}
