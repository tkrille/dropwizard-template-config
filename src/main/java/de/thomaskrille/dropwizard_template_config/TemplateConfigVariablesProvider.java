package de.thomaskrille.dropwizard_template_config;

import java.util.Map;

public interface TemplateConfigVariablesProvider {

    /**
     * @return The namespace to use when accessing objects in the config template. Objects can either be accessed
     * in the template with ${namespace.object} or without the namespace with ${object}. If you don't specify a
     * namespace there may be collisions if multiple objects have the same name.  In this case the order of
     * precedence is: environment variables, system variables, the variables from custom providers based on the
     * order you add the providers with
     * {@link TemplateConfigBundleConfiguration#addCustomProvider(TemplateConfigVariablesProvider)}.
     */
    String getNamespace();

    /**
     * @return A data model that the freemarker engine will use when parsing the config template.
     */
    Map<String, String> getVariables();

}
