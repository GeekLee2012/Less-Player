package xyz.less.rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerializationUtil {
	
	public static Object getObject(byte[] bytes) {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				ObjectInputStream in = new ObjectInputStream(bis)){
			return in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getObject(byte[] bytes, Class<T> type) {
		return (T)getObject(bytes);
	}
	
	public static byte[] toByteArray(Object obj) {
		try (ByteArrayOutputStream bao = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(bao)){
			out.writeObject(obj);
			return bao.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
