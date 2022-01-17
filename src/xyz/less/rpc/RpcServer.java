package xyz.less.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import xyz.less.apiprovider.Exporter;
import xyz.less.async.AsyncServices;

public final class RpcServer {
	private Selector selector;
	private ServerSocketChannel servChann;
	private ThreadLocal<RpcMessage> msgThreadLocal = new ThreadLocal<>();
	
	public RpcServer(int port) {
		try {
			selector = Selector.open();
			
			servChann = ServerSocketChannel.open();
			servChann.bind(new InetSocketAddress(port));
			servChann.configureBlocking(false);
			NioUtil.registerAcceptOp(selector, servChann);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		System.out.println("[Server] started 19900");
		while(true) {
			try {
				selector.select(1000);
				Set<SelectionKey> seletedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = seletedKeys.iterator();
				SelectionKey key = null;
				while(iter.hasNext()) {
					key = iter.next();
					iter.remove();
					try {
						doHandleKey(key);
					} catch(Exception e) {
						e.printStackTrace();
						if(key != null) {
							key.cancel();
							if(key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void doHandleKey(SelectionKey key) throws Exception {
		if(!key.isValid()) {
			return ;
		}
		if(key.isAcceptable()) {
			ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
			SocketChannel sc = ssc.accept();
			sc.configureBlocking(false);
			NioUtil.registerReadOp(selector, sc);
			System.out.println("[Server]Client Accepted");
		}
		if(key.isReadable()) {
			RpcMessage msg = decodeRpcMessage(key);
			if(msg != null && msg.isValid()) {
				RpcRequest request = getRequest(msg.getBody());
				System.out.println("[Server]Recv: " + request);
				if(request != null) {
					AsyncServices.submit(() -> {
						try {
							//执行方法调用
							Object provider = Exporter.getProvider(request.getClassName());
							if(provider != null) {
								MethodInvoker methodInvoker = new MethodInvoker(request.getMethodName(), 
										request.getArgs(), 
										request.getReturnType());
								Object returnObj = methodInvoker.invoke(provider);
								//返回响应
								doResponse(key, new RpcResult(methodInvoker.success(), returnObj, methodInvoker.getException()));
							} else {
								doResponse(key, new RpcResult(false, null, new RuntimeException("API Not Found!")));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
				msgThreadLocal.remove();
			}
		}
	}
	
	//TODO 粘包、拆包
	private RpcMessage decodeRpcMessage(SelectionKey key) throws IOException {
		System.out.println(">>>" + Thread.currentThread());
		RpcMessage msg = msgThreadLocal.get();
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(10240);
		int len = sc.read(buffer);
		buffer.flip();	//切换为读操作
//		System.out.println(len + ":" + buffer.remaining());
		if(len > 0) {
			byte[] bytes = null;
			int offset = 0;
			if(msg == null) {
				msg = new RpcMessage(buffer.getInt());
				msgThreadLocal.set(msg);
				offset = 4;
			}
			bytes = new byte[len - offset];
			buffer.get(bytes);
			msg.appendToBody(bytes);
		} else if(len < 0){
			key.cancel();
			sc.close();
		}
		return msg;
	}

	public RpcRequest getRequest(byte[] buf) {
		return SerializationUtil.getObject(buf, RpcRequest.class);
	}
	
	public void doResponse(SelectionKey key, RpcResult result) throws Exception {
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.wrap(SerializationUtil.toByteArray(result));
		sc.write(buffer);
		NioUtil.registerReadOp(selector, sc);
	}
	
	public void close() {
		try {
			if(servChann != null) {
				servChann.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if(selector != null) {
				selector.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[Server]closed");
	}
	
}
