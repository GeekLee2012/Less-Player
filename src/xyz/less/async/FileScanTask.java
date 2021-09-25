package xyz.less.async;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

import xyz.less.util.FileUtil;
import xyz.less.util.StringUtil;

public class FileScanTask extends RecursiveAction {
	private static final long serialVersionUID = 1L;
	
	String scanPathUri;
	List<File> result = new ArrayList<>();
	Consumer<File> consumer;
	String[] suffixes;
	
	public FileScanTask(String scanPathUri, Consumer<File> consumer, String... suffixes) {
		this.scanPathUri = scanPathUri;
		this.consumer = consumer;
		this.suffixes = suffixes;
	}
	
	@Override
	protected void compute() {
		List<FileScanTask> subTaskList = new ArrayList<>();
		
		if(StringUtil.isBlank(scanPathUri)) {
			return ;
		}
		Path path = Paths.get(scanPathUri);
		if(!Files.exists(path)) {
			return ;
		}
		File[] fileList = path.toFile().listFiles();
		for(File file : fileList) {
			if(file.isDirectory()) {
				FileScanTask task = new FileScanTask(file.getAbsolutePath(), consumer, this.suffixes);
				//task.fork();
				subTaskList.add(task);
			} else if(FileUtil.isSupported(file.getName(), suffixes)) {
				this.result.add(file);
				if(consumer != null) {
					consumer.accept(file);
				}
//				System.out.println(file.getAbsolutePath());
			}
		}
		
		for(FileScanTask  subTask : invokeAll(subTaskList)) {
			subTask.join();
		}
	}

}
