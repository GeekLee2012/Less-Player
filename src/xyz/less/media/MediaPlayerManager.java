package xyz.less.media;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xyz.less.bean.Audio;

//TODO
public final class MediaPlayerManager {
	private final List<IMediaPlayer> cachedPlayers = new ArrayList<>();
	
	public MediaPlayerManager() {
		add(new DefaultFxPlayer());
		add(new DefaultJsaPlayer());
		add(new FlacPlayer());
		add(new OggPlayer());
//		add(new Mp3Player());
	}
	
	public MediaPlayerManager add(IMediaPlayer player) {
		if(!cachedPlayers.contains(player)) {
			cachedPlayers.add(player);
		}
		return this;
	}
	
	//TODO bug 插入顺序决定能否被选择
	public IMediaPlayer select(Audio audio) {
		for(IMediaPlayer player : cachedPlayers) {
			if(player.isPlayable(audio)) {
				return player;
			}
		}
		return cachedPlayers.get(0);
	}

	public Set<String> getAllSuffixes() {
		Set<String> suffixes = new HashSet<>();
		for(IMediaPlayer player : cachedPlayers) {
			suffixes.addAll(player.getSuffixSet());
		}
		return suffixes;
	}
	
	public void printAllSuffixes() {
		System.out.print("[Supported Audio Suffixes] ");
		getAllSuffixes().forEach(e -> {
			System.out.print(e + ", ");
		});
		System.out.println();
	}
}
