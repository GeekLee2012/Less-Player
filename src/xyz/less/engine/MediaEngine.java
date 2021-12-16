package xyz.less.engine;

import xyz.less.media.FxMediaPlayer;

//TODO 要做什么，还没想好
//名字取得Niu而已，其实功能呵呵。。。
public final class MediaEngine implements Loader<FxMediaPlayer> {

	@Override
	public FxMediaPlayer load(String url) throws Exception {
		//TODO
		return new FxMediaPlayer();
	}
	
	
}
