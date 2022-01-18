package xyz.less.graphic.handler;

import java.io.File;
import java.util.function.Consumer;

import javafx.scene.image.Image;
import xyz.less.bean.Resources;
import xyz.less.engine.MediaEngine;
import xyz.less.graphic.action.DndAction.DndContext;
import xyz.less.graphic.action.DndAction.DndType;
import xyz.less.util.FileUtil;

public class DefaultDndHandle implements IDndHandle {
	protected IDndHandleActionPipeline pipeline;
	protected DndContext context;
	
	public DefaultDndHandle() {
		pipeline = new DefaultDndHandleActionPipeline();
	}
	
	public DefaultDndHandle addHandleAction(Consumer<DndContext> handle) {
		pipeline.addHandleAction(handle);
		return this;
	}

	@Override
	public void handle(DndContext context) {
		setContext(context);
		String url = context.getUrl();
		if(url.startsWith(Resources.FILE_PREFIX)) {
			resolveFileDetailDndType();
		} else if(url.startsWith(Resources.HTTPS_PREFIX) 
				|| url.startsWith(Resources.HTTP_PREFIX)) {
			context.setDndType(DndType.LINK);
		}
		pipeline.handle(context);
	}

	private void resolveFileDetailDndType() {
		File file = context.getFile();
		context.setDndType(DndType.FILE);
		context.setUserData(file);
		if(Resources.isImage(file)) { //图片
			context.setDndType(DndType.IMAGE);
			context.setUserData(new Image(context.getUrl()));
		} else if(Resources.isLryic(file)) { //歌词
			context.setDndType(DndType.LYRIC);
		} else if(FileUtil.isDirectory(file)) { //目录
			context.setDndType(DndType.DIR);
		} else if(MediaEngine.isSupportedAudioFile(file)) { //音频
			context.setDndType(DndType.AUDIO);
		} else if(Resources.isJar(file)) { //jar包
			context.setDndType(DndType.JAR);
		}
	}

	@Override
	public DndContext getContext() {
		return context;
	}

	private void setContext(DndContext context) {
		this.context = context;
	}
	
}
