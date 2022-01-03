package xyz.less.media.jsa;

import xyz.less.bean.Audio;
import xyz.less.bean.Resources;

//TODO
public final class PlayerHelpers {

	public static IPlayHelper select(Audio audio) {
		if(Resources.isFlac(audio)) {
			return new FlacPlayHelper(audio);
		} else if(Resources.isAac(audio)) {
			return new AacPlayHelper(audio);
		} else {
			return new DefaultPlayHelper(audio);
		}
	}
}
