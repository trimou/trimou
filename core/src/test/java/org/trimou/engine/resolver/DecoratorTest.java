package org.trimou.engine.resolver;

import static org.junit.Assert.assertEquals;
import static org.trimou.engine.resolver.Decorator.decorate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.Hammer;

/**
 *
 * @author Martin Kouba
 */
public class DecoratorTest extends AbstractEngineTest {

    @Test
    public void testInterpolation() {
        // javadoc example
        assertEquals("ooF", engine.compileMustache("{{reverse}}").render(
                decorate("Foo").compute("reverse", s -> new StringBuilder(s).reverse().toString()).build(engine)));

        Hammer hammer = new Hammer();
        assertEquals("kladivo EDGAR 10",
                engine.compileMustache("{{translate}} {{name}} {{age}}").render(decorate(hammer)
                        .put("translate", "kladivo").compute("name", h -> h.getName().toUpperCase()).build(engine)));

        List<Hammer> hammers = new ArrayList<>();
        hammers.add(new Hammer(0));
        hammers.add(new Hammer(1));
        hammers.add(new Hammer(2));
        assertEquals("123", engine.compileMustache("{{#each}}{{age}}{{/each}}").render(hammers.stream()
                .map(h -> decorate(h).put("age", h.getAge() + 1).build(engine)).collect(Collectors.toList())));

        // Test iterable decorators
        int[] array = { 1, 3, 5 };
        List<Integer> list = Arrays.asList(1, 3, 5);
        assertEquals("1 + 3 + 5 = 9",
                engine.compileMustache("{{#each}}{{.}}{{#iterHasNext}} + {{/iterHasNext}}{{/each}} = {{sum}}")
                        .render(decorate(array).compute("sum", vals -> Arrays.stream(array).sum()).build(engine)));
        assertEquals("1 + 3 + 5 = 9",
                engine.compileMustache("{{#each}}{{.}}{{#iterHasNext}} + {{/iterHasNext}}{{/each}} = {{sum}}")
                        .render(decorate(list).compute("sum", l -> l.stream().mapToInt(Integer::intValue).sum())
                                .build(engine)));
    }

    @Test
    public void testGetDelegate() {
        Hammer hammer = new Hammer();
        assertEquals("EDGAR vs Edgar", engine.compileMustache("{{name}} vs {{delegate.name}}")
                .render(decorate(hammer).compute("name", h -> h.getName().toUpperCase()).build(engine)));
        assertEquals("EDGAR vs Edgar", engine.compileMustache("{{name}} vs {{foo.name}}").render(
                decorate(hammer).delegateKey("foo").compute("name", h -> h.getName().toUpperCase()).build(engine)));
    }

    @Test
    public void testNestedDecorator() {
        Hammer hammer = new Hammer(5);
        assertEquals("5:Edgar:bar",
                engine.compileMustache("{{age}}:{{foo}}:{{bar}}")
                        .render(decorate(decorate(hammer).compute("foo", h -> h.getName()).build(engine))
                                .put("bar", "bar").build(engine)));

    }

}
