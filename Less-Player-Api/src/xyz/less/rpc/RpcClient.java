package xyz.less.rpc;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import xyz.less.bean.Constants;

public final class RpcClient {
	private Selector selector;
	private SocketChannel chann;
	private volatile boolean close;
	private int port = Constants.RPC_PORT; 
	private CompletableFuture<RpcResult> future;
	
	public RpcClient() {
		doInit();
	}
	
	private void doInit() {
		try {
			selector = Selector.open();
			chann = SocketChannel.open();
			chann.configureBlocking(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setClose(boolean close) {
		this.close = close;
	}

	public void start() throws Exception {
		doConnect();
		doService();
	}
	
	private void doConnect() {
		try {
			SocketAddress addr = new InetSocketAddress(port);
			if(chann.isConnected() || chann.connect(addr)) {
				NioUtil.registerReadOp(selector, chann);
			} else {
				NioUtil.registerConnectOp(selector, chann);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doService() throws Exception {
		setClose(false);
		while(!close) {
			selector.select(1000);
			Set<SelectionKey> selKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selKeys.iterator();
			SelectionKey key = null;
			while(iter.hasNext()) {
				key = iter.next();
//				iter.remove();
				try {
					doHandleKey(key);
				} catch (Exception e) {
					e.printStackTrace();
					if(key != null) {
						key.cancel();
					}
				}
			}
		}
		if(selector != null) {
			chann.close();
			selector.close();
			selector = null;
		}
	}

	private void doHandleKey(SelectionKey key) throws Exception {
		if(!key.isValid()) {
			return ;
		}
		SocketChannel sc = (SocketChannel)key.channel();
		if(key.isConnectable()) {
			if(sc.finishConnect()) {
				NioUtil.registerReadOp(selector, sc);
				System.out.println("[Client] Connected");
			}
		} else if(key.isReadable()) {
			//TODO
			ByteBuffer buffer = ByteBuffer.allocate(10240);
			int len = sc.read(buffer);
			if(len > 0) {
				//获取接收到的数据
				setRpcResult(buffer);
			} else if(len < 0) {
				key.cancel();
//				sc.close();
			}
//			key.interestOps(SelectionKey.OP_READ);
		} 
	}
	
	private void setRpcResult(ByteBuffer buffer) throws Exception {
		buffer.flip();	//切换为读操作
		byte[] dstBytes = new byte[buffer.remaining()];
		buffer.get(dstBytes);
		RpcResult result = SerializationUtil.getObject(dstBytes, RpcResult.class);
		future.complete(result);
		NioUtil.registerWriteOp(selector, chann);
		System.out.println("[Client]Recv: " + result);
	}

	public Future<RpcResult> send(RpcRequest request) throws Exception {
		byte[] bytes = SerializationUtil.toByteArray(request);
		return doSend(new RpcMessage(bytes));
	}
	
	private Future<RpcResult> doSend(RpcMessage msg) throws Exception {
		future = new CompletableFuture<RpcResult>();
		ByteBuffer buffer = ByteBuffer.allocate(4 + msg.getLength());
		buffer.putInt(msg.getLength());
		buffer.put(msg.getBody());
		buffer.flip();
		NioUtil.writeAll(chann, buffer);
		NioUtil.registerReadOp(selector, chann);
		System.out.println("[Client]send bytes: " + msg.getLength());
		return future;
	}
	
	public void close() {
		setClose(true);
	}
	
}
