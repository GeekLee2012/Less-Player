package xyz.less.api.provider;

import java.util.*;

import xyz.less.api.IApi;
import xyz.less.util.ReflectUtil;

public final class Exporter {
	/** [ ApiClsName, ApiProviderCls ] */
	private static final HashMap<String, Class<? extends IApi>> exportedMap = new HashMap<>();
	private static final HashMap<Class<? extends IApi>, Object> cachedProviders = new HashMap<>();
	
	public static void export(Class<? extends IApi> providerClass) {
		try {
			getApiInterfaces(providerClass).forEach(cls -> doExport(cls, providerClass));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/** 获取IApi接口的所有直接子接口 */
	private static Set<Class<? extends IApi>> getApiInterfaces(Class<? extends IApi> providerClass) {
		Set<Class<? extends IApi>> result = new HashSet<>();
		Class<?> superCls = providerClass.getSuperclass();
		if (superCls != null && IApi.class.isAssignableFrom(superCls)) {
			result.addAll(getApiInterfaces((Class<? extends IApi>) superCls));
		}
		Class<?>[] interfaces = providerClass.getInterfaces();
		if(interfaces != null) {
			for (Class<?> cls : interfaces) {
				if (IApi.class.isAssignableFrom(cls)) {
					if(cls == IApi.class) {	//递归出口
						result.add(providerClass);
					} else {
						result.addAll(getApiInterfaces((Class<? extends IApi>) cls));
					}
				}
			}
		}
		return result;
	}

	private static void doExport(Class<? extends  IApi> type, Class<? extends IApi> providerClass) {
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

	public static void export(Class<? extends  IApi> type, Object provider) {
		try {
			String exportedKey = type.getName();
			Class<? extends IApi> providerClass = (Class<? extends IApi>) provider.getClass();
			exportedMap.put(exportedKey, providerClass);
			cachedProviders.put(providerClass, provider);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void exportObjectsFor(Class<? extends IApi> providerClass, Object... objs) {
		export(providerClass);
		Object provider = getProvider(providerClass);
		if(objs != null) {
			for(Object obj : objs) {
				ReflectUtil.setFirstField(provider, obj);
			}
		}
	}
	
	public static void exportObjectForField(Class<? extends IApi> providerClass, String fieldName, Object obj) {
		export(providerClass);
		Object provider = getProvider(providerClass);
		ReflectUtil.setField(provider, fieldName, obj);
	}
	
	public static Set<String> getExportedProviderKeys() {
		return exportedMap.keySet();
	}
	
	public static Object getProvider(String exportedKey) {
		try {
			Class<? extends IApi> cachedProviderClass = exportedMap.get(exportedKey);
			return getProvider(cachedProviderClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getProvider(Class<? extends IApi> providerClass) {
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
	
	public static void unexport(Class<? extends IApi>... providerClasses) {
		try {
			for(Class<?> providerCls : providerClasses) {
				doUnexport(providerCls);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void unexportApis(Class<?>... apis) {
		try {
			for(Class<?> api : apis) {
				doUnexportApi(api);
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
				break ;
			}
		}
		cachedProviders.remove(providerCls);
	}
	
	private static void doUnexportApi(Class<?> api) {
		Iterator<String> iter = exportedMap.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			if(api.getName().equalsIgnoreCase(key)) {
				iter.remove();
				break ;
			}
		}
	}
	
	public static void printAll() {
		exportedMap.entrySet().forEach(e -> {
			System.out.println(">>>>>>Exported: Key=" + e.getKey() + ", Provider=" + e.getValue());
		});
	}
	
}
