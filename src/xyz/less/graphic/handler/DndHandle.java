package xyz.less.graphic.handler;

import java.util.function.Consumer;

import xyz.less.graphic.action.DndAction.DndContext;

public interface DndHandle {
	DndContext getContext();
	void handle(DndContext context);
	DndHandle addHandler(Consumer<DndContext> handle);
}
