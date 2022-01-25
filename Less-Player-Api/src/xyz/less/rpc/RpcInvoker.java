package xyz.less.rpc;

import java.lang.reflect.Method;

public final class RpcInvoker<T> {
	private Class<T> apiClass;
	private RpcClient client;
	
	public RpcInvoker(RpcClient client, Class<T> apiClass) {
		this.client = client;
		this.apiClass = apiClass;
	}

	public Object invoke(Method method, Object[] args) throws Exception {
		System.out.println(">>>Invoke: " + method.getName() + "()");
		RpcRequest req = createRequest(method, args);
		System.out.println(">>>Req: " + req);
		RpcResult result = sendSync(req);
		System.out.println(">>>Result: " + result);
		checkThrowException(result);
		return handleResult(result, req.getReturnType());
	}

	private void checkThrowException(RpcResult result) throws Exception {
		if(!result.isSuccess()) {
			Exception except = (Exception)result.getException();
			if(except != null) {
				throw except;
			}
		}
	}

	private RpcRequest createRequest(Method method, Object[] args) {
		return new RpcRequest(apiClass, method.getName(), 
				args, method.getParameterTypes(), 
				method.getReturnType());
	}
	
	private RpcResult sendSync(RpcRequest req) throws Exception {
		return client.send(req).get();
	}
	
	//TODO
	private Object handleResult(RpcResult result, Class<?> returnType) {
		if(result == null) {
			return getDefaultValue(returnType);
		}
		return result.getData();
	}

	private Object getDefaultValue(Class<?> returnType) {
		if(returnType == byte.class 
				|| returnType == short.class
				|| returnType == int.class 
				|| returnType == long.class
				|| returnType == float.class
				|| returnType == double.class) {
			return 0;
		} else if(returnType == boolean.class) {
			return false;
		} else if(returnType == char.class) {
			return '\u0000';
		}
		//void, Array, Object
		return null;
	}

}
