package org.trimou.dropwizard.views;

import io.dropwizard.views.View;

public class RelativeView extends View {

    private final String name;

    public RelativeView(String name) {
        super("relative.trimou");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
