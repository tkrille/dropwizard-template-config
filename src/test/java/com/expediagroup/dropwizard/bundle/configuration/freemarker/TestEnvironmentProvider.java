package com.expediagroup.dropwizard.bundle.configuration.freemarker;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class TestEnvironmentProvider implements EnvironmentProvider {
    private final Map<String, String> data = new HashMap<>();

    public void put(String name, String value) {
        data.put(name, value);
    }

    @Override
    public Map<String, String> getEnvironment() {
        return ImmutableMap.copyOf(data);
    }
}
