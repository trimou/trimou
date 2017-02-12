package org.trimou.tests.cdi.resolver;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named("delta")
@Dependent
public class Delta {

    public static List<Long> destructions = new ArrayList<>();

    private long createdAt;

    @PostConstruct
    public void init() {
        createdAt = System.nanoTime();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @PreDestroy
    public void destroy() {
        destructions.add(createdAt);
    }

    public static void reset() {
        destructions.clear();
    }

}
