package org.trimou.engine.priority;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.trimou.AbstractEngineTest;

/**
 *
 * @author Martin Kouba
 */
public class PrioritiesTest extends AbstractEngineTest {

    @Test
    public void testComparator() {

        List<WithPriority> list = new ArrayList<>();
        list.add(new WithPriority() {

            @Override
            public int getPriority() {
                return 0;
            }
        });
        list.add(new WithPriority() {

            @Override
            public int getPriority() {
                return -1;
            }
        });
        list.add(new WithPriority() {

            @Override
            public int getPriority() {
                return 10;
            }
        });
        list.add(new WithPriority() {

            @Override
            public int getPriority() {
                return 1;
            }
        });
        Collections.sort(list, Priorities.higherFirst());
        assertEquals(10, list.get(0).getPriority());
        assertEquals(1, list.get(1).getPriority());
        assertEquals(0, list.get(2).getPriority());
        assertEquals(-1, list.get(3).getPriority());

        Collections.sort(list, Priorities.lowerFirst());
        assertEquals(-1, list.get(0).getPriority());
        assertEquals(0, list.get(1).getPriority());
        assertEquals(1, list.get(2).getPriority());
        assertEquals(10, list.get(3).getPriority());
    }

}
