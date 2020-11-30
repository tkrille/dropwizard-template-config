package com.expediagroup.dropwizard.bundle.configuration.freemarker;

import java.util.Map;

public interface EnvironmentProvider {
    Map<String, String> getEnvironment();
}
