package org.trimou.example.ping;

import java.io.IOException;
import java.util.Collections;
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
    MustacheEngine engine;

    @Override
    public Configuration getConfiguration(ServletContext servletContext) {

        return ConfigurationBuilder.begin()

                // Allows to serve all the templates separately
                // The template name is dynamically resolved
                .addRule()
                .when(Direction.isInbound()
                        .and(Path.matches("/{name}.html"))
                        .and(Resource.exists("/templates/{name}.html")))
                        .perform(
                                Log.message(Level.INFO,
                                        "Client requested HTML file: {name}")
                                        .and(TemplateOperation.render(engine, "{name}")))
                // Static rewrite rule to serve a specific template
                .addRule()
                .when(Direction.isInbound()
                        .and(Path.matches("/cdi")))
                        .perform(
                                TemplateOperation.render(engine, "pingLogCdi"));
    }

    @Override
    public int priority() {
        return 1;
    }

    private static class TemplateOperation extends HttpOperation implements
            Parameterized {

        private final MustacheEngine engine;

        private final RegexParameterizedPatternBuilder templateNamePatternBuilder;

        private final String templateName;

        public static TemplateOperation render(MustacheEngine engine,
                String templateNamePattern) {
            return new TemplateOperation(engine, templateNamePattern);
        }

        private TemplateOperation(MustacheEngine engine,
                String templateNamePattern) {
            this.engine = engine;
            if (templateNamePattern.contains("{")) {
                this.templateNamePatternBuilder = new RegexParameterizedPatternBuilder(
                        templateNamePattern);
                this.templateName = null;
            } else {
                this.templateNamePatternBuilder = null;
                this.templateName = templateNamePattern;
            }
        }

        @Override
        public void performHttp(HttpServletRewrite event,
                EvaluationContext context) {

            HttpServletResponse response = event.getResponse();
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            try {
                engine.getMustache(getTemplateName(event, context)).render(response.getWriter(), null);
                response.flushBuffer();
            } catch (IOException e) {
                throw new RewriteException(
                        "Could not write value to response stream.", e);
            }
            event.abort();
        }

        @Override
        public Set<String> getRequiredParameterNames() {
            return templateNamePatternBuilder != null ? templateNamePatternBuilder.getRequiredParameterNames() : Collections.<String>emptySet();
        }

        @Override
        public void setParameterStore(ParameterStore store) {
            if(templateNamePatternBuilder != null) {
                templateNamePatternBuilder.setParameterStore(store);
            }
        }

        private String getTemplateName(HttpServletRewrite event,
                EvaluationContext context) {
            return templateNamePatternBuilder != null ? templateNamePatternBuilder.build(event, context) : templateName;
        }
    }

}
