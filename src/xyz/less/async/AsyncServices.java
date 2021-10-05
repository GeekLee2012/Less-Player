package xyz.less.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import javafx.application.Platform;

public class AsyncServices {
	private static final ExecutorService executorService = Executors.newFixedThreadPool(4);
	private static final ForkJoinPool fjPool = new ForkJoinPool();
	
	public static Future<?> submit(Runnable task) {
		return executorService.submit(task);
	}
	
	public static <V> Future<V> submit(Callable<V> task) {
		return executorService.submit(task);
	}
	
	public static Future<?> submitOnFutureDone(Future<?> future, Runnable task) {
		if(future == null) {
			return submit(task);
		}
		return submit(() -> {
			try {
				future.get();
			} catch(Exception e) {
				e.printStackTrace();
			}
			try {
				task.run();
			}catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	public static Future<?> submitFxTaskOnFutureDone(Future<?> future, Runnable task) {
		if(future == null) {
			return submit(task);
		}
		return submitOnFutureDone(future, () -> {
			Platform.runLater(()-> {
				task.run();
			});
		});
	}

	public static Future<?> submit(FileScanTask task) {
		return fjPool.submit(task);
	}
}
