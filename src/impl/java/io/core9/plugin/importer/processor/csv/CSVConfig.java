package io.core9.plugin.importer.processor.csv;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class CSVConfig {
	private String url;
	private String separator;
	private String collection;
	private String contenttype;
	private String primaryKey;
	private FieldConfig[] fields;
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * @return the collection
	 */
	public String getCollection() {
		return collection;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}

	/**
	 * @return the contenttype
	 */
	public String getContenttype() {
		return contenttype;
	}

	/**
	 * @param contenttype the contenttype to set
	 */
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	/**
	 * @return the primaryKey
	 */
	public String getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @return the fields
	 */
	public FieldConfig[] getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(FieldConfig[] fields) {
		this.fields = fields;
	}
	
	public CellProcessor[] retrieveCellProcessors() {
		int length = this.fields.length;
		CellProcessor[] processors = new CellProcessor[length];
		for(int i = 0; i < length; i++) {
			processors[i] = createCellProcessor(this.fields[i]);
		}
		return processors;
	}
	
	private CellProcessor createCellProcessor(FieldConfig config) {
		CellProcessor processor = null;
		if(config.getParseInt()) {
			processor = new ParseInt();
		}
		if(config.getNotNull()) {
			processor = new NotNull(processor);
		}
		return processor;
	}

}
