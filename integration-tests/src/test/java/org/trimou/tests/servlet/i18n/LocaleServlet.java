package org.trimou.tests.servlet.i18n;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.resolver.ResolutionContext;
import org.trimou.engine.resolver.i18n.LocaleAwareResolver;
import org.trimou.handlebars.Options;
import org.trimou.handlebars.i18n.LocaleAwareValueHelper;
import org.trimou.servlet.i18n.RequestLocaleSupport;

/**
 *
 * @author Martin Kouba
 */
@WebServlet("/test")
public class LocaleServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(LocaleServlet.class);

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        MustacheEngine engine = MustacheEngineBuilder
                .newBuilder()
                .addGlobalData("locale", Locale.FRENCH)
                .setLocaleSupport(new RequestLocaleSupport())
                .registerHelper("getLocale", new LocaleAwareValueHelper() {
                    @Override
                    public void execute(Options options) {
                        append(options, getLocale(options).getLanguage());
                    }
                    @Override
                    protected int numberOfRequiredParameters() {
                        return 0;
                    }
                })
                .addResolver(new LocaleAwareResolver(1000) {
                    @Override
                    public Object resolve(Object contextObject, String name,
                            ResolutionContext context) {
                        if (name.equals("resolverLocale")) {
                            return getCurrentLocale().getLanguage();
                        }
                        return null;
                    }
                }).build();

        String locale = engine.compileMustache("request_locale_support_test", "{{resolverLocale}}:::{{getLocale}}")
                .render(null);
        LOGGER.info("Trimou current locale: {}", locale);
        resp.setContentType("text/plain");
        resp.getWriter().append(locale);
    }

}
