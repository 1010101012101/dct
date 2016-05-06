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

package cn.vlabs.duckling.vwb.service.auth.policy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Introduction Here.
 * @date Feb 3, 2010
 * @author zzb
 */
public class AuthorizationTokenStream {
	
	private BufferedReader reader;
	private StringTokenizer tokens;
	private int lineNum;

	public AuthorizationTokenStream(BufferedReader br) {
		this.reader = br;
		lineNum = 0;
	}
	
	public int getLineNum() {
		return lineNum;
	}
	
	public boolean hasNextToken() throws IOException {
		if(tokens == null || !tokens.hasMoreTokens()) {
			addTokens();
		}
		if(tokens == null || !tokens.hasMoreTokens()) {
			return false;
		} else {
			return true;
		}
	}
	
	public String nextToken() throws IOException {
		if(tokens == null || !tokens.hasMoreTokens()) {
			addTokens();
		}
		if(tokens == null || !tokens.hasMoreTokens()) {
			return null;
		}
		return tokens.nextToken();
	}
	
	public String nextUsefulToken() throws IOException {
		String token = nextToken();
		if(isNoUseToken(token)) {
			return nextUsefulToken().trim();
		}
		return token;
	}
	public void close() throws IOException{
		reader.close();
	}
	private static boolean isNoUseToken(String token) {
		if(token.trim().equals("")) {
			return true;
		}
		return false;
	}
	
	private void addTokens() throws IOException {
		String line = reader.readLine();
		lineNum ++;
		while(line != null) {
			if(line.trim().equals("") || line.startsWith("#")) {
				line = reader.readLine();
				lineNum ++;
			} else {
				tokens = new StringTokenizer(line);
				return;
			}
		}
	}

}
