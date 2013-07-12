package org.trimou.tests.cdi.resolver;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("charlie")
@SessionScoped
public class Charlie extends BeanWithId implements Serializable {

    private static final long serialVersionUID = 1854401872473617346L;

}
