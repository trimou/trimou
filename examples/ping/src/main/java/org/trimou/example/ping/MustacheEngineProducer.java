package org.trimou.example.ping;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.engine.listener.SimpleStatsCollector;
import org.trimou.handlebars.HelpersBuilder;
import org.trimou.handlebars.i18n.DateTimeFormatHelper;
import org.trimou.minify.Minify;
import org.trimou.servlet.i18n.RequestLocaleSupport;
import org.trimou.servlet.locator.ServletContextTemplateLocator;

/**
 * A producer for {@link MustacheEngine}.
 *
 * @author Martin Kouba
 */
public class MustacheEngineProducer {

    @ApplicationScoped
    @Produces
    public MustacheEngine produceMustacheEngine(
            SimpleStatsCollector simpleStatsCollector) {
        // 1. CDI, servlet and PrettyTime resolvers are registered automatically
        // 2. Precompile all available templates
        // 3. Do not escape values
        // 4. Register extra helpers (set, isOdd, ...)
        // 5. Register basic date and time formatting helper
        // 6. ServletContextTemplateLocator is most suitable for webapps
        // 7. The current locale will be based on the Accept-Language header
        // 8. Minify all the templates
        // 9. Collect some basic rendering statistics
        return MustacheEngineBuilder
                .newBuilder()
                .setProperty(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES,
                        true)
                .setProperty(EngineConfigurationKey.SKIP_VALUE_ESCAPING, true)
                .registerHelpers(HelpersBuilder.extra().build())
                .registerHelper("format", new DateTimeFormatHelper())
                .addTemplateLocator(
                        new ServletContextTemplateLocator(1, "/templates",
                                "html"))
                .setLocaleSupport(new RequestLocaleSupport())
                .addMustacheListener(Minify.htmlListener())
                .addMustacheListener(simpleStatsCollector).build();
    }

}
