package de.thomaskrille.dropwizard_template_config;

import java.util.HashMap;
import java.util.Map;

public class TestCustomHashesProvider implements TemplateConfigHashesProvider {
    private final String namespace;
    private final Map<String, Object> data = new HashMap<>();

    public TestCustomHashesProvider(String namespace) {
        this.namespace = namespace;
    }

    public void put(String name, Object value) {
        data.put(name, value);
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public Map<String, Object> getHashes() {
        return this.data;
    }
}
