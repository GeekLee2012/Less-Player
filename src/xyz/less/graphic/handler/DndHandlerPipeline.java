package xyz.less.graphic.handler;

import java.util.function.Consumer;

import xyz.less.graphic.action.DndAction.DndContext;

public interface DndHandlerPipeline {
	void addHandler(Consumer<DndContext> handle);
	void handle(DndContext context);
}
