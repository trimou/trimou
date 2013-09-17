package org.trimou.tests.servlet.i18n;

import static org.junit.Assert.assertEquals;
import static org.trimou.tests.IntegrationTestUtils.createTestArchiveBase;
import static org.trimou.tests.IntegrationTestUtils.getResolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;

/**
 *
 * @author Martin Kouba
 */
@RunAsClient
@RunWith(Arquillian.class)
public class RequestLocaleSupportTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return createTestArchiveBase().addClasses(LocaleServlet.class)
                .addAsLibraries(
                        getResolver().artifact(
                                "org.trimou:trimou-extension-servlet")
                                .resolveAsFiles());
    }

    @ArquillianResource
    URL contextPath;

    @Test
    public void testRequestLocaleSupport()
            throws FailingHttpStatusCodeException, MalformedURLException,
            IOException {
        WebClient webClient = new WebClient();
        WebRequest request = new WebRequest(new URL(contextPath, "test"));
        request.setAdditionalHeader("Accept-Language", "cs");
        TextPage page = webClient.getPage(request);
        assertEquals("cs", page.getContent());
    }

}
