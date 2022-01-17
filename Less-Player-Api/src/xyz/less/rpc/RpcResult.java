package xyz.less.rpc;

import java.io.Serializable;

public class RpcResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean success;
	private Object data;
	private Throwable exception;
	
	public RpcResult() {
	}
	public RpcResult(boolean success, Object data) {
		super();
		this.success = success;
		this.data = data;
	}
	public RpcResult(boolean success, Object data, Throwable exception) {
		super();
		this.success = success;
		this.data = data;
		this.exception = exception;
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
