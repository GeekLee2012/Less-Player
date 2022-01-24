package xyz.less.graphic.handler;

import java.io.File;

import javafx.scene.image.Image;
import xyz.less.bean.AppContext;
import xyz.less.bean.Resources;
import xyz.less.graphic.control.DndAction.DndContext;
import xyz.less.graphic.control.DndAction.DndType;
import xyz.less.util.FileUtil;

public final class DefaultDndHandle implements IDndHandle {
	protected IDndHandleActionPipeline pipeline;
	protected DndContext context;
	
	public DefaultDndHandle() {
		pipeline = new DefaultDndHandleActionPipeline();
	}
	
	public DefaultDndHandle addHandleAction(IDndHandleAction action) {
		pipeline.addHandleAction(action);
		return this;
	}

	@Override
	public void handle(DndContext context) {
		setContext(context);
		String url = context.getUrl();
		if(url.startsWith(Resources.FILE_PREFIX)) {
			resolveFileDndType();
		} else if(url.startsWith(Resources.HTTPS_PREFIX) 
				|| url.startsWith(Resources.HTTP_PREFIX)) {
			context.setDndType(DndType.LINK);
		}
		pipeline.handle(context);
	}

	private void resolveFileDndType() {
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
		} else if(AppContext.get().isFileSupported(file)) { //音频
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
