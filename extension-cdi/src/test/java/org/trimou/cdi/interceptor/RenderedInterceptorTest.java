package org.trimou.cdi.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.trimou.cdi.interceptor.RenderedInterceptor;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class RenderedInterceptorTest {

	@Deployment
	public static WebArchive createTestArchive() {

		MavenDependencyResolver resolver = DependencyResolvers.use(
				MavenDependencyResolver.class).loadMetadataFromPom("pom.xml");

		return ShrinkWrap
				.create(WebArchive.class)
				.addAsWebInfResource(
						new StringAsset(Descriptors
								.create(BeansDescriptor.class)
								.createInterceptors()
								.clazz(RenderedInterceptor.class.getName())
								.up().exportAsString()), "beans.xml")
				.addClasses(Foo.class)
				.addPackage(RenderedInterceptor.class.getPackage())
				.addAsLibraries(
						resolver.artifact("org.knir.trim:trim-core")
								.resolveAsFiles());
	}

	@Inject
	Foo foo;

	@Test
	public void testRendering() {
		assertNotNull(foo);
		assertEquals("pong", foo.ping("pong"));
	}

}
