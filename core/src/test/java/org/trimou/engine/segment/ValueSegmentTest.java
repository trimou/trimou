package org.trimou.engine.segment;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.trimou.AbstractEngineTest;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 *
 * @author Martin Kouba
 */
public class ValueSegmentTest extends AbstractEngineTest {

	@Before
	public void buildEngine() {
	}

	@Test
	public void testNoValueProblem() {
		try {
			MustacheEngineBuilder
					.newBuilder()
					.setProperty(
							EngineConfigurationKey.NO_VALUE_INDICATES_PROBLEM,
							true).build()
					.compileMustache("value_segment_problem", "{{foo}}")
					.render(null);
		} catch (MustacheException e) {
			if (!e.getCode().equals(MustacheProblem.RENDER_NO_VALUE)) {
				fail("Invalid problem");
			}
			System.out.println(e.getMessage());
		}
	}

}
