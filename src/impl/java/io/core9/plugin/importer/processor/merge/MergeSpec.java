package io.core9.plugin.importer.processor.merge;

public class MergeSpec {
	private String name;
	private SpecSource parent;
	private SpecSource child;
	private SpecSource destination;
	
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
	 * @return the parent
	 */
	public SpecSource getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SpecSource parent) {
		this.parent = parent;
	}

	/**
	 * @return the child
	 */
	public SpecSource getChild() {
		return child;
	}

	/**
	 * @param child the child to set
	 */
	public void setChild(SpecSource child) {
		this.child = child;
	}

	/**
	 * @return the destination
	 */
	public SpecSource getDestination() {
		return destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(SpecSource destination) {
		this.destination = destination;
	}
}
