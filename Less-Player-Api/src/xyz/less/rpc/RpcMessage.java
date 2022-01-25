package xyz.less.rpc;

import xyz.less.util.SerializationUtil;
import xyz.less.util.StringUtil;


/**
 * 消息: [Header,Body] <br>
 * 消息头Header: [MagicCode,Size,Id] <br>
 * Size: 总字节数 <br>
 * Id所占字节数: 固定为32bytes <br>
 * Body所占字节数: Size - Id字节数 <br>
 */
public final class RpcMessage {
	private String id;
	private int size = -1;
	private byte[] body;
	private int pos = 0;
	public static final String MAGIC_CODE = "nRpC";
	public static final int ID_SIZE = 32;
	public static final int HEADER_SIZE = (4 + 4 + 32);
	
	public RpcMessage() {
		
	}
	
	public RpcMessage(int size) {
		if(size < ID_SIZE) {
			throw new RuntimeException("Message's Size at least " + ID_SIZE + " bytes");
		}
		setSize(size);
		int bodySize = size - ID_SIZE;
		if(bodySize > 0) {
			setBody(new byte[bodySize]);
		}
	}
	
	public RpcMessage(RpcRequest request) {
		this(StringUtil.uuid(true), SerializationUtil.toByteArray(request));
	}
	
	public RpcMessage(String id, byte[] body) {
		setId(id);
		setBody(body);
		setSize(ID_SIZE + getBodySize());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setId(byte[] bytes) {
		setId(new String(bytes));
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size > 0 ? size : -1;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public int remaining() {
		return size <= 0 ? -1 : (size - pos);
	}
	
	private int getBodySize() {
		return body != null ? body.length : 0;
	}
	
	public void appendToBody(byte[] bytes) {
		if(bytes == null || bytes.length < 1) {
			return ;
		}
		int size = bytes.length;
		int remaining = remaining();
		if(body == null || size > remaining) {
			throw new RuntimeException("Message's size NOT enough, remainig bytes: " + remaining());
		}
		System.arraycopy(bytes, 0, body, pos, size);
		pos += size;
	}
	
	public boolean isValid() {
		return size > 0 && size == (ID_SIZE + getBodySize());
	}
	
	public RpcRequest getRequest() {
		return SerializationUtil.getObject(body, RpcRequest.class);
	}
	
	public static boolean isMagicCode(byte[] bytes) {
		return MAGIC_CODE.equals(new String(bytes));
	}
	
}
