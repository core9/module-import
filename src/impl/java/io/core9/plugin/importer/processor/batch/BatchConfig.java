package io.core9.plugin.importer.processor.batch;

public class BatchConfig {
	private Importer[] importers;

	/**
	 * @return the importers
	 */
	public Importer[] getImporters() {
		return importers;
	}

	/**
	 * @param importers the importers to set
	 */
	public void setImporters(Importer[] importers) {
		this.importers = importers;
	}
	
}
