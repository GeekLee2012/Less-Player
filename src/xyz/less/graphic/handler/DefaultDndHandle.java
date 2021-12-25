package xyz.less.graphic.handler;

import java.io.File;
import java.util.function.Consumer;

import javafx.scene.image.Image;
import xyz.less.bean.ConfigConstant;
import xyz.less.graphic.action.DndAction.DndContext;
import xyz.less.graphic.action.DndAction.DndType;
import xyz.less.util.FileUtil;

public class DefaultDndHandle implements DndHandle {
	protected DndHandlerPipeline pipeline;
	protected DndContext context;
	
	public DefaultDndHandle() {
		this.pipeline = new DefaultDndHandlerPipeline();
	}
	
	public DefaultDndHandle addHandler(Consumer<DndContext> handle) {
		pipeline.addHandler(handle);
		return this;
	}

	@Override
	public void handle(DndContext context) {
		setContext(context);
		String url = context.getUrl();
		if(url.startsWith(ConfigConstant.FILE_PREFIX)) {
			resolveFileDetailDndType();
		} else if(url.startsWith(ConfigConstant.HTTPS_PREFIX) 
				|| url.startsWith(ConfigConstant.HTTP_PREFIX)) {
			context.setDndType(DndType.LINK);
		}
		pipeline.handle(context);
	}

	private void resolveFileDetailDndType() {
		File file = context.getFile();
		context.setDndType(DndType.FILE);
		context.setUserData(file);
		if(FileUtil.isImage(file)) { //图片
			context.setDndType(DndType.IMAGE);
			context.setUserData(new Image(context.getUrl()));
		} else if(FileUtil.isLryic(file)) { //歌词
			context.setDndType(DndType.LYRIC);
		} else if(FileUtil.isDirectory(file)) { //目录
			context.setDndType(DndType.DIR);
		} else if(FileUtil.isAudio(file)) { //音频
			context.setDndType(DndType.AUDIO);
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
