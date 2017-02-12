package org.trimou.engine.config;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class ConfigurationPropertiesTest {

    @Test
    public void testBuildPropertyKey() {
        assertEquals("org.trimou.engine.config.myPropertyName",
                ConfigurationProperties.buildPropertyKey("MY_PROPERTY_NAME",
                        new String[] { ConfigurationPropertiesTest.class
                                .getPackage().getName() }));
        assertEquals("org.trimou.engine.config.myPropertyName",
                ConfigurationProperties.buildPropertyKey("MY.PROPERTY.NAME",
                        ".", new String[] { ConfigurationPropertiesTest.class
                                .getPackage().getName() }));
    }

    @Test
    public void testConvertConfigValue() {
        assertEquals(10, ConfigurationProperties
                .convertConfigValue(Integer.class, "10"));
        assertEquals(10L,
                ConfigurationProperties.convertConfigValue(Long.class, "10"));
        assertEquals(Boolean.FALSE, ConfigurationProperties
                .convertConfigValue(Boolean.class, "10"));
    }

    @Test(expected = IllegalStateException.class)
    public void testConvertConfigValueFails() {
        ConfigurationProperties.convertConfigValue(BigDecimal.class, "10");
    }

}
