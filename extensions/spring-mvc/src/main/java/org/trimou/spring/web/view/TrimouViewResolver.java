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
package org.trimou.spring.web.view;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.config.EngineConfigurationKey;
import org.trimou.exception.MustacheException;
import org.trimou.handlebars.Helper;
import org.trimou.servlet.locator.ServletContextTemplateLocator;

import java.util.Map;

/**
 * @author Minkyu Cho
 */
public class TrimouViewResolver extends AbstractTemplateViewResolver implements ViewResolver, InitializingBean {
	private String fileEncoding = System.getProperty("file.encoding");
	private boolean handlebarsSupport = true;
	private boolean debug = false;
	private boolean preCompile = false;
	private Map<String, Helper> helpers = Maps.newHashMap();
	private MustacheEngine engine;

	public TrimouViewResolver() {
		setViewClass(TrimouView.class);
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		TrimouView view = (TrimouView) super.buildView(viewName);
		try {
			Mustache template = engine.getMustache(viewName);
			view.setTemplate(template);
			return view;
		} catch (Exception e) {
			throw new MustacheException(view.getUrl() + " : "  + e.getMessage());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final int PRIORITY = 1;
		engine = MustacheEngineBuilder
				.newBuilder()
				.setProperty(EngineConfigurationKey.TEMPLATE_CACHE_ENABLED, isCache())
				.setProperty(EngineConfigurationKey.DEFAULT_FILE_ENCODING, getFileEncoding())
				.setProperty(EngineConfigurationKey.HANDLEBARS_SUPPORT_ENABLED, isHandlebarsSupport())
				.setProperty(EngineConfigurationKey.DEBUG_MODE, isDebug())
				.setProperty(EngineConfigurationKey.PRECOMPILE_ALL_TEMPLATES, isPreCompile())
				.registerHelpers(helpers)
				.addTemplateLocator(new ServletContextTemplateLocator(PRIORITY, getPrefix(), getSuffix(), getServletContext()))
				.build();
	}

	@Override
	protected Class<?> requiredViewClass() {
		return TrimouView.class;
	}

	public String getFileEncoding() {
		return fileEncoding;
	}

	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}

	public boolean isHandlebarsSupport() {
		return handlebarsSupport;
	}

	public void setHandlebarsSupport(boolean handlebarsSupport) {
		this.handlebarsSupport = handlebarsSupport;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isPreCompile() {
		return preCompile;
	}

	public void setPreCompile(boolean preCompile) {
		this.preCompile = preCompile;
	}

	public Map<String, Helper> getHelpers() {
		return helpers;
	}

	public void setHelpers(Map<String, Helper> helpers) {
		this.helpers = helpers;
	}

	public MustacheEngine getEngine() {
		return engine;
	}

	public void setEngine(MustacheEngine engine) {
		this.engine = engine;
	}
}