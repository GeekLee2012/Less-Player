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
	private static final int TIMEOUT = 60;
	
	public static Future<?> submit(Runnable task) {
		return executorService.submit(task);
	}
	
	public static <V> Future<V> submit(Callable<V> task) {
		return executorService.submit(task);
	}
	
	public static void cancel(Future<?>... futures) {
		try {
			if(futures != null) {
				Arrays.asList(futures).forEach(future -> {
					if(future != null) {
						future.cancel(true);
					}
				});
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Future<?> submitOnFutureDone(Future<?> future, Runnable task, Runnable onCancelledTask, Runnable onFailedTask) {
		if(future == null) {
			return submit(task);
		}
		return submit(() -> {
			try {
				future.get(TIMEOUT, TimeUnit.SECONDS);
				doInvoke(task);
			} catch(CancellationException e1) {
				doInvoke(onCancelledTask);
			} catch(Exception e2) {
				e2.printStackTrace();
				doInvoke(onFailedTask);
			}
		});
	}
	
	public static Future<?> submitFxTaskOnFutureDone(Future<?> future, Runnable task) {
		return submitFxTaskOnFutureDone(future, task, null, null);
	}
	
	public static Future<?> submitFxTaskOnFutureDone(Future<?> future, Runnable task, Runnable onCancelledTask, Runnable onFailedTask) {
		return future == null ? submit(wrapTask2RunLater(task)) 
			: submitOnFutureDone(future,  
					wrapTask2RunLater(task), 
					wrapTask2RunLater(onCancelledTask), 
					wrapTask2RunLater(onFailedTask));
	}

	public static Future<?> submit(FileScanTask task) {
		return fjPool.submit(task);
	}
	
	public static void runLater(Runnable task) {
		Platform.runLater(task);
	}
	
	public static Runnable wrapTask2RunLater(Runnable task) {
		return () -> runLater(task);
	}
	
	private static void doInvoke(Runnable task) {
		if(task != null) {
			task.run();
		}
	}
}
