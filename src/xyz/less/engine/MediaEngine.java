package xyz.less.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xyz.less.bean.Audio;
import xyz.less.media.DefaultDelegatePlayer;
import xyz.less.media.IDelegatePlayer;
import xyz.less.media.jsa.JsaDelegatePlayer;
import xyz.less.util.FileUtil;

//TODO 要做什么，还没想好
//名字取得Niu而已，其实功能呵呵。。。
public final class MediaEngine {
	private final static List<IDelegatePlayer> PLAYERS = new ArrayList<>();
	public final static String[] SUFFIXES_1 = {".mp3", ".wav", ".aac", ".m4a"};
	public final static String[] SUFFIXES_2 = {".flac", ".aac", ".wav"};
	public final static String[] ALL_SUFFIXES;
	
	static {
		ALL_SUFFIXES = getAllSuffixes();
		
		PLAYERS.add(new DefaultDelegatePlayer());
		PLAYERS.add(new JsaDelegatePlayer());
		
		Arrays.asList(ALL_SUFFIXES).forEach(s -> {
			System.out.print(s + " ");
		});
		System.out.println();
	}
	
	//TODO
	public static IDelegatePlayer select(Audio audio) {
		for(IDelegatePlayer player : PLAYERS) {
			if(player.isPlayable(audio)) {
				return player;
			}
		}
		return PLAYERS.get(0);
	}
	
	private static String[] getAllSuffixes() {
		Set<String> result = new HashSet<>();
		Collections.addAll(result, SUFFIXES_1);
		Collections.addAll(result, SUFFIXES_2);
		return result.toArray(new String[result.size()]);
	}
	
	
	public static boolean isSupportedAudioFile(File file) {
		return FileUtil.isFileSupported(file, ALL_SUFFIXES);
	}
	
}
