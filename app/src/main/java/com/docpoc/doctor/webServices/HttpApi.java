package com.docpoc.doctor.webServices;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HttpApi {
	private static final String LOG = "adviser.HttpApi";
	private static CookieStore cookieStore = null;

	public static HttpResponse makeRequest(String uri, String json) {
	    try {
	        HttpPost httpPost = new HttpPost(uri);
	    //    httpPost.setEntity(new StringEntity(json));
	     //   httpPost.setHeader("Accept", "application/json");
	    //    httpPost.setHeader("Content-type", "application/json");


	        
	        DefaultHttpClient httpClient = defaultHttpClient();
	        return httpClient.execute(httpPost);
	        //return new DefaultHttpClient().execute(httpPost);
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public static DefaultHttpClient defaultHttpClient() {
		BasicHttpParams basicHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(basicHttpParams, 20000);
		HttpConnectionParams.setSoTimeout(basicHttpParams, 20000);
		HttpConnectionParams.setSocketBufferSize(basicHttpParams, 8192);
		HttpClientParams.setRedirecting(basicHttpParams, true);
		return new DefaultHttpClient(basicHttpParams);
	}
	
	public static String sendRequest(String serverUrl, String Json) {
		HttpResponse httpresponse = null;
		int status = -1;

		try {
			/** Gets the HTTP response response */
			httpresponse = makeRequest(MyConstants.BASE_URL, Json);
		    status = httpresponse.getStatusLine().getStatusCode();	    
		    String response = null;
			/** If the status code 200 response successfully */
		    
		    if (status == 200) {
				/** Remove the response string */
				response = EntityUtils.toString(
					httpresponse.getEntity(), HTTP.UTF_8);


				return response.trim();
			}

		} catch (Exception e) {
			Log.v(LOG, "send request error");
			status = -1;
		}

		return null;
	}

	
	public static void clearCookie() {
		if (cookieStore != null)
			cookieStore.clear();
		cookieStore = null;
	}



}
