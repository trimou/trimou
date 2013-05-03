package org.trimou.servlet.resolver;

import static org.trimou.util.Priorities.after;

import javax.servlet.http.HttpServletRequest;

import org.trimou.engine.resolver.AbstractResolver;
import org.trimou.servlet.RequestHolder;
import org.trimou.util.Priorities;

/**
 *
 * @author Martin Kouba
 */
public class HttpServletRequestResolver extends AbstractResolver {

	public static final int SERVLET_REQUEST_RESOLVER_PRIORITY = after(Priorities.EXTENSION_RESOLVERS_DEFAULT_PRIORITY);

	private static final String REQUEST_KEY = "request";

	@Override
	public int getPriority() {
		return SERVLET_REQUEST_RESOLVER_PRIORITY;
	}

	@Override
	public Object resolve(Object contextObject, String name) {

		if (contextObject != null) {
			return null;
		}

		if (REQUEST_KEY.equals(name)) {
			HttpServletRequest request = RequestHolder.getCurrentRequest();
			if (request != null) {
				return new HttpServletRequestWrapper(request);
			}
		}
		return null;
	}

}
