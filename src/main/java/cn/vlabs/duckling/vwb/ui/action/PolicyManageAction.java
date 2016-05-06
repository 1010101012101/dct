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
package cn.vlabs.duckling.vwb.ui.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.auth.AuthorizationService;
import cn.vlabs.duckling.vwb.service.auth.acl.Acl;
import cn.vlabs.duckling.vwb.service.auth.policy.AuthorizationLexicParseException;
import cn.vlabs.duckling.vwb.service.auth.policy.AuthorizationSyntaxParseException;
import cn.vlabs.duckling.vwb.service.auth.policy.PolicyData;
import cn.vlabs.duckling.vwb.service.auth.policy.PolicyUtil;
import cn.vlabs.duckling.vwb.service.dpage.DPage;
import cn.vlabs.duckling.vwb.service.dpage.DPageService;
import cn.vlabs.duckling.vwb.service.resource.Resource;
import cn.vlabs.duckling.vwb.ui.base.BaseDispatchAction;
import cn.vlabs.duckling.vwb.ui.command.VWBCommand;

/**
 * Introduction Here.
 * 
 * @date May 6, 2010
 * @author Sun Peng (sunp@cnic.cn)
 */
public class PolicyManageAction extends BaseDispatchAction {
	protected ActionForward dispatchMethod(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response, String name) throws Exception {

		request.setAttribute("showPreview", "'init'");

		if (name == null || name.equals("")) {
			name = "query";
		}
		Resource res = this.getSavedViewPort(request);
		VWBContext context = VWBContext.createContext(request,
				VWBCommand.ADMIN, res);

		if (context.hasAccess(response)) {
			return super.dispatchMethod(mapping, form, request, response, name);
		} else
			return null;
	}

	public ActionForward init(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		return doManageLayout(context);
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		Acl policy = VWBContext.getContainer().getAuthorizationService().getPolicy(context.getSiteId());
		String policyStr = PolicyUtil.acl2PolicyString(policy);

		request.setAttribute("PolicyStr", policyStr);

		request.setAttribute("showPreview", "'show'");

		request.getSession().setAttribute("PolicyNotify", "当前全局权限");
		request.getSession().setAttribute("PolicyNotifyColor", "blue");

		return doManageLayout(context);
	}

	public ActionForward query(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		Acl policy = VWBContext.getContainer().getAuthorizationService().getPolicy(context.getSiteId());

		List<PolicyData> pdl = PolicyUtil.acl2PolicyData(policy);

		pdl = translate(context.getSiteId(),VWBContainerImpl.findContainer().getDpageService(),pdl);

		request.getSession().setAttribute("ACL", policy);
		request.getSession().setAttribute("PData", pdl);
		request.getSession().setAttribute("PolicyNotify", "当前全局权限");
		request.getSession().setAttribute("PolicyNotifyColor", "blue");

		return doManageLayout(context);
	}
	
	public ActionForward template(ActionMapping mapping, ActionForm p_form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		final DynaActionForm form = (DynaActionForm) p_form;
		final String templateId = (String) form.get("templateId");
		if (templateId == null)
			return null;
		int templateIdInt = Integer.parseInt(templateId.trim());

		Acl policy = loadTemplatePolicy(templateIdInt);

		List<PolicyData> pdl = PolicyUtil.acl2PolicyData(policy);
		pdl = translate(context.getSiteId(), VWBContainerImpl.findContainer().getDpageService(),pdl);

		String vogroup = context.getVOGroup();

		if (vogroup != null && !vogroup.equals("")) {
			policy = PolicyUtil.replacePolicy(policy, vogroup);
			pdl = PolicyUtil.replacePolicyData(pdl, vogroup);
		}

		request.getSession().setAttribute("ACL", policy);
		request.getSession().setAttribute("PData", pdl);
		if (templateIdInt == 0) {
			request.getSession().setAttribute("PolicyNotify", "当前全局权限");
			request.getSession().setAttribute("PolicyNotifyColor", "blue");
		} else if (templateIdInt == 1) {
			request.getSession().setAttribute("PolicyNotify",
					"\"内部使用\"模板.当前权限配置已更改，提交后生效.");
			request.getSession().setAttribute("PolicyNotifyColor", "red");
		} else if (templateIdInt == 2) {
			request.getSession().setAttribute("PolicyNotify",
					"\"公开发布\"模板.当前的权限配置已更改，提交后生效.");
			request.getSession().setAttribute("PolicyNotifyColor", "red");
		}
		return doManageLayout(context);
	}

