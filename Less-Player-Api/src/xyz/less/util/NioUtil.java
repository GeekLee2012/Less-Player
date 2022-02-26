package xyz.less.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public final class NioUtil {
	public static final int MAX_RETRY = 3;

	public static void registerAcceptOp(Selector selector, SelectableChannel sc) throws Exception {
		doRegisterOp(selector, sc, SelectionKey.OP_ACCEPT);
	}
	
	public static void registerConnectOp(Selector selector, SelectableChannel sc) throws Exception {
		doRegisterOp(selector, sc, SelectionKey.OP_CONNECT);
	}
	
	public static void registerReadOp(Selector selector, SelectableChannel sc) throws Exception {
		doRegisterOp(selector, sc, SelectionKey.OP_READ);
	}
	
	public static void registerWriteOp(Selector selector, SelectableChannel sc) throws Exception {
		doRegisterOp(selector, sc, SelectionKey.OP_WRITE);
	}

	public static void writeAll(SocketChannel sc, ByteBuffer buffer) throws IOException {
		if(sc.isOpen()) {
			sc.write(buffer);
			while(buffer.remaining() > 0) {
				sc.write(buffer);
			}
			buffer.clear();
		}
	}
	
	private static void doRegisterOp(Selector selector, SelectableChannel sc, int ops) throws IOException {
		if(sc.isOpen() && selector.isOpen()) {
			sc.register(selector, ops);
		}
	}
	
}
