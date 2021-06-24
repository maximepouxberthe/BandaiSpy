package com.skampe.utils.helpers;

import java.lang.reflect.InvocationTargetException;

public class InstanceHelper {

	private InstanceHelper() {
		// Hide public constructor
	}

	public static Object getInstance(final String className) {
		try {
			return Class.forName(className).newInstance();
		} catch (final IllegalAccessException | InstantiationException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getInstanceWithParams(final String className, final Class<?>[] paramsTypes,
			final Object[] params) {
		try {
			final Class<?> clazz = Class.forName(className);
			return clazz.getConstructor(paramsTypes).newInstance(params);
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getInstanceOfParentClass(final String className, final Class<?> parentClass) {
		try {
			final Class<?> childClass = Class.forName(className);
			if (parentClass.isAssignableFrom(childClass)) {
				return childClass.newInstance();
			}
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getInstanceOfParentClassWithParams(final String className, final Class<?> parentClass,
			final Class<?>[] paramsTypes, final Object[] params) {
		try {
			final Class<?> childClass = Class.forName(className);
			if (parentClass.isAssignableFrom(childClass)) {
				return childClass.getConstructor(paramsTypes).newInstance(params);
			}
		} catch (final ClassNotFoundException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
}
