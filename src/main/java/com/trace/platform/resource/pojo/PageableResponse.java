package com.trace.platform.resource.pojo;

import java.util.List;

public class PageableResponse<T>{
	
	private int totalPages;
	private long totalElements;
	private int size;
	private int page;
	private List<T> contents;
	
	public PageableResponse() {
		// TODO Auto-generated constructor stub
	}

	public PageableResponse(int totalPages, int totalElements, int size, int page, List<T> contents) {
		super();
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.size = size;
		this.page = page;
		this.contents = contents;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<T> getContents() {
		return contents;
	}

	public void setContents(List<T> contents) {
		this.contents = contents;
	}
	
	
}
