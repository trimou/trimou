package org.trimou.tests.mvc;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class Bean {

    private String id;

    @PostConstruct
    public void init() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

}
