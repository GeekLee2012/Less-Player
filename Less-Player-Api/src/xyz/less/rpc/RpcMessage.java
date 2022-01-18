package xyz.less.rpc;

import java.util.Arrays;

import xyz.less.util.SerializationUtil;


/**
 * 消息组成: [length,id,body] <br>
 * id所占字节数: 固定为32bytes <br>
 * body所占字节数: length(总字节数) - id字节数 <br>
 */
public class RpcMessage {
	private String id;
	private int length = -1;
	private byte[] body;
	public static final int ID_LENGTH = 32;
	public static final int BODY_OFFSET = 4 + ID_LENGTH;
	
	public RpcMessage() {
		
	}
	
	public RpcMessage(int length) {
		setLength(length);
	}
	
	public RpcMessage(RpcRequest request) {
		this(getRequestId(request), SerializationUtil.toByteArray(request));
	}
	
	public RpcMessage(String id, byte[] body) {
		setId(id);
		setBody(body);
		int bodyLen = (body != null ? body.length : 0);
		setLength(ID_LENGTH + bodyLen);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length > 0 ? length : -1;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public void appendToBody(byte[] bytes) {
		int oLen = body != null ? body.length : 0;
		int nLen = oLen + bytes.length;
		if(body != null) {
			body = Arrays.copyOf(body, nLen);
		} else {
			body = new byte[nLen];
		}
		System.arraycopy(bytes, 0, body, oLen, nLen - oLen);
	}
	
	public boolean isValid() {
		int bodyLen = (body != null ? body.length : 0);
		return this.length > 0 && this.length == (ID_LENGTH + bodyLen);
	}
	
	public RpcRequest getRequest() {
		return SerializationUtil.getObject(body, RpcRequest.class);
	}
	
	//32bytes = 13 + 1 + 18
	public static String getRequestId(RpcRequest request) {
		return String.format("%1$013d@%2$018d", request.hashCode(), System.currentTimeMillis());
	}
}
