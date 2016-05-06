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

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cn.vlabs.duckling.api.vmt.entity.tree.GroupNode;
import cn.vlabs.duckling.api.vmt.entity.tree.PositionNode;
import cn.vlabs.duckling.api.vmt.entity.tree.VONode;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.VWBSession;
import cn.vlabs.duckling.vwb.service.user.IUserService;
import cn.vlabs.duckling.vwb.spi.VWBContainer;
import cn.vlabs.duckling.vwb.ui.base.BaseAction;

/**
 * Introduction Here.
 * 
 * @date 2010-3-9
 * @author Fred Zhang (fred@cnic.cn)
 */
public class VOTreeAction extends BaseAction {
	Logger log = Logger.getLogger(VOTreeAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		if (VWBSession.findSession(request).isAuthenticated()) {
			String cmd = request.getParameter("cmd");
			if ((cmd == null) || (cmd.equals("query")))
				queryVOTree(request, response);
		}
		return null;
	}

	private void queryVOTree(HttpServletRequest request,
			HttpServletResponse response) {
		String node = request.getParameter("node");
		response.setCharacterEncoding("UTF-8");

		VWBContext context = VWBContext.createContext(request,
				VWBContext.SIMPLE);
		if ((node == null) || (node.equals("")) || (node.equals("VO"))) {
			node = "group/" + context.getVOGroup();
		} else if (node.indexOf("/") < 0) {
			node = "group/" + node;
		}
		getTree(context, request, response, node);
	}

	private void getTree(VWBContext context, HttpServletRequest request,
			HttpServletResponse response, String node) {
		VWBContainer container = VWBContainerImpl.findContainer();
		String voName = context.getVO();
		IUserService userService = container.getUserService();
		String type = node.substring(0, node.indexOf("/"));
		String name = node.substring(node.indexOf("/") + 1);
		VONode treeNode = null;
		if ("group".equals(type)) {
			treeNode = (GroupNode) userService.getTreeGroupNode(voName, name,
					false);
		} else if ("position".equals(type)) {
			String groupName = name.substring(0, name.indexOf("."));
			String positionName = name.substring(name.indexOf(".") + 1);
			treeNode = (PositionNode) userService.getTreePositionNode(voName,
					groupName, positionName);
		} else if ("user".equals(type)) {
		}
		if (treeNode != null) {
			VOTreeJSONDumper dumper = new VOTreeJSONDumper();
			dumper.visitChildren(treeNode);
			writeToResponse(response, dumper.toString());
		}
	}

	private void writeToResponse(HttpServletResponse response, String xml) {
		response.setContentType("text/html;charset=UTF-8");
		try {
			Writer wr = response.getWriter();
			wr.write(xml);
			wr.close();
		} catch (IOException e) {
			log.debug("Write xml to response error!", e);
		}
	}
}
