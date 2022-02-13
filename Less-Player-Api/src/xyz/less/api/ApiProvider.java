package xyz.less.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import xyz.less.async.AsyncServices;
import xyz.less.bean.Constants;
import xyz.less.rpc.RpcClient;
import xyz.less.rpc.RpcInvoker;

public final class ApiProvider {
	private static int rpcPort = Constants.DEFAULT_RPC_PORT;
	private static Map<Class<?>, RpcClient> cachedClients = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public static <T extends IApi> T create(Class<T> apiClass) {
		if(apiClass != null) {
			return (T)Proxy.newProxyInstance(
						apiClass.getClassLoader(), 
						getAllInterfaces(apiClass), 
						new ApiHandler<>(apiClass));
		}
		return null;
	}
	
	public static void release(Class<?>... apiClasses) {
		if(apiClasses != null) {
			for(Class<?> apiCls : apiClasses) {
				doRelease(apiCls);
			}
		}
	}
	
	private static void doRelease(Class<?> apiClass) {
		RpcClient client = cachedClients.get(apiClass);
		if(client != null) {
			client.close();
		}
		cachedClients.remove(apiClass);
	}
	
	private static Class<?>[] getAllInterfaces(Class<?> apiClass) {
		return new Class<?>[] { apiClass };
	}
	
	public static void setRpcPort(int port) {
		rpcPort = port;
	}
	
	private static RpcClient getRpcClient(Class<?> apiClass) {
		RpcClient client = cachedClients.get(apiClass);
		if(client == null) {
			client = new RpcClient(rpcPort);
			cachedClients.put(apiClass, client);
		}
		return client;
	}
	
	private static void startClient(RpcClient client) {
		AsyncServices.submit(()-> {
			try {
				client.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private static class ApiHandler<T extends IApi> implements InvocationHandler {
		private RpcInvoker<T> rpcInvoker;
		private RpcClient rpcClient;
		
		public ApiHandler(Class<T> apiClass) {
			rpcClient = getRpcClient(apiClass);
			startClient(rpcClient);
			rpcInvoker = new RpcInvoker<>(rpcClient, apiClass);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return rpcInvoker.invoke(method, args);
		}
	}

}
