package org.trimou.tests.mvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.trimou.tests.IntegrationTestUtils.createCDITestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.resolve;

import java.io.IOException;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

/**
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class TrimouViewEngineTest {

    // Exclude default JAX-RS impl in WildFly
    public static final StringAsset JBOSS_DEPLOYMENT_STRUCTURE_ASSET = new StringAsset(
            "<jboss-deployment-structure xmlns=\"urn:jboss:deployment-structure:1.2\"><deployment><exclude-subsystems><subsystem name=\"jaxrs\"/></exclude-subsystems></deployment></jboss-deployment-structure>");

    @Deployment(testable = false)
    public static WebArchive createTestArchive() {
        return createCDITestArchiveBase()
                .addClasses(TrimouApplication.class, SimpleController.class,
                        Bean.class)
                .addAsWebInfResource(JBOSS_DEPLOYMENT_STRUCTURE_ASSET,
                        "jboss-deployment-structure.xml")
                .addAsLibraries(resolve("org.trimou:trimou-extension-cdi"))
                .addAsLibraries(resolve("org.trimou:trimou-extension-mvc"))
                .addAsLibraries(
                        resolve("org.glassfish.jersey.containers:jersey-container-servlet"))
                .addAsLibraries(
                        resolve("org.glassfish.jersey.ext.cdi:jersey-cdi1x"))
                .addAsLibraries(
                        resolve("org.glassfish.jersey.ext:jersey-bean-validation"))
                .addAsLibraries(resolve("org.glassfish.ozark:ozark"))
                .addAsWebInfResource(
                        new StringAsset(
                                "{{request.requestURI}}:{{user}}:{{locale}}:{{bean.id}}:{{bean.id}}"),
                        "/views/simple.trimou");
    }

    @ArquillianResource
    URL contextPath;

    @Test
    public void testController()
            throws FailingHttpStatusCodeException, IOException {
        WebClient webClient = new WebClient();
        WebRequest request = new WebRequest(
                new URL(contextPath, "resources/simple?user=mike"));
        request.setAdditionalHeader("Accept-Language", "cs");
        Page page = webClient.getPage(request);
        String[] parts = page.getWebResponse().getContentAsString().split(":");
        assertEquals(5, parts.length);
        assertTrue(parts[0].endsWith("/resources/simple"));
        assertEquals("mike", parts[1]);
        assertEquals("cs", parts[2]);
        assertEquals(parts[3], parts[4]);
    }

}
