package xyz.less.graphic.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import xyz.less.graphic.action.DndAction.DndContext;

public class DefaultDndHandlerPipeline implements DndHandlerPipeline {
	private List<Consumer<DndContext>> handlers;
	
	public DefaultDndHandlerPipeline() {
		this.handlers = new ArrayList<>();
	}
	
	public void addHandler(Consumer<DndContext> handler) {
		handlers.add(handler);
	}
	
	@Override
	public void handle(DndContext context) {
		//TODO 暂时简单处理
		handlers.forEach(e -> { 
			e.accept(context);
		});
	}

}