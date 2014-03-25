package io.core9.plugin.importer.processor.merge;

import io.core9.plugin.database.mongodb.MongoDatabase;
import io.core9.plugin.importer.processor.AbstractProcessor;
import io.core9.plugin.importer.processor.Processor;
import io.core9.plugin.server.VirtualHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeProcessor extends AbstractProcessor<MergeConfig> implements Processor<MergeConfig> {
	
	@Override
	public String getProcessorIdentifier() {
		return "core_merge";
	}

	@Override
	public String getProcessorName() {
		return "Merge Processor";
	}

	@Override
	public String process(String type, VirtualHost vhost, MergeConfig config) {
		MongoDatabase database = this.pgetImporterPlugin().getRepository();
		for(MergeSpec spec : config.getSpecs()) {
			Map<String,Map<String,Object>> children = parseChildren(spec.getChild().getKey(), database.getMultipleResults((String) vhost.getContext("database"), vhost.getContext("prefix") + spec.getChild().getCollection(), new HashMap<String,Object>()));
			List<Map<String,Object>> parents = database.getMultipleResults((String) vhost.getContext("database"), vhost.getContext("prefix") + spec.getParent().getCollection(), new HashMap<String,Object>());
			if(spec.getDestination().getKey() != null) {
				for(Map<String,Object> parent : parents) {
					String ids = (String) parent.get(spec.getParent().getKey());
					List<Map<String,Object>> fullChildren = new ArrayList<Map<String,Object>>();
					for(String id : ids.split(",")) {
						fullChildren.add(children.get(id));
					}
					parent.put(spec.getParent().getKey(), fullChildren);
					Map<String,Object> newDoc = new HashMap<String,Object>();
					newDoc.put("$set", parent);
					Map<String,Object> query = new HashMap<String,Object>();
					query.put(spec.getDestination().getKey(), parent.get(spec.getDestination().getKey()));
					database.upsert((String) vhost.getContext("database"), vhost.getContext("prefix") + spec.getDestination().getCollection(), newDoc, query);
				}
			} else {
				database.delete((String) vhost.getContext("database"), vhost.getContext("prefix") + spec.getDestination().getCollection(), new HashMap<String,Object>());
				for(Map<String,Object> parent : parents) {
					String ids = (String) parent.get(spec.getParent().getKey());
					List<Map<String,Object>> fullChildren = new ArrayList<Map<String,Object>>();
					for(String id : ids.split(",")) {
						fullChildren.add(children.get(id));
					}
					parent.put(spec.getParent().getKey(), fullChildren);
					database.upsert((String) vhost.getContext("database"), vhost.getContext("prefix") + spec.getDestination().getCollection(), parent, parent);
				}
			}
		}
		return "ok";
	}

	@Override
	public Class<?> getConfigClass() {
		return MergeConfig.class;
	}

	/**
	 * Parses the given children hashmap to an indexed hashmap (by child key)
	 * @param key
	 * @param children
	 * @return
	 */
	private Map<String,Map<String,Object>> parseChildren(String key, List<Map<String,Object>> children) {
		Map<String,Map<String,Object>> result = new HashMap<String,Map<String,Object>>();
		for(Map<String,Object> child : children) {
			result.put((String) child.get(key), child);
		}
		return result;
	}
}
