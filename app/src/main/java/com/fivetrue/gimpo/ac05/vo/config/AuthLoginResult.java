package com.fivetrue.gimpo.ac05.vo.config;

public class AuthLoginResult {
	private String code = null;
	private String state = null;
	private String error = null;
	private String error_description = null;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getError_description() {
		return error_description;
	}
	public void setError_description(String error_description) {
		this.error_description = error_description;
	}
	
	@Override
	public String toString() {
		return "LoginResult [code=" + code + ", state=" + state + ", error=" + error + ", error_description="
				+ error_description + "]";
	}
}
