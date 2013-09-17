package org.trimou.tests.servlet.i18n;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.i18n.LocaleAwareResolver;
import org.trimou.servlet.i18n.RequestLocaleSupport;

/**
 *
 * @author Martin Kouba
 */
@WebServlet("/test")
public class LocaleServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory
            .getLogger(LocaleServlet.class);

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String locale = MustacheEngineBuilder
                .newBuilder()
                .setLocaleSupport(new RequestLocaleSupport())
                .addResolver(new LocaleAwareResolver(1000) {

                    @Override
                    public Object resolve(Object contextObject, String name,
                            ResolutionContext context) {
                        if (name.equals("locale")) {
                            return getCurrentLocale().getLanguage();
                        }
                        return null;
                    }
                }).build()
                .compileMustache("request_locale_support_test", "{{locale}}")
                .render(null);
        logger.info("Trimou current locale: {}", locale);
        resp.setContentType("text/plain");
        resp.getWriter().append(locale);
    }

}
