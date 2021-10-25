package xyz.less.async;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
	
	public static void cancel(Future<?>... futures) {
		if(futures != null) {
			Arrays.asList(futures).forEach(future -> {
				if(future != null) {
					future.cancel(true);
				}
			});
		}
	}
	
	public static Future<?> submitOnFutureDone(Future<?> future, Runnable task, Runnable onCancelledTask, Runnable onFailedTask) {
		if(future == null) {
			return submit(task);
		}
		return submit(() -> {
			try {
				future.get(60, TimeUnit.SECONDS);
				if(task != null) {
					task.run();
				}
			} catch(CancellationException ce) {
				//ce.printStackTrace();
				if(onCancelledTask != null) {
					onCancelledTask.run();
				}
			} catch(Exception e) {
				e.printStackTrace();
				if(onFailedTask != null) {
					onFailedTask.run();
				}
			}
		});
	}
	
	public static Future<?> submitFxTaskOnFutureDone(Future<?> future, Runnable task) {
		return submitFxTaskOnFutureDone(future, task, null, null);
	}
	
	public static Future<?> submitFxTaskOnFutureDone(Future<?> future, Runnable task, Runnable onCancelledTask, Runnable onFailedTask) {
		if(future == null) {
			return submit(task);
		}
		return submitOnFutureDone(future, () -> {
			Platform.runLater(()-> {
				if(task != null) {
					task.run();
				}
			});
		}, () -> {
			Platform.runLater(()-> {
				if(onCancelledTask != null) {
					onCancelledTask.run();
				}
			});
		}, () -> {
			Platform.runLater(()-> {
				if(onFailedTask != null) {
					onFailedTask.run();
				}
			});
		});
	}

	public static Future<?> submit(FileScanTask task) {
		return fjPool.submit(task);
	}
	
}
