package org.trimou.cdi;

import javax.inject.Named;

import org.trimou.cdi.context.RenderingScoped;

@RenderingScoped
@Named("renderingScopedBean")
public class RenderingScopedBean {

    public String getName() {
        return "bar";
    }

}
