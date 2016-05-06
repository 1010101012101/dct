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

package cn.vlabs.duckling.vwb.ui.action.page;

import java.io.IOException;
import java.io.Writer;
import java.security.Permission;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.GroupPrincipal;
import cn.vlabs.duckling.vwb.service.auth.Role;
import cn.vlabs.duckling.vwb.service.auth.UserPrincipal;
import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.acl.AclEntry;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.base.BaseAction;
import cn.vlabs.duckling.vwb.ui.command.DPageCommand;
import cn.vlabs.duckling.vwb.ui.command.PortalCommand;

import com.thoughtworks.xstream.XStream;

/**
 * Introduction Here.
 * 
 * @date 2010-3-9
 * @author Fred Zhang (fred@cnic.cn)
 */
public class PrivsAction extends BaseAction {
	ResourceBundle rb = null;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		Resource res = this.getSavedViewPort(request);
		VWBContext context = null;
		if (Resource.TYPE_DPAGE.equals(res.getType())) {
			context = VWBContext.createContext(request, DPageCommand.EDIT_INFO,
					res);
		} else if (Resource.TYPE_PORTAL.equals(res.getType())) {
			context = VWBContext.createContext(request, PortalCommand.CONFIG,
					res);
		}
		rb = context.getBundle("templates/default");
		if (context.hasAccess(response)) {
			String cmd = request.getParameter("cmd");
			response.setStatus(200);
			if (cmd.equals("query")) {
				queryPrivilege(request, response, res, context.getSiteId());
			} else if (cmd.equals("save")) {
				savePrivilege(request, response, res);
			} else if (cmd.equals("delete")) {
				deletePrivilege(request, response, res);
			} else if (cmd.equals("publicview")) {
				publicViewPrivilege(request, response, res);
			} else {
				publicPrivilege(request, response, res);
			}
		}

