package xyz.less.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.less.bean.ConfigConstant;

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

	public static String getDefault(String value, String defaultValue) {
		return isBlank(value) || isMessyCode(value) ? defaultValue : value;
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
			url = URLDecoder.decode(url, ConfigConstant.UTF_8);
			int fromIndex = url.lastIndexOf("/") + 1;
			int toIndex = url.lastIndexOf(".");
			return url.substring(fromIndex, toIndex);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
    
    public static boolean isMessyCode(String value) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(value);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
//                chLength++; 
            }
        }
        System.err.println(count + " : " + chLength);
        return (count / chLength) > 0.4;
    }
}

