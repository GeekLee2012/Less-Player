package xyz.less.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import xyz.less.util.StringUtil;

public class Audio implements Comparable<Audio>, Serializable {
	private static final long serialVersionUID = 1L;
	private String title;
	private String artist;
	private String album;
	private double duration;
	private transient byte[] coverArt;
	private String source;
	private transient BooleanProperty playing;
	
	public Audio() {
		
	}
	
	public Audio(String title, String artist, String album, 
			double duration, byte[] coverArt, String source) {
		super();
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.duration = duration;
		this.coverArt = coverArt;
		this.source = source;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public byte[] getCoverArt() {
		return coverArt;
	}
	public Image getCoverArtImage() {
		return coverArt == null ? null : 
			new Image(new ByteArrayInputStream(coverArt));
	}
	public void setCoverArt(byte[] coverArt) {
		this.coverArt = coverArt;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public boolean isPlaying() {
		return playingProperty().get();
	}
	public void setPlaying(boolean playing) {
		this.playingProperty().set(playing);;
	}
	public BooleanProperty playingProperty() {
		if (playing == null) {
			playing = new SimpleBooleanProperty(false);
		}
		return playing;
	}
	
	@Override
	public boolean equals(Object o) {
		return compareTo((Audio)o) == 0;
	} 
	
	@Override
	public int compareTo(Audio o) {
		if(o == null || StringUtil.isEmpty(o.getSource())) {
			return 1;
		}
		//排序规则: 目录优先, 名称次之（不区分大小写）
		return this.source.compareToIgnoreCase(o.getSource());
	}
}
