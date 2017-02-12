package org.trimou;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Hammer {

    private String id;

    public String nail = "NAIL";

    public Integer age;

    private Map<String, Integer> map;

    public Hammer() {
        this(10);
    }

    public Hammer(Integer age) {
        this.id = UUID.randomUUID().toString();
        this.age = age;
        this.map = new HashMap<String, Integer>();
        this.map.put("foo", 10);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return "Edgar";
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isPersistent() {
        return false;
    }

    public Integer getAgeForName(String name) {
        return 20;
    }

    @Override
    public String toString() {
        return "HAMMER";
    }

    public Object getNull() {
        return null;
    }

    public ArchiveType getArchiveType() {
        return ArchiveType.WAR;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

}
