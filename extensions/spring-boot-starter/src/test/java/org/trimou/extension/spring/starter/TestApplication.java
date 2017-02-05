/*
 * Copyright 2017 Trimou Team
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

package org.trimou.extension.spring.starter;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Configuration
@MinimalWebConfiguration
class TestApplication {

    @RequestMapping("/")
    String home(final Model model) {
        initModel(model);
        return "home";
    }

    @RequestMapping("/partial")
    String partial(final Model model) {
        initModel(model);
        return "partial";
    }

    @RequestMapping("/decorated")
    String decorated(final Model model) {
        initModel(model);
        return "decorated";
    }

    private void initModel(final Model model) {
        model.addAttribute("message", "Trimou rocks!");
        model.addAttribute("title", "Trimou");
    }
}
