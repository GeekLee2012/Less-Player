package xyz.less.graphic;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import xyz.less.graphic.control.AutoDrawerAction;
import xyz.less.graphic.control.DndAction;
import xyz.less.graphic.control.DnmAction;
import xyz.less.graphic.control.DndAction.DndContext;

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
	
	public static <T> T byId(String id, Scene scene) {
		return bySelector("#" + id, scene);
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
		addChildren(parent, Arrays.asList(nodes));
	}
	
	public static void addChildren(Pane parent, Collection<? extends Node> c) {
		if(parent !=null && c != null) {
			parent.getChildren().addAll(c);
		}
	}
	
	public static void addIcons(Stage stage, Image... icons) {
		if(icons != null) {
			applyStages(st -> st.getIcons().addAll(
					Arrays.asList(icons)), stage);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void bind(Property<?> target, ObservableValue refer) {
		if(target != null && refer != null) {
			target.bind(refer);
		}
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
			List<String> list = stage.getScene().getStylesheets();
			if(!list.contains(stylesheet)) {
				list.add(stylesheet);
			}
		}, stages);
	}
	
	public static void toggleStyleClass(boolean value,String styleClass, Node... nodes) {
		if(value) {
			Guis.addStyleClass(styleClass, nodes);
		} else {
			Guis.removeStyleClass(styleClass, nodes);
		}
	}
	
	public static void addStyleClass(String styleClass, Node... nodes) {
		applyNodes(node -> {
			if(!node.getStyleClass().contains(styleClass)) {
				node.getStyleClass().add(styleClass);
			}
		}, nodes);
	}

	public static void addStyleClass(Node node, String... styleClasses) {
		if(node != null && styleClasses != null) {
			node.getStyleClass().addAll(styleClasses);
		}
	}
	
	public static void removeStyleClass(String styleClass, Node... nodes) {
		applyNodes(node -> {
			while(node.getStyleClass().contains(styleClass)) {
				node.getStyleClass().remove(styleClass);
			}
		}, nodes);
	}

	public static void removeStyleClass(Node node, String... styleClasses) {
		if(node != null && styleClasses != null) {
			node.getStyleClass().removeAll(styleClasses);
		}
	}
	
	public static void addHoverStyleClass(String styleClass, Node... nodes) {
		addHoverAction(node -> addStyleClass(styleClass, node), 
				node -> removeStyleClass(styleClass, node), 
				nodes);
	}

	public static void addHoverStyleClass(Node node, String... styleClasses) {
		if(node != null && styleClasses != null) {
			addHoverAction(e -> addStyleClass(e, styleClasses),
					e -> removeStyleClass(e, styleClasses),
					node);
		}
	}
	
	public static void addHoverAction(Consumer<? super Node> enterAction, Consumer<? super Node> exitAction, Node... nodes) {
		applyNodes(node -> {
			node.setOnMouseEntered(e -> {
				enterAction.accept(node);
			});
			node.setOnMouseExited(e -> {
				exitAction.accept(node);
			});
		}, nodes);
	}

	public static void addHoverAction(BiConsumer<? super Node, ? super MouseEvent> enterAction, BiConsumer<? super Node, ? super MouseEvent> exitAction, Node... nodes) {
		applyNodes(node -> {
			node.setOnMouseEntered(e -> {
				enterAction.accept(node, e);
			});
			node.setOnMouseExited(e -> {
				exitAction.accept(node, e);
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
	
	public static void setGraphic(Image image, Label... nodes) {
		setGraphic(new ImageView(image), nodes);
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
	
	public static void setOnMouseClicked(EventHandler<? super MouseEvent> handler, Node... nodes) {
		applyNodes(e -> e.setOnMouseClicked(handler), nodes);
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
	 * @param ignoreTriggers
	 * @return
	 */
	public static DnmAction addDnmAction(Stage target, Node trigger, Node... ignoreTriggers) {
		return addDnmAction(target, trigger, null, ignoreTriggers);
	}
	
	public static DnmAction addDnmAction(Stage target, Node trigger, Consumer<DnmAction.Pos> action, Node... ignoreTriggers) {
		return new DnmAction(target, trigger, action, ignoreTriggers);
	}
	
	public static DndAction addDndAction(Node node, Consumer<DndContext> action) {
		return new DndAction(node, action);
	}
	
	public static AutoDrawerAction addAutoDrawerAction(Stage stage) {
		return new AutoDrawerAction(stage);
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
	
	public static int setImage(ImageView view, Image[] images, int index) {
		if(view != null && images != null 
				&& index < images.length) {
			view.setImage(images[index]);
		}
		return index;
	}
	
	/**
	 * C Style Boolean->Index
	 * @param view
	 * @param images
	 * @param value true->1, false->0
	 * @return
	 */
	public static int setImage(ImageView view, Image[] images, boolean value) {
		return setImage(view, images, value ? 1 : 0);
	}
	
	public static void setTransparent(Stage mainStage) {
		Guis.applyStages(s -> s.getScene().setFill(null), mainStage);
	}

	public static Parent loadFxml(URL url) {
		try {
			return FXMLLoader.load(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addShutdownHook(Runnable action) {
		Runtime.getRuntime().addShutdownHook(new Thread(action));
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}

	public static boolean isMacOS() {
		return getOSName().toLowerCase().startsWith("mac os");
	}

	public static void setVisible(boolean visible, Stage... stages) {
		applyStages(stage -> {
			if(visible) {
				stage.show();
			} else {
				stage.hide();
			}
		}, stages);
	}

	public static void setRectangleClip(Node node, double arcWidth, double arcHeight) {
		Rectangle rect = new Rectangle(node.prefWidth(-1), node.prefHeight(-1));
		rect.setArcWidth(arcWidth);
		rect.setArcHeight(arcHeight);
		node.setClip(rect);
	}

	public static void snapshotAndResetClip(ImageView imgView) {
		SnapshotParameters param = new SnapshotParameters();
		param.setFill(Color.TRANSPARENT);
		WritableImage image = imgView.snapshot(param, null);

		imgView.setClip(null);
		imgView.setImage(image);
	}

	//TODO Nothing Relative with GUI
	public static <T> void ifPresent(T t, Consumer<T> consumer) {
		if(t != null) {
			if(t instanceof Boolean && !(Boolean)t) {
				return ;
			}
			consumer.accept(t);
		}
	}
	
	//TODO Nothing Relative with GUI
	public static <T> void ifNotPresent(T t, Consumer<T> consumer) {
		if(t == null) {
			consumer.accept(t);
		} else if(t instanceof Boolean && !(Boolean)t) {
			consumer.accept(t);
		}
	}

}