		return null;
	}

	/**
	 * Brief Intro Here
	 * 
	 * @param
	 */
	private void publicPrivilege(HttpServletRequest request,
			HttpServletResponse response, Resource res) {

		@SuppressWarnings("unchecked")
		List<PagePrivVo> privs = (List<PagePrivVo>) request.getSession()
				.getAttribute("pagePrivileges");
		if (privs == null)
			privs = new ArrayList<PagePrivVo>();
		else
			privs.clear();
		PagePrivVo priv = new PagePrivVo();
		priv.setName("All");
		priv.setType("role");
		priv.setView("true");
		priv.setEdit("true");
		privs.add(priv);
		request.getSession().setAttribute("pagePrivileges", privs);
		request.getSession().setAttribute("updatePrivileges", "true");
		writeToResponse(response, "success");

	}

	/**
	 * Brief Intro Here
	 * 
	 * @param
	 */
	private void publicViewPrivilege(HttpServletRequest request,
			HttpServletResponse response, Resource res) {
		String type = request.getParameter("type");
		String name = "All";
		String view = "true";
		String edit = "false";
		@SuppressWarnings("unchecked")
		List<PagePrivVo> privs = (List<PagePrivVo>) request.getSession()
				.getAttribute("pagePrivileges");

		if (privs == null)
			privs = new ArrayList<PagePrivVo>();

		boolean found = false;
		for (PagePrivVo priv : privs) {
			if (priv.getName().equals(name) && priv.getType().equals(type)) {
				priv.setView(view);
				priv.setEdit(edit);
				break;
			}
		}

		if (!found) {
			PagePrivVo priv = new PagePrivVo();
			priv.setName(name);
			priv.setType("role");
			priv.setView(view);
			priv.setEdit(edit);
			privs.add(priv);
		}
		request.getSession().setAttribute("pagePrivileges", privs);
		request.getSession().setAttribute("updatePrivileges", "true");
		writeToResponse(response, "success");

	}

	private void deletePrivilege(HttpServletRequest request,
			HttpServletResponse response, Resource res) {
		String type = request.getParameter("type");
		String name = request.getParameter("name");
		@SuppressWarnings("unchecked")
		List<PagePrivVo> privs = (List<PagePrivVo>) request.getSession()
				.getAttribute("pagePrivileges");

		if (privs == null)
			privs = new ArrayList<PagePrivVo>();

		if (type != null && name != null) {
			String[] types = type.split(",");
			String[] names = name.split(",");
			if (types.length == names.length) {
				for (int i = 0; i < types.length; i++) {
					for (PagePrivVo priv : privs) {
						if (priv.getName().indexOf("(All)") > 0) {
							if ("All".equals(names[i])
									&& priv.getType().equals(types[i])) {
								privs.remove(priv);
								break;
							}
						}
						if (priv.getName().equals(names[i])
								&& priv.getType().equals(types[i])) {
							privs.remove(priv);
							break;
						}
					}
				}
			} else {
				outFailureMsg(response, rb.getString("pagepriv.error.delete"));
				log.debug("Failure to delete privilege.");
			}
		} else {
			outFailureMsg(response, rb.getString("pagepriv.error.delete"));
			log.debug("Failure to delete privilege.");
		}

		request.getSession().setAttribute("pagePrivileges", privs);
		request.getSession().setAttribute("updatePrivileges", "true");
		writeToResponse(response, "success");

	}

	private void savePrivilege(HttpServletRequest request,
			HttpServletResponse response, Resource res) {
		String type = request.getParameter("type");
		String name = request.getParameter("name");
		String view = request.getParameter("view");
		String edit = request.getParameter("edit");
		@SuppressWarnings("unchecked")
		List<PagePrivVo> privs = (List<PagePrivVo>) request.getSession()
				.getAttribute("pagePrivileges");
		if (privs == null)
			privs = new ArrayList<PagePrivVo>();

		boolean found = false;
		for (PagePrivVo priv : privs) {
			if (priv.getName().equals(name) && priv.getType().equals(type)) {
				found = true;
				if (view.equals("false") && (edit.equals("false"))) {
					privs.remove(priv);
					break;
				}
				priv.setView(view);
				priv.setEdit(edit);
				break;
			}
		}

		if (!found) {
			if (!view.equals("false") || !(edit.equals("false"))) {
				PagePrivVo priv = new PagePrivVo();
				priv.setName(name);
				priv.setType(type);
				priv.setView(view);
				priv.setEdit(edit);
				privs.add(priv);
			}
		}
		request.getSession().setAttribute("updatePrivileges", "true");
		request.getSession().setAttribute("pagePrivileges", privs);
		writeToResponse(response, "success");
	}

	private String convertToXML(List<PagePrivVo> docs) {
		XStream stream = new XStream();
		stream.alias("privilege", PagePrivVo.class);
		String xml = stream.toXML(docs);
		return xml;
	}

	private void queryPrivilege(HttpServletRequest request,
			HttpServletResponse response, Resource res, int siteId) {
		@SuppressWarnings("unchecked")
		List<PagePrivVo> privs = (List<PagePrivVo>) request.getSession()
				.getAttribute("pagePrivileges");
		if (privs == null){
			privs = getPagePrivs(siteId, res.getResourceId());
		}
		if (privs == null || privs.size() == 0) {
			privs = getPagePrivs(siteId,
					VWBContainerImpl.findContainer().getViewPortService()
							.getMappedAclId(siteId, res.getResourceId()));
			request.getSession().setAttribute("updatePrivileges", "true");
		}
		if ((privs != null) && (privs.size() > 0)) {
			request.getSession().setAttribute("pagePrivileges", privs);
			String xml = convertToXML(privs);
			writeToResponse(response, xml);
		} else {
			request.getSession().setAttribute("pagePrivileges",
					new ArrayList<PagePrivVo>());
			writeToResponse(response, "");
		}
	}

	private List<PagePrivVo> getPagePrivs(int siteId, int resourceid) {
		Acl acl = VWBContainerImpl.findContainer().getAuthorizationService()
				.getViewPortAcl(siteId, resourceid);
		List<PagePrivVo> pvos = new ArrayList<PagePrivVo>();
		if (acl != null) {
			Enumeration<AclEntry> e = acl.entries();
			while (e.hasMoreElements()) {
				AclEntry entry = (AclEntry) e.nextElement();
				PagePrivVo priv = new PagePrivVo();
				Principal principal = entry.getPrincipal();
				priv.setName(VWBContainerImpl.findContainer().getUserService()
						.queryUser(principal.getName()));
				if (priv.getName() != null && priv.getName().equals("All")) {
					priv.setName(rb.getString("javascript.page.allentities"));
				}
				if (principal instanceof UserPrincipal) {
					priv.setType("user");
				} else if (principal instanceof GroupPrincipal) {
					priv.setType("group");
				} else if (principal instanceof Role) {
					priv.setType("role");
				}
				for (Enumeration<Permission> ps = entry.permissions(); ps
						.hasMoreElements();) {
					Permission p = (Permission) ps.nextElement();
					String action = p.getActions();
					if (action != null) {
						if (action.indexOf("view") > -1) {
							priv.setView("true");
						}
						if (action.indexOf("edit") > -1) {
							priv.setEdit("true");
						}
					}
				}
				pvos.add(priv);
			}
		}
		return pvos;
	}

	private void writeToResponse(HttpServletResponse response, String xml) {
		response.setContentType("text/xml;charset=UTF-8");
		try {
			Writer wr = response.getWriter();
			wr.write(xml);
			wr.close();
		} catch (IOException e) {
			outFailureMsg(response, rb.getString("docpriv.error.xml"));
			log.debug("Write xml to response error!", e);
		}
	}

	private void outFailureMsg(HttpServletResponse response, String msg) {
		response.setStatus(404);
		try {
			response.getOutputStream().print(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
