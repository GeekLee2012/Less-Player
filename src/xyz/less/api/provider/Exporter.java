package xyz.less.api.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import xyz.less.api.IApi;
import xyz.less.util.ReflectUtil;

public final class Exporter {
	private static final HashMap<String, Class<?>> exportedMap = new HashMap<>();
	private static final HashMap<Class<?>, Object> cachedProviders = new HashMap<>();
	
	public static void export(Class<?> providerClass) {
		boolean found = false;
		//TODO 仅支持两级接口继承关系: provider实现的接口及其接口的父接口
		try {
			//provider实现的接口
			for(Class<?> cls : providerClass.getInterfaces()) { 
				//接口的父接口
				for(Class<?> pCls : cls.getInterfaces()) {
					if(pCls == IApi.class) {
						found = true;
						break ;
					}
				}
				if(found) {
					doExport(cls, providerClass);
					found = false;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doExport(Class<?> type, Class<?> providerClass) {
		try {
			Object cachedProvider = cachedProviders.get(providerClass);
			if(cachedProvider == null) {
				cachedProvider = providerClass.newInstance();
			}
			export(type, cachedProvider);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void export(Class<?> type, Object provider) {
		try {
			String exportedKey = type.getName();
			Class<?> providerClass = provider.getClass();
			exportedMap.put(exportedKey, providerClass);
			cachedProviders.put(providerClass, provider);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void exportObjectsFor(Class<?> providerClass, Object... objs) {
		export(providerClass);
		Object provider = getProvider(providerClass);
		if(objs != null) {
			for(Object obj : objs) {
				ReflectUtil.setFirstField(provider, obj);
			}
		}
	}
	
	public static void exportObjectForField(Class<?> providerClass, String fieldName, Object obj) {
		export(providerClass);
		Object provider = getProvider(providerClass);
		ReflectUtil.setField(provider, fieldName, obj);
	}
	
	public static Set<String> getExportedProviderKeys() {
		return exportedMap.keySet();
	}
	
	public static Object getProvider(String exportedKey) {
		try {
			Class<?> cachedProviderClass = exportedMap.get(exportedKey);
			return getProvider(cachedProviderClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getProvider(Class<?> providerClass) {
		try {
			return cachedProviders.get(providerClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void unexportAll() {
		exportedMap.clear();
		cachedProviders.clear();
	}
	
	public static void unexport(Class<?>... providerClasses) {
		try {
			for(Class<?> providerCls : providerClasses) {
				doUnexport(providerCls);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void doUnexport(Class<?> providerCls) {
		Iterator<String> iter = exportedMap.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			if(providerCls == exportedMap.get(key)) {
				iter.remove();
			}
		}
		cachedProviders.remove(providerCls);
	}
	
	public static void printProviderKeys() {
		Exporter.getExportedProviderKeys().forEach(e -> {
			System.out.println(">>>>>>Exported: " + e);
		});
	}
}
