package org.trimou.tests;

import java.io.File;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.WebAppVersionType;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 *
 * @author Martin Kouba
 */
public final class IntegrationTestUtils {

    private static final String[] SERVLET_CONTAINER_CLASSES = { "org.jboss.arquillian.container.jetty.embedded_7.JettyEmbeddedContainer" };

    // See also http://weld.cdi-spec.org/documentation/#4
    private static final StringAsset CDI11_JBOSSALL_WORKAROUND_ASSET = new StringAsset(
            "<jboss xmlns=\"urn:jboss:1.0\"><weld xmlns=\"urn:jboss:weld:1.0\" require-bean-descriptor=\"true\"/></jboss>");

    public static WebArchive createCDITestArchiveBase() {
        WebArchive testArchive = createTestArchiveBase();
        testArchive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return testArchive;
    }

    public static File[] resolve(String ga) {
        return Maven.configureResolver().workOffline().loadPomFromFile("pom.xml").resolve(
                ga).withTransitivity().asFile();
    }

    public static WebArchive createTestArchiveBase() {
        WebArchive testArchive = ShrinkWrap.create(WebArchive.class);
        // Suppressing implicit bean archives without beans.xml
        testArchive.addAsManifestResource(CDI11_JBOSSALL_WORKAROUND_ASSET,
                "jboss-all.xml");
        // Add Weld servlet for embedded containers
        if (isServletContainer()) {
            testArchive.addAsLibraries(resolve("org.jboss.weld.servlet:weld-servlet"));
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
