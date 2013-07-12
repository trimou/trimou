package org.trimou.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;

public class ReflectionsTest {

    @Test
    public void testGetMembers() {
        assertNotNull(Reflections.findMethod(Charlie.class, "name"));
        assertNotNull(Reflections.findMethod(Charlie.class, "old"));
        assertNotNull(Reflections.findMethod(Charlie.class, "hasSomething"));
        assertNotNull(Reflections.findMethod(Charlie.class, "getAnotherName"));
        assertNotNull(Reflections.findMethod(Charlie.class, "anotherName"));
        assertNotNull(Reflections.findMethod(Charlie.class, "isOk"));
        assertNotNull(Reflections.findMethod(Charlie.class, "ok"));
        assertNotNull(Reflections.findMethod(Charlie.class, "info"));
        assertNull(Reflections.findMethod(Charlie.class, "getPrice"));
        assertNotNull(Reflections.findField(Charlie.class, "publicField"));
        assertNull(Reflections.findField(Charlie.class, "privateField"));
    }

    public static class Alpha {

        @SuppressWarnings("unused")
        private String privateField;

        // OK
        public String getName() {
            return null;
        }

        // OK
        public int isOld() {
            return 1;
        }

        // OK
        public boolean hasSomething() {
            return true;
        }

        // Not read method - private
        @SuppressWarnings("unused")
        private BigDecimal getPrice() {
            return null;
        }

        // OK
        public String getInfo() {
            return null;
        }

        // Not read method - protected
        protected String getProtected() {
            return null;
        }

    }

    public static class Bravo extends Alpha {

        public final String publicField = "foo";

        // Not read method - has param
        public String getWithParam(String param) {
            return null;
        }

        // Not read method - no return value
        public void getNoReturnValue() {
        }

        // OK
        public String getAnotherName() {
            return null;
        }

    }

    public static class Charlie extends Bravo {

        // OK
        public Boolean isOk() {
            return null;
        }

    }

}
