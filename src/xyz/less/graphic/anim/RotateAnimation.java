package xyz.less.graphic.anim;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * 为什么没有使用RotateTransition？
 * 相比之下(本机测试)，直接使用RotateTransition时CPU占用率较高
 */
public class RotateAnimation extends AnimationTimer {
	private final static double MAX_DEGREE = 360;
	private final static int DEFAULT_FPS = 60;
	private int fps = DEFAULT_FPS;
	
	private Node node;
	private Duration duration;
	private double rotate = 0;
	private double lastMills;
	private double step;
	private double npf = 0;
	private double refreshMills = 50;
	
	public RotateAnimation(Node node, Duration duration) {
		setFPS(DEFAULT_FPS);
		setNode(node);
		setDuration(duration);
	}
	
	public RotateAnimation() {
		setFPS(DEFAULT_FPS);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
		this.step = (MAX_DEGREE * refreshMills)  / duration.toMillis();
	}
	
	public void setFPS(int fps) {
		this.fps = fps;
		this.npf = 1E9 / fps;
		this.refreshMills = toMills(npf);
	}
	
	public int getFPS() {
		return fps;
	}
	
	@Override
	public void handle(long now) {
		double nowMills = toMills(now);
		lastMills = lastMills > 0 ? lastMills : nowMills;
		double distance = nowMills - lastMills;
		if(distance >= refreshMills) {
			rotate = (rotate + step) % MAX_DEGREE;
			node.setRotate(rotate);
			lastMills = nowMills;
		}
	}
	
	public void resetRotate() {
		rotate = 0;
		node.setRotate(0);
	}
	
	private double toMills(double nanosec) {
		return nanosec * 1E-6;
	}

}
