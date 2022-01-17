package xyz.less.rpc;

import java.util.Arrays;

public class RpcMessage {
	private int length;
	private byte[] body;
	
	public RpcMessage() {
		this(-1);
	}
	
	public RpcMessage(int length) {
		setLength(length);
	}
	
	public RpcMessage(byte[] body) {
		if(body != null) {
			setBody(body);
			setLength(body.length);
		}
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
		int bodyLen = body != null ? body.length : 0;
		return this.length > 0 
				&& this.length == bodyLen;
	}
}
