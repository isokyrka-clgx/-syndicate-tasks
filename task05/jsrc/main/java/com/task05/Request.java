package com.task05;

import java.util.Map;

public class Request {
	private int principalId;
	private Map<String, String> content;

	public Request(int principalId, Map<String, String> content) {
		this.principalId = principalId;
		this.content = content;
	}

	public Request() {
	}

	public int getPrincipalId() {
		return principalId;
	}

	public Map<String, String> getContent() {
		return content;
	}

	public void setPrincipalId(int principalId) {
		this.principalId = principalId;
	}

	public void setContent(Map<String, String> content) {
		this.content = content;
	}
}
