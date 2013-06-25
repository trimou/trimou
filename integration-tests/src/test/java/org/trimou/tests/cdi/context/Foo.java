package org.trimou.tests.cdi.context;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.trimou.cdi.context.RenderingScoped;

@Named("foo")
@RenderingScoped
public class Foo {

	@Inject
	private Event<Foo> event;

	private Long createdAt;

	@PostConstruct
	public void init() {
		this.createdAt = System.nanoTime();
	}

	@PreDestroy
	public void destroy() {
		event.fire(this);
	}

	public Long getCreatedAt() {
		return createdAt;
	}

}
