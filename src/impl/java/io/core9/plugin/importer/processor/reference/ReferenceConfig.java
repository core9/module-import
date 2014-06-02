package io.core9.plugin.importer.processor.reference;

import io.core9.plugin.importer.processor.ImporterConfig;

public class ReferenceConfig extends ImporterConfig {
	
	private String sourceCollection;
	private String sourceField;
	private boolean multipleValues;
	
	private String referenceCollection;
	private String referenceField;
	
	private String targetCollection;

	public String getSourceCollection() {
		return sourceCollection;
	}

	public void setSourceCollection(String sourceCollection) {
		this.sourceCollection = sourceCollection;
	}

	public String getSourceField() {
		return sourceField;
	}

	public void setSourceField(String sourceField) {
		this.sourceField = sourceField;
	}

	public boolean isMultipleValues() {
		return multipleValues;
	}

	public void setMultipleValues(boolean multipleValues) {
		this.multipleValues = multipleValues;
	}

	public String getReferenceCollection() {
		return referenceCollection;
	}

	public void setReferenceCollection(String referenceCollection) {
		this.referenceCollection = referenceCollection;
	}

	public String getReferenceField() {
		return referenceField;
	}

	public void setReferenceField(String referenceField) {
		this.referenceField = referenceField;
	}

	public String getTargetCollection() {
		return targetCollection;
	}

	public void setTargetCollection(String targetCollection) {
		this.targetCollection = targetCollection;
	}

}
