package xyz.less.media;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import xyz.less.bean.Audio;
import xyz.less.util.StringUtil;

public final class AudioSuffixManager {
	private final Set<String> suffixes = new HashSet<>();
	
	public void setSuffixes(String... suffixes) {
		this.suffixes.clear();
		addSuffixes(suffixes);
	}
	
	public void addSuffixes(String... suffixes) {
		if(suffixes != null) {
			addSuffixes(Arrays.asList(suffixes));
		}
	}
	
	public void addSuffixes(Collection<String> suffixes) {
		if(suffixes != null) {
			for(String suffix : suffixes) {
				if(suffix != null) {
					suffix = suffix.trim().toLowerCase();
					if(suffix.indexOf(".") == -1) {
						suffix = "." + suffix;
					}
					this.suffixes.add(suffix);
				}
			}
		}
	}
	
	public boolean isPlayable(Audio audio) {
		if(audio == null) {
			return false;
		}
		String source = StringUtil.trim(audio.getSource());
		if(source != null) {
			for(String suffix : suffixes) {
				if(source.toLowerCase().endsWith(suffix)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Set<String> getSuffixSet() {
		return suffixes;
	}
	
}
