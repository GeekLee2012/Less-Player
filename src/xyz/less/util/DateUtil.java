package xyz.less.util;

import java.util.StringTokenizer;

public class DateUtil {
	
	public static double toMinutes(String mmssSSS) {
		StringTokenizer tokenizer = new StringTokenizer(mmssSSS);
		String mm = StringUtil.trim(tokenizer.nextToken(":"));
		String ss = StringUtil.trim(tokenizer.nextToken("."))
								.substring(1);
		String SSS = StringUtil.trim(tokenizer.nextToken());
		
		int minutes = Integer.parseInt(mm);
		int secords = Integer.parseInt(ss);
		int millis = Integer.parseInt(SSS);
		return minutes + (secords + millis / 1000.0D) / 60.0D;
	}
	
	public static void main(String[] args) {
		System.out.println(toMinutes("05:20.99"));
	}
}