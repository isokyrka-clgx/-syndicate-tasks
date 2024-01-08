package com.task02;

public class Response {
	private final int statusCode;
	private final String message;

	public Response(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}
}
