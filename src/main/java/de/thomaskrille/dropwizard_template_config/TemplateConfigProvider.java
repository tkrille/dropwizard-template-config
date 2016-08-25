package de.thomaskrille.dropwizard_template_config;

import java.util.Map;

public interface TemplateConfigProvider {

    /**
     * @return The namespace to use when accessing objects in the config template.  Objects can either be accessed
     * in the template with ${namespace.object}
     */
    public String getNamespace();

    /**
     * @return A data model that the freemarker engine will use when parsing the config template.
     */
    public Map<String, String> getDataModel();

}
