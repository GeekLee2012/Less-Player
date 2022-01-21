package xyz.less.api;

import javafx.stage.Stage;

public interface IAppContextApi extends IApi{
	Stage getMainStage();
	String getSkinName();
}
