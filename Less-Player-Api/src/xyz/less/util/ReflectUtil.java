package xyz.less.util;

import java.lang.reflect.Field;

public final class ReflectUtil {

	public static void setFirstField(Object obj, Object value) {
		try {
			Class<?> cls = obj.getClass();
			Field[] fields = cls.getDeclaredFields();
			for(Field field : fields) {
				field.setAccessible(true);
				Class<?> type = field.getType();
				if(type == value.getClass()) {
					field.set(obj, value);
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setField(Object obj, String name, Object value) {
		try {
			Class<?> cls = obj.getClass();
			Field field = cls.getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
