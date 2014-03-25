package io.core9.plugin.importer.processor;

import io.core9.plugin.importer.ImporterPlugin;
import io.core9.plugin.server.VirtualHost;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;


public interface Processor<T> {
	
	/**
	 * Return the config class
	 * @return
	 */
	Class<?> getConfigClass();
	
	/**
	 * Return the processor identifier (unique, system name)
	 * @return
	 */
	String getProcessorIdentifier();
	
	/**
	 * Return the processor name
	 * @return
	 */
	String getProcessorName();
	
	/**
	 * Get the Processor config (JSON Schema)
	 * @return
	 */
	JsonSchema getProcessorConfig();
	
	/**
	 * Get the processor JS template (path)
	 */
	String getTemplate();
	
	/**
	 * Set the importer plugin
	 * @param plugin
	 * @return
	 */
	Processor<T> psetImporterPlugin(ImporterPlugin plugin);
	
	/**
	 * Get the importer plugin
	 * @return
	 */
	ImporterPlugin pgetImporterPlugin();
	
	/**
	 * Process the request
	 */
	String process(String type, VirtualHost vhost, T configuration);

}
