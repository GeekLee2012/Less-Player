package xyz.less.rpc;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;

public final class MethodInvoker {
	private String methodName;
	private Object[] args;
	private Class<?>[] argTypes;
	private Throwable exception;
	@SuppressWarnings("unused")
	private Class<?> returnType; //暂时用不上
	
	public MethodInvoker(String methodName, Object[] args, Class<?>[] argTypes, Class<?> returnType) {
		super();
		this.methodName = methodName;
		this.args = args;
		this.argTypes = argTypes;
		this.returnType = returnType;
	}
	
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	
	public Throwable getException() {
		return exception;
	}
	
	public boolean success() {
		return exception == null;
	}

	public Object invoke(Object target) throws Exception {
		CompletableFuture<Object> future = new CompletableFuture<>();
		Platform.runLater(() -> {
			Object returnObj = null;
			try {
				Method method = target.getClass().getMethod(methodName, argTypes);
				method.setAccessible(true);
				returnObj = method.invoke(target, args);
				System.out.println(">>>Invoke Method " + methodName +"(), Return: " + returnObj);
			} catch (Exception e) {
				setException(e);
				e.printStackTrace();
			}
			future.complete(returnObj);
		});
		return future.get();
	}

	@SuppressWarnings("unused")
	@Deprecated
	private Class<?>[] getArgsTypes() {
		if(args == null || args.length < 1) {
			return null;
		}
		int i = 0;
		Class<?>[] argTypes = new Class<?>[args.length];
		for(Object arg : args) {
			argTypes[i++] = arg.getClass();
		}
		return argTypes;
	}
	
}
