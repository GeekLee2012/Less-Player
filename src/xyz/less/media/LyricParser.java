package xyz.less.media;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import xyz.less.bean.Lyric;
import xyz.less.util.StringUtil;

/**
 * lrc格式解析器
 */
public class LyricParser {
	private static final Logger LOGGER = Logger.getLogger(LyricParser.class.getName());
	public static final String TAG_BEGIN = "[";
	public static final String TAG_END = "]";
	
	public static final String TITLE_TAG_NAME = "ti";
	public static final String ARTIST_TAG_NAME = "ar";
	public static final String ALBUM_TAG_NAME = "al";
	public static final String BY_TAG_NAME = "by";
	public static final String OFFSET_TAG_NAME = "offset";
	public static final String META_TAG_DELIM = ":";
	
	public Lyric parse(String uri) throws Exception{
		Path path = Paths.get(URI.create(uri));
		if(!Files.exists(path)) {
			LOGGER.info("暂无文件: " 
					+ StringUtil.toSlash(path.toFile()
								.getAbsolutePath()));
			return null;
		}
		String filename = StringUtil.toSlash(path.toFile()
				.getAbsolutePath());
		List<String> lines = null;
		try {
			lines = Files.readAllLines(path);
		} catch(IOException e) {
			LOGGER.info("UTF8编码读取失败: " + filename);
			//e.printStackTrace();
			lines = Files.readAllLines(path, Charset.forName("GBK"));
		}
		if(lines == null) {
			LOGGER.info("尝试使用GBK编码读取失败: " + filename);
			return null;
		}
		return parseTextLines(lines);
	}
	
	public Lyric parseText(String text) {
		return parseTextLines(text.split("\r"));
	}
	
	public Lyric parseTextLines(String[] lines) {
		return parseTextLines(Arrays.asList(lines));
	}
	
	public Lyric parseTextLines(List<String> lines) {
		Lyric lyric = new Lyric();
		for(String line : lines) {
			line = StringUtil.trim(line);
			if(StringUtil.isBlank(line)) {
				continue;
			}
			if(!line.startsWith(TAG_BEGIN) 
				|| !line.contains(TAG_END)) {
				continue ;
			}
			if(isTimeDataTag(line)) {
				parseTimeData(lyric, line);
			} else {
				parseMeta(lyric, line);
			}
		}
		return lyric;
	}

	private void parseMeta(Lyric lyric, String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, getTagDelim(META_TAG_DELIM));
		if(tokenizer.countTokens() < 2) {
			return ;
		}
		String name = StringUtil.trim(tokenizer.nextToken());
		String value = StringUtil.trim(tokenizer.nextToken());
		if(StringUtil.isBlank(value)) {
			return ;
		}
		if(TITLE_TAG_NAME.equalsIgnoreCase(name)) {
			lyric.setTitle(value);
		} else if(ARTIST_TAG_NAME.equalsIgnoreCase(name)) {
			lyric.setArtist(value);
		} else if(ALBUM_TAG_NAME.equalsIgnoreCase(name)) {
			lyric.setAlbum(value);
		} else if(BY_TAG_NAME.equalsIgnoreCase(name)) {
			lyric.setBy(value);
		} else if(OFFSET_TAG_NAME.equalsIgnoreCase(name)) {
			lyric.setOffset(Integer.parseInt(value));
		}
	}

	private void parseTimeData(Lyric lyric, String line) {
		StringTokenizer tokenizer = new StringTokenizer(line, getTagDelim());
		int count = tokenizer.countTokens();
		if(count < 2) {
			return ;
		}
		List<String> list = new ArrayList<>();
		while(tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken());
		}
		String value = list.get(count - 1);
		for(int i = 0; i < list.size() - 1; i++) {
			lyric.getDatas().put(unifyTime(list.get(i)), value);
		}
		return ;
	}

	private boolean isTimeDataTag(String line) {
		//TODO regex
		return Character.isDigit(line.charAt(1));
	}
	
	private static String getTagDelim(String... delims) {
		return TAG_BEGIN + String.join("", delims) +TAG_END;
	}
	
	private static String unifyTime(String time) {
		return time.contains(".") ? time : time + ".000";
	}
	
}
