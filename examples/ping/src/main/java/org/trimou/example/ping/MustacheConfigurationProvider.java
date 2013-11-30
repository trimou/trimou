package org.trimou.example.ping;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.logging.Logger.Level;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Log;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Resource;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.trimou.engine.MustacheEngine;

/**
 *
 * @author Martin Kouba
 */
@RewriteConfiguration
public class MustacheConfigurationProvider extends HttpConfigurationProvider {

    @Inject
    MustacheEngineProducer engineProducer;

    @Override
    public Configuration getConfiguration(ServletContext servletContext) {

        // Init MustacheEngineProducer during startup
        final MustacheEngine engine = engineProducer.initialize(servletContext);

        return ConfigurationBuilder.begin()

                .addRule()
                .when(Direction.isInbound()
                        .and(Path.matches("/{name}.html"))
                        .and(Resource.exists("/templates/{name}.html")))
                        .perform(
                                Log.message(Level.INFO,
                                        "Client requested HTML file: {name}")
                                        .and(TemplateOperation.render(engine, "{name}")));
    }

    @Override
    public int priority() {
        return 1;
    }

    private static class TemplateOperation extends HttpOperation implements
            Parameterized {

        private final MustacheEngine engine;

        private final RegexParameterizedPatternBuilder templateNamePatternBuilder;

        public static TemplateOperation render(MustacheEngine engine,
                String templateNamePattern) {
            return new TemplateOperation(engine, templateNamePattern);
        }

        private TemplateOperation(MustacheEngine engine,
                String templateNamePattern) {
            this.engine = engine;
            this.templateNamePatternBuilder = new RegexParameterizedPatternBuilder(
                    templateNamePattern);
        }

        @Override
        public void performHttp(HttpServletRewrite event,
                EvaluationContext context) {

            HttpServletResponse response = event.getResponse();
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            try {
                engine.getMustache(
                        templateNamePatternBuilder.build(event, context))
                        .render(response.getWriter(), null);
                response.flushBuffer();
            } catch (IOException e) {
                throw new RewriteException(
                        "Could not write value to response stream.", e);
            }
            event.abort();
        }

        @Override
        public Set<String> getRequiredParameterNames() {
            return templateNamePatternBuilder.getRequiredParameterNames();
        }

        @Override
        public void setParameterStore(ParameterStore store) {
            templateNamePatternBuilder.setParameterStore(store);
        }
    }

}
