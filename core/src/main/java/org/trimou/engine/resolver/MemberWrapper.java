package org.trimou.engine.resolver;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Martin Kouba
 */
public interface MemberWrapper {

	/**
	 *
	 * @param instance
	 * @return the member value for the given instance
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object getValue(Object instance) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException;

}
