package xyz.less.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sound.sampled.AudioSystem;

import xyz.less.bean.Audio;
import xyz.less.media.DefaultDelegatePlayer;
import xyz.less.media.IDelegatePlayer;
import xyz.less.media.jsa.JsaDelegatePlayer;
import xyz.less.media.jsa.OggDelegatePlayer;
import xyz.less.util.FileUtil;

public final class MediaService {
	private final static List<IDelegatePlayer> PLAYERS = new ArrayList<>();
	public final static String[] SUFFIXES_1 = {".mp3", ".wav", ".aac", ".m4a"};
	public final static String[] SUFFIXES_2 = {".flac", ".aac", ".wav"};
	public final static String[] SUFFIXES_3 = {".ogg"};
	public final static String[] ALL_SUFFIXES;
	
	static {
		ALL_SUFFIXES = getAllSuffixes();
		
		PLAYERS.add(new DefaultDelegatePlayer());
		PLAYERS.add(new JsaDelegatePlayer());
		PLAYERS.add(new OggDelegatePlayer());
		
		Arrays.asList(ALL_SUFFIXES).forEach(System.out::println);
	}
	
	public static IDelegatePlayer select(Audio audio) {
		for(IDelegatePlayer player : PLAYERS) {
			if(player.isPlayable(audio)) {
				return player;
			}
		}
		return PLAYERS.get(0);
	}
	
	public static Set<String> getAllSuffixSet() {
		Set<String> result = new HashSet<>();
		Collections.addAll(result, SUFFIXES_1);
		Collections.addAll(result, SUFFIXES_2);
		Collections.addAll(result, SUFFIXES_3);
		result.addAll(getAudioSystemSupportedTypes());
		return result;
	}
	
	private static String[] getAllSuffixes() {
		Set<String> suffixes = getAllSuffixSet();
		return suffixes.toArray(new String[suffixes.size()]);
	}
	
	public static List<String> getAudioSystemSupportedTypes() {
		List<String> types = new ArrayList<>();
		Arrays.asList(AudioSystem.getAudioFileTypes()).forEach(t -> {
			types.add("." + t.getExtension());
		});
		return types;
	}
	
	public static boolean isSupportedAudioFile(File file) {
		return FileUtil.isFileSupported(file, ALL_SUFFIXES);
	}
	
	public static File getAudioFile(Audio audio) {
		return Paths.get(URI.create(audio.getSource())).toFile();
	}
	
	public static InputStream getInputStream(Audio audio) {
		try {
			return new FileInputStream(getAudioFile(audio));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
