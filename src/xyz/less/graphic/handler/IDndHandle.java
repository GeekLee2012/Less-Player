package xyz.less.graphic.handler;

import java.util.function.Consumer;

import xyz.less.graphic.action.DndAction.DndContext;

public interface IDndHandle {
	DndContext getContext();
	void handle(DndContext context);
	IDndHandle addHandleAction(Consumer<DndContext> handle);
}
