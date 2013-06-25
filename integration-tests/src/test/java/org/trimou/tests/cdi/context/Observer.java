package org.trimou.tests.cdi.context;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class Observer {

	private List<Long> foos = new ArrayList<Long>();

	public void observeFoo(@Observes Foo foo) {
		foos.add(foo.getCreatedAt());
	}

	public List<Long> getFoos() {
		return foos;
	}

}
