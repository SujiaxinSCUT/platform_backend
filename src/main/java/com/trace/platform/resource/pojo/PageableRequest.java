package com.trace.platform.resource.pojo;

public class PageableRequest {

	private Integer size;
	private Integer page;
	
	public PageableRequest() {
		// TODO Auto-generated constructor stub
	}

	public PageableRequest(Integer size, Integer page) {
		super();
		this.size = size;
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
	
	
}
