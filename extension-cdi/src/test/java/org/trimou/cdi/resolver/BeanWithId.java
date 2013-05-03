package org.trimou.cdi.resolver;

import java.util.UUID;

import javax.annotation.PostConstruct;

public abstract class BeanWithId {

	private String id;

	@PostConstruct
	public void init() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

}
