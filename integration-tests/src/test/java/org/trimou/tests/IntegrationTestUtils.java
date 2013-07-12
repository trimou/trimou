package org.trimou.tests;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.WebAppVersionType;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

/**
 *
 * @author Martin Kouba
 */
public final class IntegrationTestUtils {

    private static final String[] SERVLET_CONTAINER_CLASSES = { "org.jboss.arquillian.container.jetty.embedded_7.JettyEmbeddedContainer" };

    public static MavenDependencyResolver getResolver() {
        return DependencyResolvers.use(MavenDependencyResolver.class)
                .loadMetadataFromPom("pom.xml").goOffline();
    }

    public static WebArchive createCDITestArchiveBase() {
        return createTestArchiveBase().addAsWebInfResource(EmptyAsset.INSTANCE,
                "beans.xml");
    }

    public static WebArchive createTestArchiveBase() {

        WebArchive testArchive = ShrinkWrap.create(WebArchive.class);

        if (isServletContainer()) {
            testArchive.setWebXML(new StringAsset(Descriptors
                    .create(WebAppDescriptor.class)
                    .version(WebAppVersionType._3_0)
                    .createListener()
                    .listenerClass(
                            "org.jboss.weld.environment.servlet.Listener").up()
                    .exportAsString()));
        }
        return testArchive;
    }

    private static boolean isServletContainer() {
        for (String className : SERVLET_CONTAINER_CLASSES) {
            if (LoadableExtension.Validate.classExists(className)) {
                return true;
            }
        }
        return false;
    }

}
