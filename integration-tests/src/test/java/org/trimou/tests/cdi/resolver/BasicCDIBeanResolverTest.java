package org.trimou.tests.cdi.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;
import java.util.Map;

import javax.enterprise.inject.spi.Extension;
import javax.naming.spi.Resolver;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.api.Mustache;
import org.trimou.cdi.resolver.CDIBeanResolver;
import org.trimou.cdi.resolver.CDIBeanResolverExtension;
import org.trimou.engine.MustacheEngineBuilder;

import com.google.common.collect.ImmutableMap;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BasicCDIBeanResolverTest {

	@Deployment
	public static WebArchive createTestArchive() {

		MavenDependencyResolver resolver = DependencyResolvers.use(
				MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

		return ShrinkWrap
				.create(WebArchive.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(Alpha.class, Bravo.class, Charlie.class, BeanWithId.class)
				.addPackage(CDIBeanResolver.class.getPackage())
				.addAsLibraries(
						resolver.artifact("org.trimou:trimou-core")
								.resolveAsFiles())
				.addAsServiceProvider(Extension.class,
						CDIBeanResolverExtension.class)
				.addAsServiceProvider(Resolver.class, CDIBeanResolver.class);
	}

	@Inject
	Alpha alpha;

	@Inject
	Bravo bravo;

	@Inject
	Charlie charlie;

	@Test
	public void testResolution() {

		assertNotNull(alpha);
		assertNotNull(bravo);
		assertNotNull(charlie);

		Mustache mustache = MustacheEngineBuilder
				.newBuilder()
				.build()
				.compile("cdi_bean_resolver_test",
						"{{hello}}: {{#alpha}}{{id}} {{bravo.age}}{{/alpha}} {{bravo.id}} {{charlie.id}}{{neverExisted}}");

		String result = String.format("Hello: %s 78 %s %s", alpha.getId(), bravo.getId(), charlie.getId());
		Map<String, Object> data = ImmutableMap.<String, Object>of("hello", "Hello");

		StringWriter writer = new StringWriter();
		mustache.render(writer, data);
		assertEquals(result, writer.toString());
		writer = new StringWriter();
		mustache.render(writer, data);
		assertEquals(result, writer.toString());
	}

}
