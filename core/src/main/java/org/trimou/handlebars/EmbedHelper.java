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

import com.google.common.collect.Iterables;

/**
 * <code>
 * {{embed data.template}}
 * </code>
 *
 * @author Minkyu Cho
 */
public class EmbedHelper extends BasicValueHelper {

    @Override
    public void execute(Options options) {
        String sourceName = Iterables.getFirst(options.getParameters(), "").toString();
        String mustacheSource = options.source(sourceName);
        StringBuilder script = new StringBuilder();
        script.append("<script id=\"")
                .append(sourceName.replace("/", "_"))
                .append("\" type=\"text/template\">\n")
                .append(mustacheSource)
                .append("\n")
                .append("</script>");
        options.append(script.toString());
    }
}