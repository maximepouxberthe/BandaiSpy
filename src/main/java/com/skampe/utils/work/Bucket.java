package com.skampe.utils.work;

public abstract class Bucket implements Runnable {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
