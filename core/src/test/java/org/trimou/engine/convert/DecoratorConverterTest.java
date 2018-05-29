package org.trimou.engine.convert;

import static org.junit.Assert.assertEquals;
import static org.trimou.engine.convert.DecoratorConverter.decorate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.Hammer;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.util.ImmutableList;

/**
 *
 * @author Martin Kouba
 */
public class DecoratorConverterTest extends AbstractTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testInterpolation() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addContextConverter(decorate(String.class)
                        .compute("reverse", string -> new StringBuilder(string).reverse().toString()).build())
                .addContextConverter(decorate(Hammer.class).put("translate", "kladivo")
                        .compute("name", hammer -> hammer.getName().toUpperCase()).build())
                .addContextConverter(decorate(List.class).compute("reversed", list -> {
                    List<?> reversed = new ArrayList<>(list);
                    Collections.reverse(reversed);
                    return reversed;
                }).build()).build();

        // javadoc example
        assertEquals("ooF", engine.compileMustache("{{reverse}}").render("Foo"));
        assertEquals("kladivo EDGAR 10", engine.compileMustache("{{translate}} {{name}} {{age}}").render(new Hammer()));
        assertEquals("bravo:charlie:alpha:", engine.compileMustache("{{#each this.reversed}}{{.}}:{{/each}}")
                .render(ImmutableList.of("alpha", "charlie", "bravo")));
    }

    @Test
    public void testGetDelegate() {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addContextConverter(decorate(Hammer.class).compute("name", h -> h.getName().toUpperCase()).build())
                .build();
        Hammer hammer = new Hammer();
        assertEquals("EDGAR vs Edgar", engine.compileMustache("{{name}} vs {{delegate.name}}").render(hammer));
    }

}
