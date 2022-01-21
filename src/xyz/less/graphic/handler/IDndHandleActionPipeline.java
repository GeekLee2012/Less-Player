package xyz.less.graphic.handler;

import java.util.function.Consumer;

import xyz.less.graphic.action.DndAction.DndContext;

public interface IDndHandleActionPipeline {
	void addHandleAction(Consumer<DndContext> handle);
	void handle(DndContext context);
}
