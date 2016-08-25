package de.thomaskrille.dropwizard_template_config;

import java.util.HashMap;
import java.util.Map;

public class TestCustomProvider implements TemplateConfigProvider {
    private final String namespace;
    private final Map<String, String> data = new HashMap<>();

    public TestCustomProvider(String namespace) {
        this.namespace = namespace;
    }

    public void put(String name, String value) {
        data.put(name, value);
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public Map<String, String> getDataModel() {
        return this.data;
    }
}
