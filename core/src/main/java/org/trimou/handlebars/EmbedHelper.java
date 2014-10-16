/*
 * Copyright 2014 Minkyu Cho
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.handlebars;

/**
 * Embed the template source.
 *
 * <code>
 * {{embed data.template}}
 * </code>
 *
 * <p>By default, the template source is embedded as a JavaScript snippet.</p>
 *
 * @author Minkyu Cho
 */
public class EmbedHelper extends BasicValueHelper {

    private final SourceProcessor processor;

    public EmbedHelper() {
        this(new SourceProcessor() {
            @Override
            public String process(String mustacheName, String mustacheSource) {
                return new StringBuilder().append("<script id=\"")
                        .append(mustacheName.replace("/", "_"))
                        .append("\" type=\"text/template\">\n")
                        .append(mustacheSource).append("\n")
                        .append("</script>").toString();
            }
        });
    }

    public EmbedHelper(SourceProcessor sourceProcessor) {
        this.processor = sourceProcessor;
    }

    @Override
    public void execute(Options options) {
        String sourceName = options.getParameters().get(0).toString();
        options.append(processor.process(sourceName, options.source(sourceName)));
    }

    public interface SourceProcessor {

        String process(String mustacheName, String mustacheSource);
    }

}