package org.trimou.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named("appScopedBean")
public class ApplicationScopedBean {

    public String getName() {
        return "foo";
    }

}
