package xyz.less.graphic.handler;

import xyz.less.graphic.control.DndAction.DndContext;

public interface IDndHandle {
	DndContext getContext();
	void handle(DndContext context);
	IDndHandle addHandleAction(IDndHandleAction action);
}
