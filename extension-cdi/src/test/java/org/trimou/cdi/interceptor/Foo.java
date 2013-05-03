package org.trimou.cdi.interceptor;

import org.trimou.cdi.interceptor.Rendered;

import com.google.common.collect.ImmutableMap;

public class Foo {

	@Rendered(template = "foo.txt")
	public Object ping(String value) {
		return ImmutableMap.<String, Object> of("foo", value);
	}

}
