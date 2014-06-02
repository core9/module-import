package io.core9.plugin.importer;

import io.core9.core.executor.Executor;
import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.admin.AdminPlugin;
import io.core9.plugin.admin.plugins.AdminConfigRepository;
import io.core9.plugin.database.mongodb.MongoDatabase;
import io.core9.plugin.importer.processor.ImporterConfig;
import io.core9.plugin.importer.processor.Processor;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.VirtualHostProcessor;

import java.util.Map;

public interface ImporterPlugin extends Core9Plugin, AdminPlugin, Executor, VirtualHostProcessor {
	
	final String TYPE_IMPORT = "import";
	
	/**
	 * Register a processor to the importer
	 * @param processor
	 */
	void registerProcessor(Processor<? extends ImporterConfig> processor);
	
	/**
	 * Return the data repository
	 * @return
	 */
	MongoDatabase getRepository();
	
	/**
	 * Return the data repository
	 * @return
	 */
	AdminConfigRepository getConfigRepository();
	
	/**
	 * Runs an importer
	 * @param importer
	 * @return 
	 */
	String runImporter(String type, VirtualHost vhost, Map<String,Object> importer);
}
