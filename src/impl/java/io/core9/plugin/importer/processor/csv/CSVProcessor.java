package io.core9.plugin.importer.processor.csv;

import io.core9.plugin.database.mongodb.MongoDatabase;
import io.core9.plugin.importer.processor.AbstractProcessor;
import io.core9.plugin.importer.processor.Processor;
import io.core9.plugin.server.VirtualHost;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

public class CSVProcessor extends AbstractProcessor<CSVConfig> implements Processor<CSVConfig> {
	
	@Override
	public String getProcessorIdentifier() {
		return "core_csv";
	}

	@Override
	public String getProcessorName() {
		return "CSV Processor";
	}
	
	@Override
	public String getTemplate() {
		return "importer/csv/csv.tpl.html";
	}

	@SuppressWarnings({ "unchecked", "resource" })
	@Override
	public String process(String type, VirtualHost vhost, CSVConfig config) {
		MongoDatabase database = this.pgetImporterPlugin().getRepository();
		ICsvMapReader reader = null;
		try {
			URL url = new URL(config.getUrl());
			CsvPreference pref;
			switch(config.getSeparator()) {
			case ";":
				pref = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;
				break;
			case "\t":
			case "tab":
				pref = CsvPreference.TAB_PREFERENCE;
				break;
			case "excel":
				pref = CsvPreference.EXCEL_PREFERENCE;
				break;
			case "":
			case ",":
			default:
				pref = CsvPreference.STANDARD_PREFERENCE;
				break;
			}
			reader = new CsvMapReader(new InputStreamReader(url.openStream()), pref);
			final String[] header = reader.getHeader(true);
			if(type.equals("head")) {
				String result = "[";
				int n = header.length;
				for(String head : header) {
					result += "\"" + head + "\"";
					if(--n > 0) {
						result += ",";
					}
					else {
						result += "]";
					}
				}
				return result;
			} else {
				Map<String, ? extends Object> line;
				final CellProcessor[] processors = config.retrieveCellProcessors();
				if(config.getPrimaryKey() == null || config.getPrimaryKey().equals("")) {
					database.delete((String) vhost.getContext("database"), (String) vhost.getContext("prefix") + config.getCollection(), new HashMap<String, Object>());
				}
				while((line = reader.read(header, processors)) != null) {
					if(config.getContenttype() != null && !config.getContenttype().equals("")) {
						((Map<String,String>)line).put("contenttype", config.getContenttype());
					}
					if(config.getPrimaryKey() != null && !config.getPrimaryKey().equals("")) {
						// Primary key set, do partial update (this can be fixed by $setOnInsert on MongoDB 2.5.5+
						Map<String,Object> query = new HashMap<String,Object>();
						query.put(config.getPrimaryKey(), line.get(config.getPrimaryKey()));
						Map<String,Object> result = database.getSingleResult(
								(String) vhost.getContext("database"), 
								(String) vhost.getContext("prefix") + config.getCollection(),
								query);
						query.clear();
						if(result != null && result.get("_id") != null) {
							query.put("_id", result.get("_id"));
						}
						Map<String,Object> newDoc = new HashMap<String,Object>();
						newDoc.put("$set", line);
						database.upsert(
								(String) vhost.getContext("database"), 
								(String) vhost.getContext("prefix") + config.getCollection(), 
								newDoc, 
								query);
					} else {
						// No primary key, do full document update
						database.upsert(
							(String) vhost.getContext("database"), 
							(String) vhost.getContext("prefix") + config.getCollection(), 
							(Map<String, Object>) line, 
							(Map<String, Object>) line);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "ok";
	}

	@Override
	public Class<?> getConfigClass() {
		return CSVConfig.class;
	}

}
