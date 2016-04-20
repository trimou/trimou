package org.trimou.engine.resolver;

import org.trimou.engine.resource.ReleaseCallback;

public class DummyResolutionContext implements ResolutionContext {

    @Override
    public void registerReleaseCallback(ReleaseCallback callback) {
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public int getKeyPartIndex() {
        return 0;
    }

}
