package io.core9.plugin.importer.processor.merge;

import io.core9.plugin.importer.processor.ImporterConfig;

import java.util.ArrayList;

public class MergeConfig extends ImporterConfig {
	private ArrayList<MergeSpec> specs;

	/**
	 * @return the specs
	 */
	public ArrayList<MergeSpec> getSpecs() {
		return specs;
	}

	/**
	 * @param specs the specs to set
	 */
	public void setSpecs(ArrayList<MergeSpec> specs) {
		this.specs = specs;
	}

	
}
