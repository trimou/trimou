/*
 * Copyright 2018 Trimou team
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
package org.trimou.example.decorator;

import static org.trimou.engine.convert.DecoratorConverter.decorate;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;

/**
 *
 * @author Martin Kouba
 */
public class DecoratorConverterExample {

    public static void main(String[] args) {

        // Build the engine
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                // Register context converter that decorates all instances of User
                .addContextConverter(decorate(User.class)
                        .compute("age", user -> Year.now().minusYears(user.getYearOfBirth().getValue())).build())
                .build();

        // Compile the template
        Mustache template = engine.compileMustache("users",
                "Users\n=====\n{{#each}}{{iterIndex}}. {{name}} is {{age}} years old\n{{/each}}");

        // Prepare some data
        List<User> users = new ArrayList<>();
        users.add(new User("Bob", Year.of(1980)));
        users.add(new User("Susan", Year.of(1985)));
        users.add(new User("Perry", Year.of(1970)));

        System.out.println(String.format("Start rendering of %s...", template.getName()));

        long start = System.nanoTime();
        // And now render the data
        String output = template.render(users);
        long end = System.nanoTime() - start;

        System.out.println(String.format("Template %s rendered in %s ms:\n", template.getName(), end / 1000000));
        System.out.println(output);
    }

}
