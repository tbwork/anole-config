package com.github.tbwork.anole.loader;

import com.github.tbwork.anole.loader.spiext.ConfigSource;

public class ExtConfigSource implements ConfigSource {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public String retrieve(String key) {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
