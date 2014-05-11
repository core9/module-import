package io.core9.plugin.importer.processor.reference;

import io.core9.plugin.database.mongodb.MongoDatabase;
import io.core9.plugin.importer.processor.AbstractProcessor;
import io.core9.plugin.importer.processor.Processor;
import io.core9.plugin.server.VirtualHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReferenceProcessor extends AbstractProcessor<ReferenceConfig> implements Processor<ReferenceConfig> {

	@Override
	public Class<?> getConfigClass() {
		return ReferenceConfig.class;
	}

	@Override
	public String getProcessorIdentifier() {
		return "reference";
	}

	@Override
	public String getProcessorName() {
		return "Reference";
	}

	@Override
	public String process(String type, VirtualHost vhost, ReferenceConfig configuration) {
		MongoDatabase db = this.pgetImporterPlugin().getRepository();
		List<Map<String,Object>> source = db.getMultipleResults((String) vhost.getContext("database"), vhost.getContext("prefix") + configuration.getSourceCollection(), new HashMap<String, Object>());
		for(Map<String,Object> item : source) {
			String value = (String) item.get(configuration.getSourceField());
			if(configuration.isMultipleValues()) {
				List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
				for(String refId : value.split(",")) {
					result.add(retrieveReference(vhost, db, configuration, refId));
				}
				item.put(configuration.getSourceField(), result);
			} else {
				item.put(configuration.getSourceField(), retrieveReference(vhost, db, configuration, value));
			}
			Map<String,Object> query = new HashMap<String, Object>();
			query.put("_id", item.get("_id"));
			db.upsert((String) vhost.getContext("database"), vhost.getContext("prefix") + configuration.getTargetCollection(), item, query);
		}
		return "ok";
	}

	private Map<String,Object> retrieveReference(VirtualHost vhost, MongoDatabase db, ReferenceConfig configuration, String refId) {
		Map<String,Object> query = new HashMap<String, Object>();
		query.put(configuration.getReferenceField(), refId);
		Map<String,Object> ref = db.getSingleResult((String) vhost.getContext("database"), vhost.getContext("prefix") + configuration.getReferenceCollection(), query);
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("key", refId);
		result.put("value", ref.get("_id"));
		return result;
	}
	
	
}
