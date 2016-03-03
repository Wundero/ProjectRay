package me.Wundero.ProjectRay.fanciful;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import com.google.common.collect.Maps;

public final class Reflection {
	private static String _versionString;

	public static synchronized String getVersion() {
		if (_versionString == null) {
			if (Bukkit.getServer() == null) {
				return null;
			}
			String str = Bukkit.getServer().getClass().getPackage().getName();
			_versionString = str.substring(str.lastIndexOf('.') + 1) + ".";
		}
		return _versionString;
	}

	private static final Map<String, Class<?>> _loadedNMSClasses = Maps
			.newHashMap();
	private static final Map<String, Class<?>> _loadedOBCClasses = Maps
			.newHashMap();

	public static synchronized Class<?> getNMSClass(String paramString) {
		if (_loadedNMSClasses.containsKey(paramString)) {
			return (Class<?>) _loadedNMSClasses.get(paramString);
		}
		String str = "net.minecraft.server." + getVersion() + paramString;
		Class<?> localClass = null;
		try {
			localClass = Class.forName(str);
		} catch (Exception localException) {
			localException.printStackTrace();
			_loadedNMSClasses.put(paramString, null);
			return null;
		}
		_loadedNMSClasses.put(paramString, localClass);
		return localClass;
	}

	public static synchronized Class<?> getOBCClass(String paramString) {
		if (_loadedOBCClasses.containsKey(paramString)) {
			return (Class<?>) _loadedOBCClasses.get(paramString);
		}
		String str = "org.bukkit.craftbukkit." + getVersion() + paramString;
		Class<?> localClass = null;
		try {
			localClass = Class.forName(str);
		} catch (Exception localException) {
			localException.printStackTrace();
			_loadedOBCClasses.put(paramString, null);
			return null;
		}
		_loadedOBCClasses.put(paramString, localClass);
		return localClass;
	}

	public static synchronized Object getHandle(Object paramObject) {
		try {
			return getMethod(paramObject.getClass(), "getHandle", new Class[0])
					.invoke(paramObject, new Object[0]);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}

	private static final Map<Class<?>, Map<String, Field>> _loadedFields = Maps
			.newHashMap();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static synchronized Field getField(Class<?> paramClass,
			String paramString) {
		Object localObject;
		if (!_loadedFields.containsKey(paramClass)) {
			localObject = Maps.newHashMap();
			_loadedFields.put(paramClass, (Map<String, Field>) localObject);
		} else {
			localObject = (Map) _loadedFields.get(paramClass);
		}
		if (((Map) localObject).containsKey(paramString)) {
			return (Field) ((Map) localObject).get(paramString);
		}
		try {
			Field localField = paramClass.getDeclaredField(paramString);
			localField.setAccessible(true);
			((Map) localObject).put(paramString, localField);
			return localField;
		} catch (Exception localException) {
			localException.printStackTrace();

			((Map) localObject).put(paramString, null);
		}
		return null;
	}

	private static final Map<Class<?>, Map<String, Map<ArrayWrapper<Class<?>>, Method>>> _loadedMethods = Maps
			.newHashMap();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static synchronized Method getMethod(Class<?> paramClass,
			String paramString, Class<?>... paramVarArgs) {
		if (!_loadedMethods.containsKey(paramClass)) {
			_loadedMethods.put(paramClass, new HashMap());
		}
		Map localMap1 = (Map) _loadedMethods.get(paramClass);
		if (!localMap1.containsKey(paramString)) {
			localMap1.put(paramString, new HashMap());
		}
		Map localMap2 = (Map) localMap1.get(paramString);
		ArrayWrapper localArrayWrapper = new ArrayWrapper(
				(Object[]) paramVarArgs);
		if (localMap2.containsKey(localArrayWrapper)) {
			return (Method) localMap2.get(localArrayWrapper);
		}
		for (Method localMethod : paramClass.getMethods()) {
			if ((localMethod.getName().equals(paramString))
					&& (Arrays.equals(paramVarArgs,
							localMethod.getParameterTypes()))) {
				localMethod.setAccessible(true);
				localMap2.put(localArrayWrapper, localMethod);
				return localMethod;
			}
		}
		localMap2.put(localArrayWrapper, null);
		return null;
	}
}
