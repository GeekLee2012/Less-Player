package xyz.less.graphic.handler;

import java.util.ArrayList;
import java.util.List;

import xyz.less.graphic.control.DndAction.DndContext;

public final class DefaultDndHandleActionPipeline implements IDndHandleActionPipeline {
	private List<IDndHandleAction> actions;
	
	public DefaultDndHandleActionPipeline() {
		this.actions = new ArrayList<>();
	}
	
	public void addHandleAction(IDndHandleAction action) {
		actions.add(action);
	}
	
	@Override
	public void handle(DndContext context) {
		//TODO 暂时简单处理
		actions.forEach(action -> {
			action.handle(context);
		});
	}

}
