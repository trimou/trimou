package org.trimou.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.trimou.handlebars.DecoratorHelper.decorate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class DecoratorHelperTest extends AbstractTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testHelper() {
        final MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper("decorateCat", decorate(Cat.class).put("const", "Cat")
                        // This overrides and delegates to Cat#getName()
                        .compute("name", cat -> cat.getName().toUpperCase())
                        .compute("overweight", (cat) -> cat.getWeight() > 4500 ? cat.getWeight() - 4500 : 0).build())
                .registerHelper("decorateIntList", decorate(List.class)
                        .compute("sum", l -> l.stream().mapToInt(e -> Integer.valueOf(e.toString())).sum()).build())
                .registerHelper("dummy",
                        decorate(String.class).delegateKey("foo").compute("toUpperCase", s -> s.toLowerCase()).build())
                .registerHelper("decorateStr", decorate(String.class)
                        .compute("reverse", s -> new StringBuilder(s).reverse().toString()).build())
                .build();

        // javadoc example
        assertEquals("ooF", engine.compileMustache("{{#decorateStr}}{{reverse}}{{/decorateStr}}").render("Foo"));

        List<Cat> cats = new ArrayList<>();
        cats.add(new Cat("Mikes", 3000));
        cats.add(new Cat("Mourek", 5000));
        assertEquals("Cat MIKES [3000g] has overweight 0g.Cat MOUREK [5000g] has overweight 500g.",
                engine.compileMustache(
                        "{{#each}}{{#decorateCat}}{{const}} {{name}} [{{weight}}g] has overweight {{overweight}}g.{{/decorateCat}}{{/each}}")
                        .render(cats));

        // Test wrong type
        try {
            engine.compileMustache("{{#decorateCat}}{{name}}{{/decorateCat}}").render("foo");
            fail();
        } catch (IllegalStateException expected) {
        }

        // Test iterable decorator
        assertEquals("6:123",
                engine.compileMustache("{{#decorateIntList}}{{sum}}:{{#each}}{{.}}{{/each}}{{/decorateIntList}}")
                        .render(Arrays.asList(1, 2, 3)));

        // Test delegate key
        assertEquals("lu vs LU",
                engine.compileMustache("{{#dummy}}{{toUpperCase}} vs {{foo.toUpperCase}}{{/dummy}}").render("Lu"));
    }

    class Cat {

        private final String name;

        private final Integer weight;

        public Cat(String name, Integer size) {
            this.name = name;
            this.weight = size;
        }

        public String getName() {
            return name;
        }

        public Integer getWeight() {
            return weight;
        }

    }

}
