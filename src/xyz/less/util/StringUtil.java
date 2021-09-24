package xyz.less.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public final class StringUtil {

	public static boolean isBlank(String str) {
		return isEmpty(trim(str));
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() <= 0 || "null".equalsIgnoreCase(trim(str));
	}

	public static String trim(String str) {
		if (str == null) {
			return null;
		}
		return str.trim();
	}

	/**
	 * @param duration
	 * @return 返回格式：mm:ss, 如05:20
	 */
	public static String toMmss(double duration) {
		return toMmss(duration, null);
	}

	/**
	 * @param duration
	 * @return 返回格式：mm:ss, 如05:20
	 */
	public static String toMmss(double duration, String seperator) {
		int minutes = (int) duration;
		seperator = seperator == null ? ":" : seperator;
		return String.format("%1$02d" + seperator + "%2$02d", minutes, (int) ((duration - minutes) * 60));
	}

	/**
	 * @param duration
	 * @return 返回格式：mm:ss.SS, 如05:20.99
	 */
	public static String toMmssSS(double duration) {
		int minutes = (int) duration;
		double secords = (duration - minutes) * 60;
		return String.format("%1$02d:%2$05.2f", minutes, secords);
	}

	/**
	 * @param duration
	 * @return 返回格式：mm:ss.SSS, 如05:20.999
	 */
	public static String toMmssSSS(double duration) {
		int minutes = (int) duration;
		int secords = (int) ((duration - minutes) * 60);
		int millis = (int) (((duration - minutes) * 60 - secords) * 1000);
		return String.format("%1$02d:%2$02d.%3$03d", minutes, secords, millis);
	}

	public static String toSlash(String path) {
		return toSlash(path, 0);
	}
	
	public static String toSlash(String path, int from) {
		path = trim(path);
		if (isBlank(path)) {
			return path;
		}
		from = from > 1 ? from : 1;
		return path.substring(0, from) + path.substring(from).replaceAll("\\\\", "/");
	}

	public static String getEncoding(String str) {
		String[] encodings = {"ISO-8859-1", "GBK",
				"UTF-8" };
		for(String encoding : encodings) {
			try {
				if (str.equals(new String(str.getBytes(), encoding))) {
					return encoding;
				}
			} catch (Exception ex) {
				//TODO
			}
		}
		return "UNKOWN";
	}
	
	public static String getDefault(String value, String defaultValue) {
		return isBlank(value) ? defaultValue : value;
	}
	
	public static String replaceAll(String str, String regex, String replacement) {
		if(isBlank(str)) {
			return str;
		}
		str = trim(str);
		str = str.replaceAll(regex, replacement);
		return str;
	}
	
	public static String decodeNameFromUrl(String url) {
		try {
			url = URLDecoder.decode(url, "UTF-8");
			int fromIndex = url.lastIndexOf("/") + 1;
			int toIndex = url.lastIndexOf(".");
			return url.substring(fromIndex, toIndex);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

