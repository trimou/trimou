/*
 * Copyright 2017 Martin Kouba
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
package org.trimou.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.trimou.util.Strings;

/**
 * A simple helper which allows to inspect the build info.
 *
 * @author Martin Kouba
 * @since 2.4
 */
public final class BuildInfo {

    public static final String BUILD_PROPERTIES_FILE = "/trimou-build.properties";

    public static BuildInfo load() {

        String version = null;
        String timestamp = null;

        // First try to get trimou-build.properties file
        try (InputStream in = MustacheEngineBuilder.class.getResourceAsStream(BUILD_PROPERTIES_FILE)) {
            if (in != null) {
                Properties buildProperties = new Properties();
                buildProperties.load(in);
                version = buildProperties.getProperty("version");
                timestamp = buildProperties.getProperty("timestamp");
            }
        } catch (IOException e) {
            // No-op
        }
        if (version == null) {
            // If not available try to use the manifest info
            version = MustacheEngineBuilder.class.getPackage().getImplementationVersion();
        }
        if (Strings.isEmpty(version)) {
            version = "SNAPSHOT";
        }
        if (Strings.isEmpty(timestamp)) {
            timestamp = "n/a";
        }
        return new BuildInfo(version, timestamp);
    }

    private final String version;

    private final String timestamp;

    /**
     *
     * @param version
     * @param timestamp
     */
    private BuildInfo(String version, String timestamp) {
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTimestampDate() {
        int idx = timestamp.indexOf('T');
        if (idx > 0) {
            return timestamp.substring(0, idx);
        }
        return timestamp;
    }

    @Override
    public String toString() {
        return "BuildInfo [version=" + version + ", timestamp=" + timestamp + "]";
    }

}
