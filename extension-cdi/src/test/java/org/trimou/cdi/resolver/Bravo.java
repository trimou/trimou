package org.trimou.cdi.resolver;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named("bravo")
@ApplicationScoped
public class Bravo extends BeanWithId  {

	private Integer age;

	@PostConstruct
	public void initAge() {
		age = 78;
	}

	public Integer getAge() {
		return age;
	}

}
