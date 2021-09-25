package xyz.less.bean;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import xyz.less.util.StringUtil;

import java.util.TreeMap;

public class Lyric {
	private String title;
	private String artist;
	private String album;
	private String by;
	private int offset;
	
	private Map<String, String> datas = new TreeMap<>((k1, k2) -> {
			return k1.compareToIgnoreCase(k2);
		});
	
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
	public String getBy() {
		return by;
	}
	public void setBy(String by) {
		this.by = by;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public Map<String, String> getDatas() {
		return datas;
	}
	public void setDatas(Map<String, String> datas) {
		this.datas.putAll(datas);
	}
	public int getDatasSize() {
		return this.datas.size();
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("--------------- Lyric ---------------").append("\n");
		buffer.append("title:").append(getTitle()).append("\n")
			.append("artist:").append(getArtist()).append("\n")
			.append("album:").append(getAlbum()).append("\n")
			.append("by:").append(getBy()).append("\n")
			.append("offset:").append(getOffset())
			.append("\n\n");
		Iterator<Entry<String, String>> iter = datas.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			buffer.append(entry.getKey())
				.append(" ")
				.append(entry.getValue())
				.append("\n");
		}
		buffer.append("------------------------------").append("\n");
		return buffer.toString();
	}
	
	public String toLrc() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[ti:").append(StringUtil.getDefault(getTitle(), "")).append("]\n")
			.append("[ar:").append(StringUtil.getDefault(getArtist(), "")).append("]\n")
			.append("[al:").append(StringUtil.getDefault(getAlbum(), "")).append("]\n")
			.append("[by:").append(StringUtil.getDefault(getBy(), "")).append("]\n")
			.append("[offset:").append(getOffset()).append("]\n")
			.append("\n");
		Iterator<Entry<String, String>> iter = datas.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			buffer.append("[").append(entry.getKey()).append("]")
				.append(entry.getValue()).append("\n");
		}
		return buffer.toString();
	}
	public String getLine(String timeKey) {
		return datas.get(timeKey);
	}
}
