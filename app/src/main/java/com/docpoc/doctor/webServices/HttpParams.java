package com.docpoc.doctor.webServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class HttpParams {
	private List<NameValuePair> params = new ArrayList<NameValuePair>();

	public List<NameValuePair> getParams() {
		return params;
	}

	public void addParam(String field, String value) {
		params.add(new BasicNameValuePair(field, value));
	}
}
