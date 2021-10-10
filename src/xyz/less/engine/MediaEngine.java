package xyz.less.engine;

import xyz.less.media.FxMediaPlayer;

public final class MediaEngine implements Loader<FxMediaPlayer> {

	@Override
	public FxMediaPlayer load(String url) throws Exception {
		//TODO
		return new FxMediaPlayer();
	}
	
	
}
