package org.trimou.el;

import static org.junit.Assert.assertEquals;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELProcessor;
import javax.el.ELResolver;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.Configuration;
import org.trimou.util.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
public class ELHelperTest extends AbstractTest {

    @Test
    public void testHelper() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder().build();
        assertEquals("true", engine.compileMustache("elhelper_01", "{{el 'this eq this'}}").render(true));
        assertEquals("yes", engine.compileMustache("elhelper_02", "{{#el 'this gt 10'}}yes{{/el}}").render(10));
        assertEquals("yes", engine.compileMustache("elhelper_03", "{{#el 'this < 1'}}yes{{/el}}").render(0));
        assertEquals("10", engine.compileMustache("elhelper_04", "{{el 'this.age'}}").render(new Hammer(10)));
        assertEquals("1two",
                engine.compileMustache("elhelper_05", "{{#el '[1, \"two\"]'}}{{#each this}}{{this}}{{/each}}{{/el}}")
                        .render(null));
        assertEquals(
                "123", engine
                        .compileMustache("elhelper_06",
                                "{{#el '{\"one\":1, \"two\":2, \"three\":3}'}}{{one}}{{two}}{{three}}{{/el}}")
                        .render(null));
        assertEquals("no", engine.compileMustache("elhelper_07", "{{el 'this ? \"yes\" : \"no\"'}}").render(false));
        assertEquals("10", engine.compileMustache("elhelper_08", "{{#el 'foo < bar ? foo : bar'}}{{this}}{{/el}}")
                .render(ImmutableMap.of("foo", 10, "bar", 20)));
    }

    @Test
    public void testCustomELProcessorFactory() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setProperty(ELProcessorFactory.EL_PROCESSOR_FACTORY_KEY, new CustomELProcessorFactory()).build();
        assertEquals("true", engine.compileMustache("elpfcustom_01", "{{el 'foo'}}").render(null));
    }

    @Test
    public void testCustomELProcessorFactoryClazz() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .setProperty(ELProcessorFactory.EL_PROCESSOR_FACTORY_KEY, CustomELProcessorFactory.class.getName())
                .build();
        assertEquals("true", engine.compileMustache("elpfcustom_02", "{{el 'foo'}}").render(null));
    }

    static class CustomELProcessorFactory implements ELProcessorFactory {

        @Override
        public ELProcessor createELProcessor(Configuration configuration) {
            ELProcessor elp = new ELProcessor();
            elp.getELManager().addELResolver(new ELResolver() {

                @Override
                public void setValue(ELContext context, Object base, Object property, Object value) {
                }

                @Override
                public boolean isReadOnly(ELContext context, Object base, Object property) {
                    return false;
                }

                @Override
                public Object getValue(ELContext context, Object base, Object property) {
                    if (base == null && "foo".equals(property)) {
                        context.setPropertyResolved(true);
                        return Boolean.TRUE;
                    }
                    return null;
                }

                @Override
                public Class<?> getType(ELContext context, Object base, Object property) {
                    return null;
                }

                @Override
                public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
                    return null;
                }

                @Override
                public Class<?> getCommonPropertyType(ELContext context, Object base) {
                    return null;
                }
            });
            ;
            return elp;
        }

    }

}
