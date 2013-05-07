package org.trimou.tests;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 *
 * @author Martin Kouba
 */
public final class IntegrationTestUtils {

	public static MavenDependencyResolver getResolver() {
		return DependencyResolvers.use(MavenDependencyResolver.class)
				.loadMetadataFromPom("pom.xml").goOffline();
	}

	public static WebArchive createCDITestArchiveBase() {
		return ShrinkWrap.create(WebArchive.class).addAsWebInfResource(
				EmptyAsset.INSTANCE, "beans.xml");
	}

}
