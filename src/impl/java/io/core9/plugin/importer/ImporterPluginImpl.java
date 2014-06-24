package io.core9.plugin.importer;

import io.core9.plugin.admin.AbstractAdminPlugin;
import io.core9.plugin.admin.plugins.AdminConfigRepository;
import io.core9.plugin.database.mongodb.MongoDatabase;
import io.core9.plugin.importer.processor.ImporterConfig;
import io.core9.plugin.importer.processor.Processor;
import io.core9.plugin.importer.processor.batch.BatchConfig;
import io.core9.plugin.importer.processor.batch.BatchProcessor;
import io.core9.plugin.importer.processor.csv.CSVConfig;
import io.core9.plugin.importer.processor.csv.CSVProcessor;
import io.core9.plugin.importer.processor.merge.MergeConfig;
import io.core9.plugin.importer.processor.merge.MergeProcessor;
import io.core9.plugin.importer.processor.reference.ReferenceConfig;
import io.core9.plugin.importer.processor.reference.ReferenceProcessor;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.scheduler.SchedulerPlugin;
import io.core9.scheduler.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

import org.dozer.DozerBeanMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@PluginImplementation
public class ImporterPluginImpl extends AbstractAdminPlugin implements ImporterPlugin {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@SuppressWarnings("rawtypes")
	private Map<String, Processor> processors = new HashMap<String, Processor>();
	
	@InjectPlugin
	private SchedulerPlugin scheduler;
	
	@InjectPlugin
	private AdminConfigRepository config;
	
	@InjectPlugin
	private MongoDatabase database;

	@Override
	public void registerProcessor(@SuppressWarnings("rawtypes") Processor processor) {
		processors.put(processor.getProcessorIdentifier(), processor);
	}

	@Override
	public String getControllerName() {
		return "importer";
	}

	@Override
	protected void process(Request req) {
		req.getResponse().end();
	}

	@Override
	protected void process(Request req, String type) {
		switch(type) {
		case "processor":
			try {
				req.getResponse().end(MAPPER.writeValueAsString(processors.values()));
			} catch (JsonProcessingException e) {
				req.getResponse().setStatusCode(500);
				req.getResponse().end(e.getMessage());
			}
			break;
		default:
			req.getResponse().end();
			break;
		}
	}

	@Override
	protected void process(Request req, String type, String id) {
		Map<String, Object> importer = config.readConfig(req.getVirtualHost(), id);
		switch(req.getMethod()) {
		case GET:
		case POST:
			req.getResponse().end(runImporter(type, req.getVirtualHost(), importer));
			break;
		default:
			break;
		}
	}

	@Override
	public void execute() {
		
	}

	@Override
	public MongoDatabase getRepository() {
		return database;
	}
	
	@Override
	public AdminConfigRepository getConfigRepository() {
		return config;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String runImporter(String type, VirtualHost vhost, Map<String,Object> importer) {
		@SuppressWarnings("rawtypes")
		Processor proc = processors.get(importer.get("processor"));
		ImporterConfig confpojo = null;
		if(proc.getConfigClass() != null) {
			// Map the configuration data to the configuration class
			final DozerBeanMapper mapper = new DozerBeanMapper();
			confpojo = (ImporterConfig) mapper.map(importer.get("processorOptions"), proc.getConfigClass());
		}
		return proc.psetImporterPlugin(this).process(type, vhost, confpojo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(VirtualHost[] vhosts) {
		Processor<CSVConfig> csv = new CSVProcessor();
		this.processors.put(csv.getProcessorIdentifier(), csv);
		Processor<MergeConfig> merge = new MergeProcessor();
		this.processors.put(merge.getProcessorIdentifier(), merge);
		Processor<BatchConfig> batch = new BatchProcessor();
		this.processors.put(batch.getProcessorIdentifier(), batch);
		Processor<ReferenceConfig> reference = new ReferenceProcessor();
		this.processors.put(reference.getProcessorIdentifier(), reference);
		
		for(VirtualHost vhost: vhosts) {
			List<Map<String,Object>> importers = config.getConfigList(vhost, "importer");
			for(Map<String,Object> importer : importers) {
				@SuppressWarnings("rawtypes")
				Processor proc = processors.get(importer.get("processor"));
				ImporterConfig confpojo = null;
				if(proc.getConfigClass() != null) {
					// Map the configuration data to the configuration class
					final DozerBeanMapper mapper = new DozerBeanMapper();
					confpojo = (ImporterConfig) mapper.map(importer.get("processorOptions"), proc.getConfigClass());
					if(confpojo.getInterval() > 0) {
						Task importTask = getTaskForImporter(this, (String) importer.get("_id"), vhost, proc, confpojo);
						scheduler.registerTask(importTask);
						scheduler.triggerTask(importTask.getName(), importTask.getGroup(), confpojo.getInterval());
					}
				}
			}
		}
	}

	private Task getTaskForImporter(final ImporterPlugin importerPlugin, final String id, final VirtualHost vhost, @SuppressWarnings("rawtypes") final Processor proc, final ImporterConfig config) {
		return new Task() {
			
			@Override
			public String getName() {
				return id;
			}
			
			@Override
			public String getGroup() {
				return "importer";
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void execute() {
				proc.psetImporterPlugin(importerPlugin).process("import", vhost, config);
			}
		};
	}
}
