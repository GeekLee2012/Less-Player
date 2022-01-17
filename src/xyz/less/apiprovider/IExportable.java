package xyz.less.apiprovider;

import xyz.less.util.ReflectUtil;

public interface IExportable {
	default public void exportFor(Class<?> providerClass) {
		exportObjectsFor(providerClass, this);
	}
	
	default public void exportObjectsFor(Class<?> providerClass, Object... objs) {
		Exporter.export(providerClass);
		Object provider = Exporter.getProvider(providerClass);
		if(objs != null) {
			for(Object obj : objs) {
				ReflectUtil.setFirstField(provider, obj);
			}
		}
	}
	
	default public void exportObjectForField(Class<?> providerClass, String fieldName, Object obj) {
		Exporter.export(providerClass);
		Object provider = Exporter.getProvider(providerClass);
		ReflectUtil.setField(provider, fieldName, obj);
	}
	
}
