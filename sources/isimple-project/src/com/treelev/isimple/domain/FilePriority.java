package com.treelev.isimple.domain;

public class FilePriority {

	private String fileName;

	private Integer priority;

	public FilePriority(String fileName, Integer priority) {
		this.fileName = fileName;
		this.priority = priority;
	}

	public String getFileName() {
		return fileName;
	}

	public Integer getPriority() {
		return priority;
	}
}
