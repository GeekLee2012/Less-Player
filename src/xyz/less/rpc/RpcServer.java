package xyz.less.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import xyz.less.api.ApiProvider;
import xyz.less.api.provider.Exporter;
import xyz.less.async.AsyncServices;
import xyz.less.util.NioUtil;
import xyz.less.util.SerializationUtil;

public final class RpcServer {
	private Selector selector;
	private ServerSocketChannel servChann;
	private ThreadLocal<RpcMessage> msgThreadLocal = new ThreadLocal<>();
	
	public RpcServer(int port) {
		boolean retry = false;
		do {
			retry = false;
			try {
				InetSocketAddress addr = new InetSocketAddress(port++);
				
				selector = Selector.open();
				servChann = ServerSocketChannel.open();
				servChann.bind(addr);
				servChann.configureBlocking(false);
				NioUtil.registerAcceptOp(selector, servChann);
				
				//设置API端port，提供给RpcClient读取
				ApiProvider.setRpcPort(addr.getPort());
				
				System.out.println("[Server] bind @" + addr.getPort());
			} catch (AlreadyBoundException e) {
				e.printStackTrace();
				retry = true;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		} while(retry);
	}
	
	public void start() {
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
				RpcRequest request = msg.getRequest();
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
								doResponse(key, new RpcResult(msg.getId(), methodInvoker.success(), returnObj, methodInvoker.getException()));
							} else {
								doResponse(key, new RpcResult(msg.getId(), false, null, new RuntimeException("API Not Found!")));
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
//				System.out.println(msg.getLength());
			} 
			if(msg.getId() == null){
				bytes = new byte[RpcMessage.ID_LENGTH];
				buffer.get(bytes);
				msg.setId(new String(bytes));
				offset = RpcMessage.BODY_OFFSET;
//				System.out.println(msg.getId());
			} 
			bytes = new byte[len - offset];
			buffer.get(bytes);
			msg.appendToBody(bytes);
		} 
//		else if(len < 0){
//			key.cancel();
//			sc.close();
//		}
		return msg;
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
