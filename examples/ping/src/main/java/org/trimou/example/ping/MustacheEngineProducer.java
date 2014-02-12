package org.trimou.example.ping;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletContext;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.listener.SimpleStatsCollector;
import org.trimou.engine.resolver.i18n.DateTimeFormatResolver;
import org.trimou.handlebars.NumberIsOddHelper;
import org.trimou.minify.Minify;
import org.trimou.servlet.i18n.RequestLocaleSupport;
import org.trimou.servlet.locator.ServletContextTemplateLocator;

/**
 * Make an instance of {@link MustacheEngine} an injectable resource. The bean
 * is application-scoped, so there's one engine per application.
 *
 * @author Martin Kouba
 */
@ApplicationScoped
public class MustacheEngineProducer {

    private MustacheEngine engine;

    private final SimpleStatsCollector statsCollector = new SimpleStatsCollector();

    public MustacheEngine initialize(ServletContext servletContext) {

        if (engine == null) {

            // 1. CDI, servlet and PrettyTime resolvers are registered
            // automatically
            // 2. Precompile all available templates
            // 3. Register NumberIsOddHelper
            // 4. Add basic date and time formatting resolver
            // 5. ServletContextTemplateLocator is most suitable for webapp
            // 6. The current locale will be based on the Accept-Language header
            // 7. Minify all the templates
            // 8. Collect some basic rendering statistics
            engine = MustacheEngineBuilder
                    .newBuilder()
                    .setProperty(
                            EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES,
                            true)
                    .registerHelper("isOdd", new NumberIsOddHelper())
                    .addResolver(new DateTimeFormatResolver())
                    .addTemplateLocator(
                            new ServletContextTemplateLocator(1, "/templates",
                                    "html", servletContext))
                    .setLocaleSupport(new RequestLocaleSupport())
                    .addMustacheListener(Minify.htmlListener())
                    .addMustacheListener(statsCollector)
                    .build();
        }
        return engine;
    }

    @Produces
    public MustacheEngine produceMustacheEngine() {
        return engine;
    }

    @Produces
    public SimpleStatsCollector produceStatsCollector() {
        return statsCollector;
    }

}
