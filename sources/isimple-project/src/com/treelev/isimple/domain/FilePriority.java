package com.treelev.isimple.domain;

public class FilePriority {

	private String fileName;
	private String fileSecondName;

	public String getFileSecondName() {
		return fileSecondName;
	}

	public void setFileSecondName(String fileSecondName) {
		this.fileSecondName = fileSecondName;
	}

	private Integer priority;

	public FilePriority(String fileName, String fileSecondName, Integer priority) {
		this.fileName = fileName;
		this.priority = priority;
		this.fileSecondName = fileSecondName;
	}

	public String getFileName() {
		return fileName;
	}

	public Integer getPriority() {
		return priority;
	}
}
