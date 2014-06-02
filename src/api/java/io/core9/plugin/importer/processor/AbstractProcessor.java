package io.core9.plugin.importer.processor;

import io.core9.plugin.importer.ImporterPlugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.types.SimpleTypeSchema;


public abstract class AbstractProcessor<T extends ImporterConfig> implements Processor<T> {
	
	private ImporterPlugin plugin;
	
	@Override
	public Processor<T> psetImporterPlugin(ImporterPlugin plugin) {
		this.plugin = plugin;
		return this;
	}
	
	@Override
	public ImporterPlugin pgetImporterPlugin() {
		return plugin;
	}
	
	@Override
	public String getTemplate() {
		return null;
	}

	@Override
	public JsonSchema getProcessorConfig() {
        try {
        	ObjectMapper mapper = new ObjectMapper();
            SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
			mapper.acceptJsonFormatVisitor(getConfigClass(), visitor);
			SimpleTypeSchema schema = visitor.finalSchema().asSimpleTypeSchema();
			schema.setTitle(getConfigClass().getSimpleName().toLowerCase());
			return schema;
		} catch (JsonProcessingException e) {
			return null;
		}
	}
}
