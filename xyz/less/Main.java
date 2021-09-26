package xyz.less;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.less.bean.ConfigConstant;
import xyz.less.graphic.MainView;
import xyz.less.graphic.PlayerView;

public class Main extends Application {
	Stage mainStage;
	
	private void setMainStage(Stage stage) {
		this.mainStage = stage;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		setMainStage(primaryStage);
		initStage();
	}

	private void initStage() {
		PlayerView mainView = new MainView(mainStage, 
				ConfigConstant.APP_WIDTH, ConfigConstant.APP_HEIGHT);
		mainStage.setScene(new Scene(mainView, 
				mainView.getWidth(), mainView.getHeight()));
		mainStage.initStyle(StageStyle.TRANSPARENT);
		mainStage.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> {
			//ø’∏Òº¸: ≤•∑≈/‘›Õ£“Ù¿÷
			if(KeyCode.SPACE == event.getCode()) {
				mainView.togglePlay();
			}
		});
		
		mainStage.show();
		mainView.initGraph();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
