package xyz.less.rpc;

import java.io.Serializable;

public final class RpcResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String msgId;
	private boolean success;
	private Object data;
	private Throwable exception;
	
	public RpcResult() {
		
	}
	public RpcResult(String msgId, boolean success, Object data) {
		this(msgId, success, data, null);
	}
	public RpcResult(String msgId, boolean success, Object data, Throwable exception) {
		this.success = success;
		this.msgId = msgId;
		this.data = data;
		this.exception = exception;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Throwable getException() {
		return exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	@Override
	public String toString() {
		return "RpcResult [success=" + success + ", data=" + data + "]";
	}
	
}
