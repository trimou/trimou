package org.trimou;

import java.util.HashMap;
import java.util.Map;

public class Hammer {

    public String nail = "NAIL";

    public Integer age = Integer.valueOf(15);

    private Map<String, Integer> map;

    public Hammer() {
        this.map = new HashMap<String, Integer>();
        this.map.put("foo", 10);
    }

    public String getName() {
        return "Edgar";
    }

    public Integer getAge() {
        return 10;
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
