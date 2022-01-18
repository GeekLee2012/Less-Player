package xyz.less.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import xyz.less.util.NioUtil;
import xyz.less.util.SerializationUtil;

public final class RpcClient {
	private Selector selector;
	private SocketChannel chann;
	private volatile boolean close;
	private SocketAddress addr;
	private BlockingQueue<RpcMessage> sendingQueue;
	private Map<String, CompletableFuture<RpcResult>> futureMap;
	
	public RpcClient(int port) {
		try {
			addr = new InetSocketAddress(port);
			
			selector = Selector.open();
			chann = SocketChannel.open();
			chann.configureBlocking(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		sendingQueue = new ArrayBlockingQueue<>(10, true);
		futureMap = new ConcurrentHashMap<>();
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
		onClose();
	}

	private void doHandleKey(SelectionKey key) throws Exception {
		if(!key.isValid()) {
			return ;
		}
		SocketChannel sc = (SocketChannel)key.channel();
		if(key.isConnectable()) {
			if(sc.finishConnect()) {
				key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
//				NioUtil.registerWriteOp(selector, sc);
			}
		} else if(key.isReadable()) {
			//TODO
			ByteBuffer buffer = ByteBuffer.allocate(10240);
			int len = sc.read(buffer);
			if(len > 0) {
				//获取接收到的数据
				setRpcResult(buffer);
			} 
//			else if(len < 0) {
//				key.cancel();
//				sc.close();
//			}
//			key.interestOps(SelectionKey.OP_READ);
		} else if(key.isWritable()) {
			RpcMessage msg = sendingQueue.poll(1000,TimeUnit.MILLISECONDS);
			if(msg != null) {
				System.out.println(">>>sending: " + msg);
				doSend(sc, msg);
			}
		}
	}
	
	private void setRpcResult(ByteBuffer buffer) throws Exception {
		buffer.flip();	//切换为读操作
		byte[] dstBytes = new byte[buffer.remaining()];
		buffer.get(dstBytes);
		RpcResult result = SerializationUtil.getObject(dstBytes, RpcResult.class);
		String id = result.getMsgId();
		futureMap.get(id).complete(result);
		futureMap.remove(id);
		NioUtil.registerWriteOp(selector, chann);
	}

	public Future<RpcResult> send(RpcRequest request) throws Exception {
		CompletableFuture<RpcResult> future = new CompletableFuture<>();
		RpcMessage msg = new RpcMessage(request);
		sendingQueue.offer(msg);
		futureMap.put(msg.getId(), future);
		NioUtil.registerWriteOp(selector, chann);
		return future;
	}
	
	private void doSend(SocketChannel sc, RpcMessage msg) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(36 + msg.getLength());
		buffer.putInt(msg.getLength()); //4bytes
		buffer.put(msg.getId().getBytes()); //32bytes
		buffer.put(msg.getBody());
		buffer.flip();
		NioUtil.writeAll(sc, buffer);
		NioUtil.registerReadOp(selector, chann);
		System.out.println("[Client]Bytes Sent: " + msg.getLength());
	}
	
	public void close() {
		setClose(true);
	}
	
	private void onClose() throws IOException {
		if(selector != null) {
			chann.close();
			selector.close();
			selector = null;
		}
	}
	
}
