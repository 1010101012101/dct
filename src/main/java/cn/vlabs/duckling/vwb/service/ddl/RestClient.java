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

package cn.vlabs.duckling.vwb.service.ddl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * API的客户端代码
 * 
 * @date 2015-12-18
 * @author xiejj@cstnet.cn
 */
public class RestClient {

	private static final Charset DEFAULT_CHARSET = Consts.UTF_8;
	/**
	 * 
	 */
	private static final ContentType DEFAULT_CONTENT_TYPE = ContentType.create("text/plain", DEFAULT_CHARSET);
	private static Logger LOG = Logger.getLogger(RestClient.class);
	private CloseableHttpClient httpclient = HttpClients.custom().disableCookieManagement().build();

	private HttpEntity buildMultiPartForm(String dataFieldName, String filename,
			InputStream stream, String... params) {
		MultipartEntityBuilder builder = MultipartEntityBuilder
				.create()
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
				.addBinaryBody(dataFieldName, stream,
						ContentType.DEFAULT_BINARY, filename);
		
		if (params!=null){
			for (int i=0;i<params.length/2;i++){
				StringBody contentBody = new StringBody(params[i*2+1], DEFAULT_CONTENT_TYPE);
				builder.addPart(params[i*2], contentBody);
			}
		}
		
		HttpEntity reqEntity = builder.setCharset(DEFAULT_CHARSET).build();
		return reqEntity;
	}

	private HttpUriRequest buildUriRequest(String url, String token,
			String... params) throws URISyntaxException {
		RequestBuilder rb = RequestBuilder.get().setUri(new URI(url));
		if (params != null && params.length % 2 == 0) {
			for (int i = 0; i < params.length / 2; i++) {
				rb.addParameter(params[i * 2], params[i * 2 + 1]);
			}
		}

		rb.addHeader("accept", "application/json");
		rb.addHeader("Authorization", "Bearer " + token);

		HttpUriRequest uriRequest = rb.build();
		return uriRequest;
	}

	public JSONObject httpGet(String url, String token, String... params)
			throws IOException {
		try {
			HttpUriRequest uriRequest = buildUriRequest(url, token, params);
			CloseableHttpResponse response = httpclient.execute(uriRequest);
			return readResult(response);
		} catch (ParseException | URISyntaxException e) {
			log("HttpGet", url, params, e);
		} catch (RuntimeException e){
			log("HttpUpload", url, params, e);
			throw e;
		}
		return null;
	}

	public JSONObject httpPost(String url, String token, String... params)
			throws IOException {
		JSONObject result = null;
		try {
			HttpPost httppost = new HttpPost(url);

			List<NameValuePair> list = NameValuePairUtil.assemble(params);
			httppost.setEntity(new UrlEncodedFormEntity(list, DEFAULT_CHARSET));
			httppost.setHeader("accept", "application/json");
			httppost.setHeader("Authorization", "Bearer " + token);
			
			CloseableHttpResponse response = httpclient.execute(httppost);
			result = readResult(response);
		} catch (ParseException e) {
			log("HttpPost", url, params, e);
		} catch (RuntimeException e){
			log("HttpUpload", url, params, e);
			throw e;
		}
		return result;
	}
	
	
	public JSONObject httpPost(String url, String token, Map<String,String[]> params)
			throws IOException {
		JSONObject result = null;
		try {
			HttpPost httppost = new HttpPost(url);
			List<NameValuePair> list=convert2NameValuePair(params);
			httppost.setEntity(new UrlEncodedFormEntity(list, DEFAULT_CHARSET));
			httppost.setHeader("accept", "application/json");
			httppost.setHeader("Authorization", "Bearer " + token);
			
			CloseableHttpResponse response = httpclient.execute(httppost);
			result = readResult(response);
		} catch (ParseException e) {
			log("HttpPost", url, params, e);
		} catch (RuntimeException e){
			log("HttpUpload", url, params, e);
			throw e;
		}
		return result;
	}
	
	private List<NameValuePair> convert2NameValuePair(Map<String,String[]> params){
		List<NameValuePair> result=new ArrayList<NameValuePair>();
		if(params==null||params.size()<1){
			return result;
		}
		
		Set<String> keySet=params.keySet();
		
		for(String key:keySet){
			if(params.get(key)==null||params.get(key).length<1){
				continue;
			}
			
			for(String value:params.get(key)){
				result.add(new BasicNameValuePair(key,value));
			}
		}
		
		return result;
	}

	public JSONObject httpUpload(String url, String dataFieldName,
			String filename, InputStream stream, String token, String... params) {
		try {
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Authorization", "Bearer " + token);
			HttpEntity reqEntity = buildMultiPartForm(dataFieldName, filename, stream, params);
			httppost.setEntity(reqEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			return readResult(response);
		} catch (IOException|ParseException e) {
			log("HttpUpload", url, params, e);
		} catch (RuntimeException e){
			log("HttpUpload", url, params, e);
			throw e;
		}
		return null;
	}

	private boolean isValidStatus(CloseableHttpResponse response) {
		int statusCode = response.getStatusLine().getStatusCode();
		return statusCode == 200 || statusCode == 201||statusCode==204;
	}
	private void log(String method, String url, String[] params, Exception e) {
		LOG.error("Doing " + method + " Operation for url " + url + " failed ",
				e);
		if (params != null) {
			for (int i = 0; i < params.length / 2; i++) {
				LOG.debug(params[i * 2] + "=" + params[i * 2 + 1]);
			}
		}
	}
	
	private void log(String method, String url,Map<String,String[]> params, Exception e) {
		LOG.error("Doing " + method + " Operation for url " + url + " failed ",
				e);
		if (params != null) {
			
			Set<String> keySet=params.keySet();
			
			for (String key:keySet) {
				LOG.debug(key+ "=" + params.get(key));
			}
		}
	}

	private JSONObject readJSON(CloseableHttpResponse response)
			throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));
		JSONParser jp = new JSONParser();
		try {
			return (JSONObject) jp.parse(br);
		} finally {
			br.close();
		}
	}

	private JSONObject readResult(CloseableHttpResponse response)
			throws IOException, ParseException {
		try {
			if (!isValidStatus(response)) {
				JSONObject object=null;
				try{
					
					object = readJSON(response);
				}catch(Throwable e){
					throw new APIException(response.getStatusLine().getStatusCode(),  response.getStatusLine().getReasonPhrase());
				}
				throw new APIException((long)object.get("errno"),(String)object.get("msg"));
				
			}
			
			if(response.getStatusLine().getStatusCode()==204){
				return null;
			}
			
			return readJSON(response);
		} finally {
			response.close();
		}
	}
}