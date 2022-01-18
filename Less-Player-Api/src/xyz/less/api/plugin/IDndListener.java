package xyz.less.api.plugin;

import java.io.File;

import javafx.scene.image.Image;

/** Waiting -> Done -> Failed/Success */
public interface IDndListener {
	/**图片*/
	void onDndSuccess(Image image);
	/**未被识别(或未被支持)的其他文件*/
	void onDndDone(File file);
	/**超链接*/
	void onDndLinkDone(String url);
}
