package xyz.less.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;

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
	private String coverArtUrl;
	private transient byte[] coverArt;
	private String source;
	private transient BooleanProperty playing;
	private transient HashMap<String, Object> extraMap;
	
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
		//TODO 优先级: 内容 > URL
		if(coverArt != null) {
			return new Image(new ByteArrayInputStream(coverArt));
		}
		try {
			return coverArtUrl != null ? new Image(coverArtUrl, true) : null;
		} catch (Exception e) {
//				e.printStackTrace();
		}
		return null;
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

	public String getCoverArtUrl() {
		return coverArtUrl;
	}

	public Audio setCoverArtUrl(String coverArtUrl) {
		this.coverArtUrl = coverArtUrl;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		return compareTo((Audio)o) == 0;
	} 
	
	@Override
	public int compareTo(Audio o) {
		if(o == null || StringUtil.isEmpty(o.getSource())) {
			return 1;
		}
		if(StringUtil.isBlank(this.source)) {
			return 1;
		}
		//TODO 目前排序规则: 目录优先, 名称次之（不区分大小写）
		return this.source.compareToIgnoreCase(o.getSource());
	}

	public boolean existsExtra(String key) {
		return getExtraMap().containsKey(key);
	}

	public Audio putExtra(String key, Object value) {
		getExtraMap().put(key, value);
		return this;
	}

	private HashMap<String, Object> getExtraMap() {
		if (extraMap == null) {
			extraMap = new HashMap<>();
		}
		return  extraMap;
	}

	public String getStringExtra(String key) {
		return (String) getExtraMap().get(key);
	}

	public int getIntExtra(String key) {
		return (int) getExtraMap().get(key);
	}

	public long getLongExtra(String key) {
		return (long) getExtraMap().get(key);
	}

	@Override
	public String toString() {
		return "Audio{" +
				"title='" + title + '\'' +
				", artist='" + artist + '\'' +
				", album='" + album + '\'' +
				", duration=" + duration +
				", coverArtUrl='" + coverArtUrl + '\'' +
				", source='" + source + '\'' +
				", extraMap=" + extraMap +
				'}';
	}
}
