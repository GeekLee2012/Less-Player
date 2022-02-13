package xyz.less.rpc;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

import xyz.less.api.ApiProvider;
import xyz.less.api.provider.Exporter;
import xyz.less.async.AsyncServices;
import xyz.less.util.NioUtil;
import xyz.less.util.SerializationUtil;

/**
 * 徒手撸RPC，暂时不引入Netty
 */
public final class RpcServer {
	private Selector selector;
	private ServerSocketChannel servChann;
	private ThreadLocal<RpcMessage> cachedMsg = new ThreadLocal<>();
	private ByteBuffer cachedBuf = ByteBuffer.allocate(1024 * 1024); //1MB
	
	public RpcServer(int port) {
		doInit(port);
	}
	
	private void doInit(int port) {
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
			} catch (BindException e) {
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
				while(iter.hasNext()) {
					SelectionKey key = iter.next();
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

	public Future<?> startAsync() {
		return AsyncServices.submit(() -> {
			start();
		});
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
			//并发问题
			handleRpcMessage(key);
		}
	}

	private RpcMessage decodeRpcMessageSafely(SelectionKey key) {
		try {
			return decodeMessage(key);
		} catch (Exception e) {
			e.printStackTrace();
			cachedMsg.remove();
			cachedBuf.clear();
		}
		return null;
	}

	private void handleRpcMessage(SelectionKey key) {
		RpcMessage msg = decodeRpcMessageSafely(key);
		if(msg != null && msg.isValid()) {
			cachedMsg.remove();
			RpcRequest request = msg.getRequest();
			System.out.println("[Server]Recv: " + request);
			if(request != null) {
				AsyncServices.submit(() -> {
					try {
						//执行方法调用
						Object provider = Exporter.getProvider(request.getClassName());
						if(provider != null) {
							MethodInvoker methodInvoker = new MethodInvoker(
									request.getMethodName(),
									request.getArgs(),
									request.getArgTypes(),
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
		}
	}
	
	//处理拆包、粘包
	private RpcMessage decodeMessage(SelectionKey key) throws IOException {
		System.out.println(">>>" + Thread.currentThread());
		RpcMessage msg = cachedMsg.get();
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(10240);
		int size = sc.read(buffer);
		buffer.flip();	//切换为读操作
		if(size > 0) {
			cachedBuf.put(buffer);
			cachedBuf.flip(); //切换为读操作
			size = cachedBuf.limit();
		}
		if(size > 0) {
			int offset = 0;
			if(msg == null) { 
				byte[] bytes = new byte[RpcMessage.MAGIC_CODE.length()];
				cachedBuf.get(bytes);
				if(RpcMessage.isMagicCode(bytes)) {
					msg = new RpcMessage(cachedBuf.getInt());
					cachedMsg.set(msg);
//					System.out.println(msg.getLength());
				}
			}
			//异常情况下，msg可能为null，如读取到的数据非RpcMessage数据
			if(msg != null && msg.getId() == null){
				byte[] bytes = new byte[RpcMessage.ID_SIZE];
				cachedBuf.get(bytes);
				msg.setId(bytes);
				offset = RpcMessage.HEADER_SIZE;
//				System.out.println(msg.getId());
			} 
			//粘包处理
			int unsolvedSize = size - offset;
			if(msg != null && msg.remaining() > 0 && unsolvedSize > 0) {
				if(msg.remaining() >= unsolvedSize) {
					byte[] bytes = new byte[unsolvedSize];
					cachedBuf.get(bytes);
					msg.appendToBody(bytes);
					cachedBuf.clear();
					System.out.println("CachedBuffer Clear");
				} else { //发生粘包
					byte[] bytes = new byte[msg.remaining()];
					cachedBuf.get(bytes);
					msg.appendToBody(bytes);
					//整理粘包数据，等待下次处理
					cachedBuf.compact();
				}
			}
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
