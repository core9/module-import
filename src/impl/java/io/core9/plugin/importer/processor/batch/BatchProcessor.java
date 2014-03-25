package io.core9.plugin.importer.processor.batch;

import io.core9.plugin.importer.ImporterPlugin;
import io.core9.plugin.importer.processor.AbstractProcessor;
import io.core9.plugin.importer.processor.Processor;
import io.core9.plugin.server.VirtualHost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class BatchProcessor extends AbstractProcessor<BatchConfig> implements Processor<BatchConfig> {
	
	@InjectPlugin
	private ImporterPlugin importer; 
	
	@Override
	public String getProcessorIdentifier() {
		return "core_batch";
	}

	@Override
	public String getProcessorName() {
		return "Batch Processor";
	}

	@Override
	public String process(String type, VirtualHost vhost, BatchConfig config) {
		Map<String, Map<String,Object>> importers = parseImporters(this.pgetImporterPlugin().getConfigRepository().getConfigList(vhost, "importer"));
		for(Importer importer: config.getImporters()) {
			System.out.println("Running: " + importer.getName());
			Map<String,Object> current = importers.get(importer.getName());
			this.pgetImporterPlugin().runImporter(type, vhost, current);
			System.out.println("Done: " + importer.getName());
		}
		return "ok";
	}
	
	/**
	 * Parse the importers to a name indexed map
	 * @param importers
	 * @return
	 */
	private Map<String, Map<String,Object>> parseImporters(List<Map<String,Object>> importers) {
		Map<String, Map<String,Object>> result = new HashMap<String, Map<String,Object>>();
		for(Map<String,Object> importer: importers) {
			result.put((String) importer.get("name"), importer);
		}
		return result;
	}

	@Override
	public Class<?> getConfigClass() {
		return BatchConfig.class;
	}

}
