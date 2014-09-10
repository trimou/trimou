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
import org.trimou.prettytime.PrettyTimeHelper;
import org.trimou.prettytime.resolver.PrettyTimeResolver;
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
        // 1. CDI and servlet resolvers are registered automatically
        // 2. Precompile all available templates
        // 3. Do not escape values
        // 4. PrettyTimeResolver is disabled - we use helper instead
        // 5. Register helpers (set, isOdd, ...)
        // 6. ServletContextTemplateLocator is most suitable for webapps
        // 7. The current locale will be based on the Accept-Language header
        // 8. Minify all the templates
        // 9. Collect some basic rendering statistics
        return MustacheEngineBuilder
                .newBuilder()
                .setProperty(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES,
                        true)
                .setProperty(EngineConfigurationKey.SKIP_VALUE_ESCAPING, true)
                .setProperty(PrettyTimeResolver.ENABLED_KEY, false)
                .registerHelpers(
                        HelpersBuilder.extra()
                                .add("format", new DateTimeFormatHelper())
                                .add("pretty", new PrettyTimeHelper()).build())
                .addTemplateLocator(
                        new ServletContextTemplateLocator(1, "/templates",
                                "html"))
                .setLocaleSupport(new RequestLocaleSupport())
                .addMustacheListener(Minify.htmlListener())
                .addMustacheListener(simpleStatsCollector).build();
    }

}
