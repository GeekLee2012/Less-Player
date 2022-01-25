package xyz.less.rpc;

import java.io.Serializable;
import java.util.Arrays;

public final class RpcRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private Class<?> apiClass;
	private String methodName;
	private Object[] args;
	private Class<?>[] argTypes;
	private Class<?> returnType;
	
	public RpcRequest(Class<?> apiClass, String methodName, Object[] args, Class<?>[] argTypes, Class<?> returnType) {
		super();
		this.apiClass = apiClass;
		this.methodName = methodName;
		this.args = args;
		this.argTypes = argTypes;
		this.returnType = returnType;
	}
	
	public Class<?> getApiClass() {
		return apiClass;
	}

	public void setApiClass(Class<?> apiClass) {
		this.apiClass = apiClass;
	}

	public String getClassName() {
		return apiClass.getName();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	public Class<?>[] getArgTypes() {
		return argTypes;
	}

	public void setArgTypes(Class<?>[] argTypes) {
		this.argTypes = argTypes;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	@Override
	public String toString() {
		return "RpcRequest [apiClass=" + apiClass + ", methodName=" + methodName + ", args=" + Arrays.toString(args)
				+ ", argTypes=" + Arrays.toString(argTypes) + ", returnType=" + returnType + "]";
	}
	
}
