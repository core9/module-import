package io.core9.plugin.importer.processor.csv;

public class FieldConfig {
	private String name;
	private boolean notNull;
	private boolean parseInt;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isOptional
	 */
	public boolean getNotNull() {
		return notNull;
	}

	/**
	 * @param isOptional the isOptional to set
	 */
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	/**
	 * @return the parseInt
	 */
	public boolean getParseInt() {
		return parseInt;
	}

	/**
	 * @param parseInt the parseInt to set
	 */
	public void setParseInt(boolean parseInt) {
		this.parseInt = parseInt;
	}
}
