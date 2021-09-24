package xyz.less.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import xyz.less.util.FileScanTask;

public class AsyncServices {
	private static final ExecutorService executorService = Executors.newFixedThreadPool(4);
	private static final ForkJoinPool fjPool = new ForkJoinPool();
	
	public static Future<?> submit(Runnable task) {
		return executorService.submit(task);
	}

	public static Future<?> submit(FileScanTask task) {
		return fjPool.submit(task);
	}
}
