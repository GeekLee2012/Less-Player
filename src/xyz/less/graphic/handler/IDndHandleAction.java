package xyz.less.graphic.handler;

import xyz.less.graphic.control.DndAction.DndContext;

@FunctionalInterface
public interface IDndHandleAction {
	void handle(DndContext context);
}
