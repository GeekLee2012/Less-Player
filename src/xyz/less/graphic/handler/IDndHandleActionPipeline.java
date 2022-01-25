package xyz.less.graphic.handler;

import xyz.less.graphic.control.DndAction.DndContext;

public interface IDndHandleActionPipeline {
	void addHandleAction(IDndHandleAction action);
	void handle(DndContext context);
}
