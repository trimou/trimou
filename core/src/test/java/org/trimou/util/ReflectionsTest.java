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
		Map<String, Method> readMethods = Reflections
				.getAccesibleMethods(Charlie.class);
		assertEquals(6, readMethods.size());
	}

	@Test
	public void testGetAccesibleMembers() {
		assertNotNull(Reflections.getAccesibleMethod(Charlie.class, "name"));
		assertNotNull(Reflections.getAccesibleMethod(Charlie.class, "old"));
		assertNotNull(Reflections.getAccesibleMethod(Charlie.class,
				"hasSomething"));
		assertNotNull(Reflections.getAccesibleMethod(Charlie.class,
				"getAnotherName"));
		assertNotNull(Reflections.getAccesibleMethod(Charlie.class,
				"anotherName"));
		assertNotNull(Reflections.getAccesibleMethod(Charlie.class, "isOk"));
		assertNotNull(Reflections.getAccesibleMethod(Charlie.class, "ok"));
		assertNotNull(Reflections.getAccesibleMethod(Charlie.class, "info"));
		assertNull(Reflections.getAccesibleMethod(Charlie.class, "getPrice"));
		assertNotNull(Reflections.getAccesibleField(Charlie.class,
				"publicField"));
		assertNull(Reflections.getAccesibleField(Charlie.class, "privateField"));
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
