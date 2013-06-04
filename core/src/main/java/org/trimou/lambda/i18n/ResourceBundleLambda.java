package org.trimou.lambda.i18n;

import java.util.ResourceBundle;

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.MustacheEngineBuilder.EngineBuiltCallback;
import org.trimou.engine.locale.LocaleSupport;
import org.trimou.engine.resolver.i18n.ResourceBundleResolver;
import org.trimou.lambda.InputProcessingLambda;
import org.trimou.util.Strings;

/**
 * Displays localized messages. It might be registered as a global lambda - see
 * {@link MustacheEngineBuilder#addGlobalData(String, Object)}.
 *
 * <pre>
 * {{#bundle}}key{{/bundle}}
 * </pre>
 *
 * @author Martin Kouba
 * @see ResourceBundle
 * @see ResourceBundleResolver
 */
public class ResourceBundleLambda extends InputProcessingLambda implements
		EngineBuiltCallback {

	private String baseName;

	private LocaleSupport localeSupport;

	/**
	 *
	 * @param baseName
	 */
	public ResourceBundleLambda(String baseName) {
		super();
		this.baseName = baseName;
	}

	/**
	 *
	 * @param baseName
	 * @param localeSupport
	 */
	public ResourceBundleLambda(String baseName, LocaleSupport localeSupport) {
		super();
		this.baseName = baseName;
		this.localeSupport = localeSupport;
	}

	@Override
	public String invoke(String text) {

		if (localeSupport == null) {
			throw new IllegalStateException(
					"ResourceBundleLambda requires a LocaleSupport instance to work properly");
		}

		ResourceBundle bundle = ResourceBundle.getBundle(baseName,
				localeSupport.getCurrentLocale());

		if (bundle.containsKey(text)) {
			return bundle.getObject(text).toString();
		}
		return Strings.EMPTY;
	}

	@Override
	public boolean isReturnValueInterpolated() {
		return false;
	}

	@Override
	public void engineBuilt(MustacheEngine engine) {
		this.localeSupport = engine.getConfiguration().getLocaleSupport();
	}

}
