/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */
package cn.vlabs.duckling.common.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class WebSite {
	public WebSite(String baseurl) {
		client = new HttpClient(new MultiThreadedHttpConnectionManager());
		baseURL = baseurl;
	}

	public static String getBodyContent(String url) {
		int index = url.indexOf("?");
		String query = "";
		if (index > 0) {
			query = url.substring(index+1, url.length());
			url = url.substring(0, index);
		}
		WebSite site = new WebSite(url);
		if (query.trim().length() > 0) {
			PostMethod method = site.createPostMethod(null);
			Map<String, String> params = extractParams(query.trim());
			for (String key : params.keySet()) {
				method.addParameter(key, params.get(key));
			}
			try {
				int code = site.exec(method);
				if (code == 200) {
					return method.getResponseBodyAsString();
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				method.releaseConnection();
				site.close();
			}
		}else
		{
			GetMethod method = site.createGetMethod("");
			try {
				int code = site.exec(method);
				if (code == 200) {
					return method.getResponseBodyAsString();
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				method.releaseConnection();
				site.close();
			}
		}
		return null;
	}

	private static Map<String, String> extractParams(String query) {

		Map<String, String> params = new HashMap<String,String>();
		Pattern p =  Pattern.compile("([^&]+)=([^&]+)");
		Matcher m = p.matcher(query);
	    while(m.find())
	    {
	    	String key = m.group(1).trim();
	    	if(key.length()>0)
	    	{
	    		params.put(key, m.group(2));
	    	}
	    	
	    }
		return params;
	}
//    public static void main(String[]ars)
//    {
//    	System.out.println(getBodyContent("http://localhost/umt/getUMTPublicKey"));
//    }
	public GetMethod createGetMethod(String url) {
		if (url == null)
			return new EncodableGetMethod(baseURL, encode);
		else
			return new EncodableGetMethod(baseURL + url, encode);
	}

	public PostMethod createPostMethod(String url) {
		if (url == null)
			return new EncodablePostMethod(baseURL, encode);
		else
			return new EncodablePostMethod(baseURL + url, encode);
	}

	public int exec(HttpMethod method) throws HttpException, IOException {
		return client.executeMethod(method);
	}

	public void close() {
		client.getHttpConnectionManager().closeIdleConnections(0);
	}

	public void closeIdle() {
		client.getHttpConnectionManager().closeIdleConnections(idleTimeout);
	}

	public void setTimeout(long msTimeout) {
		this.idleTimeout = msTimeout;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	private String encode = "UTF-8";
	private String baseURL;
	// 毫秒
	private long idleTimeout = 5000;
	private HttpClient client;
}
