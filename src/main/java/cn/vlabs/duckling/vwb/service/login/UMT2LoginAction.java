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

package cn.vlabs.duckling.vwb.service.login;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import cn.vlabs.commons.principal.GroupPrincipal;
import cn.vlabs.commons.principal.UserPrincipal;
import cn.vlabs.duckling.api.umt.sso.ILoginHandle;
import cn.vlabs.duckling.common.crypto.KeyFile;
import cn.vlabs.duckling.common.crypto.impl.RSAKey;
import cn.vlabs.duckling.common.http.WebSite;
import cn.vlabs.duckling.common.transmission.PublicKeyEnvelope;
import cn.vlabs.duckling.common.transmission.SignedEnvelope;
import cn.vlabs.duckling.common.transmission.UserCredentialEnvelope;
import cn.vlabs.duckling.common.util.Base64Util;
import cn.vlabs.duckling.vwb.Attributes;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.config.ISiteConfig;
import cn.vlabs.duckling.vwb.service.user.IUserService;

public class UMT2LoginAction extends LoginAction {

	private static RSAKey umtKey = null;

	public UMT2LoginAction(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	private void addUserToSiteByCheckFlag(
			Collection<Principal> currentPrincipals, String userName,
			String displayName) {
		if (currentPrincipals != null) {
			boolean existInSite = false;
			for (Principal p : currentPrincipals) {
				if (p instanceof GroupPrincipal) {
					existInSite = true;
					break;
				}
			}
			if (!existInSite) {
				String checkValue = getVwbcontext().getProperty(Attributes.CHECK_SITE_FOR_USER);
				IUserService userS = VWBContainerImpl.findContainer()
						.getUserService();
				if (checkValue != null
						&& Attributes.CHECK_SITE_FOR_USER_ADD
								.equals(checkValue)) {
					userS.addUserToGroup(getVwbcontext().getVO(), "root", userName);
					currentPrincipals.clear();
					currentPrincipals.addAll(userS.getUserPrincipal(userName,
							getVwbcontext().getVO()));

				} else if (Attributes.CHECK_SITE_FOR_USER_APPLY
						.equals(checkValue)) {
					userS.applyUserToVo(getVwbcontext().getVO(), userName, displayName,
							null);
					currentPrincipals.clear();
					currentPrincipals.addAll(userS.getUserPrincipal(userName,
							getVwbcontext().getVO()));
				}
			}

		}

	}

	private void downloadUMTKey(HttpServletRequest request) {
		ISiteConfig siteconfig = VWBContext.getContainer().getSiteConfig();
		String umtPublicKeyURl = siteconfig.getProperty(getVwbcontext().getSite().getId(),"duckling.umt.publicKey",
						"http://locahost/umt/getUMTPublicKey");
		String umtPublicKeyContent = WebSite.getBodyContent(umtPublicKeyURl);
		String umtKeyFile = request.getSession().getServletContext()
				.getRealPath("WEB-INF")
				+ File.separator + "conf" + File.separator + "umtpublickey.txt";
		try {
			FileUtils.writeStringToFile(new File(umtKeyFile),
					umtPublicKeyContent);
		} catch (IOException e) {
			log.error("failed:write umtpublickey to file(" + umtKeyFile + ")",
					e);
		}
	}

	private void loadUMTKeyFromLocal(HttpServletRequest request) {
		String umtKeyFile = request.getSession().getServletContext()
				.getRealPath("WEB-INF")
				+ File.separator + "conf" + File.separator + "umtpublickey.txt";
		if (new File(umtKeyFile).exists()) {
			KeyFile keyFile = new KeyFile();
			try {
				umtKey = keyFile
						.loadFromPublicKeyContent(PublicKeyEnvelope
								.valueOf(
										FileUtils.readFileToString(new File(
												umtKeyFile))).getPublicKey());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("");
			}
		}
	}

	private void replaceUserPrincipal(List<Principal> principals,
			UserPrincipal userp) {
		if (principals != null) {
			for (int i = 0; i < principals.size(); i++) {
				if (principals.get(i) instanceof cn.vlabs.commons.principal.UserPrincipal) {
					UserPrincipal up = (UserPrincipal) principals.get(i);
					UserPrincipal newup = new UserPrincipal(userp.getName(),
							up.getDisplayName(), up.getEmail(),
							userp.getAuthBy());
					principals.set(i, newup);
					break;
				}
			}
		}
	}

	/**
	 * 由request对象中读取用户凭证（xml格式，通过post方法由门户站点传递过来）
	 * 
	 * @throws UnkownCredentialException
	 * 
	 */
	@Override
	protected Collection<Principal> parseCredential()
			throws UnkownCredentialException {
		String signedCredential = request.getParameter("signedCredential");
		if (umtKey == null) {
			downloadUMTKey(request);
			loadUMTKeyFromLocal(request);
		}
		if (umtKey != null) {
			if (StringUtils.isNotEmpty(signedCredential)) {
				try {
					signedCredential = Base64Util
							.decodeBase64(signedCredential);
					SignedEnvelope signedData = SignedEnvelope
							.valueOf(signedCredential);
					if (signedData.verify(umtKey)) {
						UserPrincipal user = UserCredentialEnvelope.valueOf(
								signedData.getContent()).getUser();
						String name = user.getName();
						String voName = getVwbcontext().getProperty(
								"duckling.umt.vo");
						List<Principal> principals = VWBContainerImpl
								.findContainer().getUserService()
								.getUserPrincipal(name, voName);
						addUserToSiteByCheckFlag(principals, name,
								user.getDisplayName());
						if (principals != null && principals.size() > 0) {
							replaceUserPrincipal(principals, user);
							return principals;
						} else {
							LinkedList<Principal> result = new LinkedList<Principal>();
							result.add(user);
							return result;
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
					throw new UnkownCredentialException(
							"Error:signedCredential is incorrect");
				}

			} else {
				throw new UnkownCredentialException(
						"Error:signedCredential is empty");
			}
		}
		return null;
	}

	@Override
	public String makeSSOLoginURL(String localURL) {
		String ssourl = getVwbcontext().getProperty("duckling.umt.login");
		try {
			String registerURL = getVwbcontext().getProperty("duckling.umt.link.regist")
					+ "&voName=" + getVwbcontext().getVO();
			return ssourl
					+ "?WebServerURL="
					+ URLEncoder.encode(localURL, "UTF-8")
					+ "&appname="
					+ URLEncoder.encode(
							getVwbcontext().getProperty("duckling.dct.localName", "dct"),
							"UTF-8")
					+ "&logoutURL="
					+ URLEncoder.encode(getVwbcontext().getURL("plain", "logout",
							"umtSsoLogout=true", true), "UTF-8") + "&"
					+ ILoginHandle.APP_REGISTER_URL_KEY + "="
					+ URLEncoder.encode(registerURL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			return ssourl;
		}
	}
}
