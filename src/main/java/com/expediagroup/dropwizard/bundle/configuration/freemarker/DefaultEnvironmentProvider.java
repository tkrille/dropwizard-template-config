package com.expediagroup.dropwizard.bundle.configuration.freemarker;

import java.util.Map;

public class DefaultEnvironmentProvider implements EnvironmentProvider {

    @Override
    public Map<String, String> getEnvironment() {
        return System.getenv();
    }
}
