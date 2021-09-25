package xyz.less.graphic;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import xyz.less.graphic.action.DndAction;
import xyz.less.graphic.action.DnmAction;
import xyz.less.graphic.action.DnmAction.DnmOffset;

public final class Guis {
	
	public static <T> T byId(String id, Stage stage) {
		return bySelector("#" + id, stage);
	}
	
	public static <T> T byClass(String styleClass, Stage stage) {
		return bySelector("." + styleClass, stage);
	}
	
	public static <T> T bySelector(String selector, Stage stage) {
		return bySelector(selector, stage.getScene());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T bySelector(String selector, Scene sence) {
		return (T)sence.lookup(selector);
	}
	
	public static void applyStages(Consumer<? super Stage> action, Stage... stages) {
		if(stages != null) {
			Arrays.asList(stages).forEach(stage -> {
				if(stage != null) {
					action.accept(stage);
				}
			});
		}
	}
	
	public static void applyNodes(Consumer<? super Node> action, Node... nodes) {
		if(nodes != null) {
			Arrays.asList(nodes).forEach(node -> {
				if(node != null) {
					action.accept(node);
				}
			});
		}
	}
	
	public static void applyChildren(Consumer<? super Node> action, Pane... panes) {
		if(panes != null) {
			Arrays.asList(panes).forEach(pane -> {
				pane.getChildren().forEach(action);
			});
		}
	}
	
	public static void applyChildrenDeeply(Consumer<? super Node> action, Pane... panes) {
		if(panes != null) {
			Arrays.asList(panes).forEach(pane -> {
				pane.getChildren().forEach(node -> {
					if(node instanceof Pane) {
						applyChildrenDeeply(action, (Pane)node);
					} else {
						action.accept(node);
					}
				});
			});
		}
	}
	
	public static void addChildren(Pane parent, Node... nodes) {
		if(parent !=null && nodes != null) {
			parent.getChildren().addAll(Arrays.asList(nodes));
		}
	}
	
	public static void addIcons(Stage stage, Image... icons) {
		if(icons != null) {
			stage.getIcons().addAll(Arrays.asList(icons));
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void bind(Property<?> target, ObservableValue refer) {
		if(target != null && refer != null) {
			target.bind(refer);
		}
	}
	
	public static void applyChildrenPrefHeight(Pane... panes) {
		applyChildren(node -> {
			if(node instanceof Pane) {
				Pane childPane = (Pane)node;
				Pane parent = (Pane)childPane.getParent();
				bind(childPane.prefHeightProperty(), parent.prefHeightProperty());
			}
		}, panes);
	}
	
	public static void setFitSize(double value, ImageView... views) {
		applyNodes(node -> {
				ImageView view = (ImageView)node;
				view.setFitWidth(value);
				view.setFitHeight(value);
		}, views);
	}
	
	public static void setUserData(Object value, Node... nodes) {
		applyNodes(node -> node.setUserData(value), nodes);
	}
	
	public static void addStylesheet(String stylesheet, Stage... stages) {
		applyStages(stage -> {
			stage.getScene().getStylesheets().add(stylesheet);
		}, stages);
	}
	
	public static void addStyleClass(String styleClass, Node... nodes) {
		applyNodes(node -> {
			if(!node.getStyleClass().contains(styleClass)) {
				node.getStyleClass().add(styleClass);
			}
		}, nodes);
	}
	
	public static void removeStyleClass(String styleClass, Node... nodes) {
		applyNodes(node -> {
			while(node.getStyleClass().contains(styleClass)) {
				node.getStyleClass().remove(styleClass);
			}
		}, nodes);
	}
	
	public static void addHoverStyleClass(String styleClass, Node... nodes) {
		applyNodes(node -> {
			node.setOnMouseEntered(e -> {
				addStyleClass(styleClass, node);
			});
			node.setOnMouseExited(e -> {
				removeStyleClass(styleClass, node);
			});
		}, nodes);
	}
	
	public static void removeHover(Node... nodes) {
		applyNodes(node -> {
			node.setOnMouseEntered(e -> {
				//TODO
			});
			node.setOnMouseExited(e -> {
				//TODO
			});
		}, nodes);
	}
	
	public static void setGraphic(Node value, Label... nodes) {
		applyNodes(node -> ((Label)node).setGraphic(value), nodes);
	}
	
	public static void setStyle(String value, Node... nodes) {
		applyNodes(node -> node.setStyle(value), nodes);
	}
	
	public static void setVisible(boolean value, Node... nodes) {
		applyNodes(node -> node.setVisible(value), nodes);
	}
	
	public static void setAlignment(Pos value, Node... nodes) {
		applyNodes(node -> {
			try {
				Method method = node.getClass().getMethod("setAlignment", 
						new Class[] {Pos.class});
				method.setAccessible(true);
				method.invoke(node, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, nodes);
	}
	
	public static void setPickOnBounds(boolean value, Node... nodes) {
		applyNodes(node -> node.setPickOnBounds(value), nodes);
	}
	
	public static void minimize(boolean value, Stage... stages) {
		applyStages(stage -> stage.setIconified(value), stages);
	}
	
	public static void maximized(boolean value, Stage... stages) {
		applyStages(stage -> stage.setMaximized(value), stages);
	}
	
	public static void setAlwaysOnTop(boolean value, Stage... stages) {
		applyStages(stage -> stage.setAlwaysOnTop(value), stages);
	}
	
	public static void exitApplication() {
		Platform.exit();
		System.exit(0);
	}
	
	public static BorderPane layout(BorderPane pane, Node top, 
			Node left, Node center, 
			Node right, Node bottom) {
		if(pane != null && center != null) {
			pane.setTop(top);
			pane.setLeft(left);
			pane.setCenter(center);
			pane.setRight(right);
			pane.setBottom(bottom);
		}
		return pane;
	}
	
	/** Drag And Move
	 * @param target
	 * @param trigger
	 * @param ignoreChildren
	 * @return
	 */
	public static DnmAction addDnmAction(Stage target, Node trigger, Node... ignoreTriggers) {
		return addDnmAction(target, trigger, null, ignoreTriggers);
	}
	
	public static DnmAction addDnmAction(Stage target, Node trigger, Consumer<DnmOffset> action, Node... ignoreTriggers) {
		return new DnmAction(target, trigger, action, ignoreTriggers)
					.enable(true);
	}
	
	public static DndAction addDndAction(Node node, Consumer<Dragboard> action) {
		return new DndAction(node, action).enable(true);
	}
	
	
	/** 点击view后，自动切换图片，依赖于userData
	 * @param view
	 * @param images
	 */
	public static int toggleImage(ImageView view, Image[] images) {
		if(view != null && images != null) {
			Object userData = view.getUserData();
			int i = (userData == null) ? 0 : (int)userData;
			i = ++i % images.length;
			view.setUserData(i);
			view.setImage(images[i]);
			return i;
		}
		return 0;
	}
	
	/** 点击view后，自动切换图片，依赖于userData
	 * @param view
	 * @param images
	 */
	public static int toggleImage(Label view, Image[] images) {
		if(view != null && images != null) {
			Object userData = view.getUserData();
			int i = (userData == null) ? 0 : (int)userData;
			i = ++i % images.length;
			view.setUserData(i);
			ImageView imageView = (ImageView)view.getGraphic();
			imageView.setImage(images[i]);
			return i;
		}
		return 0;
	}
	
	public static void moveStages(double offsetX, double offsetY, Stage... stages) {
		applyStages(stage -> {
			stage.setX(stage.getX() + offsetX);
			stage.setY(stage.getY() + offsetY);
		}, stages);
	}
	
	public static Parent loadFxml(URL url) {
		try {
			return FXMLLoader.load(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
