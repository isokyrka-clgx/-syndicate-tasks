package com.task10.response;

public class SigninResponse {
	private String accessToken;

	public SigninResponse() {
	}

	public SigninResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
