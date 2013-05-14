package org.trimou.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.trimou.AbstractTest;
import org.trimou.engine.priority.HighPriorityComparator;
import org.trimou.engine.priority.WithPriority;


/**
 *
 * @author Martin Kouba
 */
public class HighPriorityComparatorTest extends AbstractTest {

	@Test
	public void testComparator() {

		List<WithPriority> list = new ArrayList<WithPriority>();
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
		Collections.sort(list, new HighPriorityComparator());
		assertEquals(10, list.get(0).getPriority());
		assertEquals(1, list.get(1).getPriority());
		assertEquals(0, list.get(2).getPriority());
		assertEquals(-1, list.get(3).getPriority());
	}

}