	private Acl loadTemplatePolicy(int templateIdInt) {
		Acl policy = null;
		try {
			String filename0 = "conf/duckling.policy";
			String filename1 = "conf/duckling.policy.teamwork";
			String filename2 = "conf/duckling.policy.publish";
			String filepath = null;
			if (templateIdInt == 0)
				filepath = getAbsoluteWebRootPath("/WEB-INF/"+filename0);
			if (templateIdInt == 1)
				filepath = getAbsoluteWebRootPath("/WEB-INF/"+filename1);
			if (templateIdInt == 2)
				filepath = getAbsoluteWebRootPath("/WEB-INF/"+filename2);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(filepath)), "UTF-8"));
			policy = PolicyUtil.parseEntry(br);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException : " + e.getMessage());
		} catch (AuthorizationSyntaxParseException e) {
			log.error(e.getMessage());
		} catch (AuthorizationLexicParseException e) {
			log.error(e.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return policy;
	}

	public ActionForward add(ActionMapping mapping, ActionForm p_form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);

		final DynaActionForm form = (DynaActionForm) p_form;

		final String principal = (String) form.get("addPrincipal");
		final String permission = (String) form.get("addPermission");
		if (permission == null)
			return doManageLayout(context);

		String resource = null;
		String resourceContent = null;
		String operation = null;
		if (permission.equals("Page")) {
			resource = (String) form.get("addResource");
			if (resource == null)
				return doManageLayout(context);
			if (resource.equals("All")) {
				resource = "*";
			} else {
				resourceContent = (String) form.get("addResourceContent");
				if (resourceContent == null || resourceContent.equals(""))
					return doManageLayout(context);
				resource = resourceContent;
			}
			operation = (String) form.get("addOperation1");
			if (operation == null)
				return doManageLayout(context);
			if (operation.equals("edit")) {
				operation = "view," + operation;
			}
		}
		if (permission.equals("VWB")) {
			resource = "*";
			String[] tmpArr = (String[]) form.get("addOperation2");
			if (tmpArr == null)
				return doManageLayout(context);

			String tmpStr = "login,";
			int size = tmpArr.length;
			for (int i = 0; i < size; i++) {
				tmpStr += tmpArr[i];
				tmpStr += ",";
			}
			operation = tmpStr.substring(0, tmpStr.length() - 1);

		}

		Acl policy = (Acl) request.getSession().getAttribute("ACL");
		@SuppressWarnings("unchecked")
		List<PolicyData> pdl = (List<PolicyData>) request.getSession()
				.getAttribute("PData");

		PolicyData pd = PolicyUtil.add(context.getVOGroup(), policy, principal,
				permission, operation, resource);
		pd.setId(getUniqueId(pdl));

		pd = translate(context.getSiteId(), VWBContainerImpl.findContainer().getDpageService(),pd);
		pdl.add(pd);

		request.getSession().setAttribute("ACL", policy);
		request.getSession().setAttribute("PData", pdl);
		request.getSession().setAttribute("PolicyNotify", "当前的权限配置已更改，提交后生效.");
		request.getSession().setAttribute("PolicyNotifyColor", "red");

		return doManageLayout(context);
	}

	private int getUniqueId(List<PolicyData> pdl) {
		int size = pdl.size();
		int max = 0;
		for (int i = 0; i < size; i++) {
			int currentId = pdl.get(i).getId();
			if (currentId > max) {
				max = currentId;
			}
		}
		max += 1;
		return max;
	}

	public ActionForward delete(ActionMapping mapping, ActionForm p_form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);

		final DynaActionForm form = (DynaActionForm) p_form;

		final String id = (String) form.get("delPolicyId");

		Acl policy = (Acl) request.getSession().getAttribute("ACL");
		@SuppressWarnings("unchecked")
		List<PolicyData> pdl = (List<PolicyData>) request.getSession()
				.getAttribute("PData");

		PolicyData pd = null;
		int size = pdl.size();
		int delIndex = -1;
		for (int i = 0; i < size; i++) {
			if (String.valueOf(pdl.get(i).getId()).equals(id)) {
				pd = pdl.get(i);
				delIndex = i;
			}
		}

		if (delIndex == -1) {
			request.getSession().setAttribute("PolicyNotify",
					"当前的权限配置已更改，提交后生效.");
			request.getSession().setAttribute("PolicyNotifyColor", "red");

			return doManageLayout(context);
		}

		pdl.remove(delIndex);

		PolicyUtil.remove(policy, pd);

		request.getSession().setAttribute("ACL", policy);
		request.getSession().setAttribute("PData", pdl);
		request.getSession().setAttribute("PolicyNotify", "当前的权限配置已更改，提交后生效.");
		request.getSession().setAttribute("PolicyNotifyColor", "red");

		return doManageLayout(context);
	}

	public ActionForward save(ActionMapping mapping, ActionForm p_form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		Acl policy = (Acl) request.getSession().getAttribute("ACL");

		// 替换配置文件中，图形接口不能管理的部分为原始内容
		Acl origin = VWBContext.getContainer().getAuthorizationService().getPolicy(context.getSiteId());
		policy = PolicyUtil.removeReserved(policy);
		policy = PolicyUtil.restoreReserved(policy, origin);

		String vogroup = context.getVOGroup();

		if (vogroup != null && !vogroup.equals("")) {
			policy = PolicyUtil.replacePolicy(policy, vogroup);
		}

		request.getSession().setAttribute("PolicyNotify", "当前全局权限");
		request.getSession().setAttribute("PolicyNotifyColor", "blue");

		AuthorizationService as = VWBContext.getContainer().getAuthorizationService();
		as.apply(context.getSiteId(), policy);
		request.getSession().removeAttribute("ACL");
		return query(mapping, p_form, request, response);
	}

	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		VWBContext context = VWBContext.getContext(request);
		return doManageLayout(context);
	}

	private String getAbsoluteWebRootPath(String path){
		return getServlet().getServletContext().getRealPath(path);
	}
	private List<PolicyData> translate(int siteId, DPageService dps, List<PolicyData> pdl) {
		int SIZE = pdl.size();
		for (int i = 0; i < SIZE; i++) {
			String tmp = pdl.get(i).getOperation();
			String tmpTrans = translate(tmp);
			pdl.get(i).setOperation(tmpTrans);
			String tmp2 = pdl.get(i).getResource();
			String tmpTrans2 = translate2(siteId, dps, tmp2);
			pdl.get(i).setResource(tmpTrans2);
		}
		return pdl;
	}

	private PolicyData translate(int siteid, DPageService dps, PolicyData pd) {
		String tmp = pd.getOperation();
		String tmpTrans = translate(tmp);
		pd.setOperation(tmpTrans);

		String tmp2 = pd.getResource();
		String tmpTrans2 = translate2(siteid, dps,tmp2);
		pd.setResource(tmpTrans2);

		return pd;
	}

	private String translate(String tmp) {
		String[] arr = tmp.split(",");

		String tmpOp = null;
		StringBuffer buffOp = new StringBuffer("");
		for (int j = 0; j < arr.length; j++) {
			tmpOp = arr[j].trim();
			if (tmpOp.equalsIgnoreCase("view")) {
				buffOp.append("只读");
				buffOp.append(",");
			} else if (tmpOp.equalsIgnoreCase("edit")) {
				buffOp.append("可写");
				buffOp.append(",");
			} else if (tmpOp.equalsIgnoreCase("login")) {
				buffOp.append("登录");
				buffOp.append(",");
			} else if (tmpOp.equalsIgnoreCase("upload")) {
				buffOp.append("上传");
				buffOp.append(",");
			} else if (tmpOp.equalsIgnoreCase("portlet")) {
				buffOp.append("插件");
				buffOp.append(",");
			} else if (tmpOp.equalsIgnoreCase("search")) {
				buffOp.append("搜索");
				buffOp.append(",");
			} else if (tmpOp.equalsIgnoreCase("editProfile")) {
				buffOp.append("个人设置");
				buffOp.append(",");
			} else {
				buffOp.append("错误权限");
				buffOp.append(",");
			}
		}
		String buff = buffOp.toString();
		buff = buff.substring(0, buff.length() - 1);

		return buff;
	}

	private String translate2(int siteId,DPageService dps, String tmp) {

		if (tmp.trim().equals("*"))
			return "所有页面";

		int id = Integer.parseInt(tmp.trim());
		DPage page = dps.getLatestDpageByResourceId(siteId, id);
		String title = page.getTitle();

		return title + "(" + id + ")";
	}

	private ActionForward doManageLayout(VWBContext context) {
		return layout(context, "/jsp/PolicyManage.jsp");
	}
}
