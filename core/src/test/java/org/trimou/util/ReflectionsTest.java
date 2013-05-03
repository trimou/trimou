package org.trimou.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;

public class ReflectionsTest {

	@Test
	public void testGetReadMethods() {

		Map<String, Method> readMethods = Reflections.getReadMethods(Charlie.class);
		assertEquals(5, readMethods.size());

	}

	@Test
	public void testGetReadMethod() {
		assertNotNull(Reflections.getReadMethod(Charlie.class, "name"));
		assertNotNull(Reflections.getReadMethod(Charlie.class, "old"));
		assertNotNull(Reflections.getReadMethod(Charlie.class, "hasSomething"));
		assertNotNull(Reflections.getReadMethod(Charlie.class, "getAnotherName"));
		assertNotNull(Reflections.getReadMethod(Charlie.class, "anotherName"));
		assertNotNull(Reflections.getReadMethod(Charlie.class, "isOk"));
		assertNotNull(Reflections.getReadMethod(Charlie.class, "ok"));
		assertNull(Reflections.getReadMethod(Charlie.class, "getPrice"));
	}

	public static class Alpha {

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

		// Not read method - static
		public static String getInfo() {
			return null;
		}

		// Not read method - protected
		protected String getProtected() {
			return null;
		}

	}

	public static class Bravo extends Alpha {

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
