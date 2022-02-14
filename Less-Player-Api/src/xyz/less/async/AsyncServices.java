package xyz.less.async;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

public final class AsyncServices {
//	private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
	private static final ExecutorService executorService = Executors.newCachedThreadPool();
	private static final ForkJoinPool FJ_POOL = new ForkJoinPool();
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
				e1.printStackTrace();
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

	public static Future<?> submit(ForkJoinTask<?> task) {
		return FJ_POOL.submit(task);
	}
	
	public static void runLater(Runnable task) {
		if(task != null) {
			Platform.runLater(task);
		}
	}
	
	public static Runnable wrapTask2RunLater(Runnable task) {
		return task != null ? () -> runLater(task) : null;
	}
	
	private static void doInvoke(Runnable task) {
		if(task != null) {
			task.run();
		}
	}
}
