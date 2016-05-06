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

package cn.vlabs.duckling.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.vlabs.duckling.api.vmt.entity.tree.AbstractVONode;
import cn.vlabs.duckling.api.vmt.entity.tree.GroupNode;
import cn.vlabs.duckling.api.vmt.entity.tree.PositionNode;
import cn.vlabs.duckling.api.vmt.entity.tree.VONode;
import cn.vlabs.duckling.vwb.VWBContainerImpl;
import cn.vlabs.duckling.vwb.VWBContext;
import cn.vlabs.duckling.vwb.service.user.IUserService;

/**
 * Introduction Here.
 * 
 * @date Mar 2, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class VOTreeUtil {
	private VWBContext context;
	private String scriptHead, scriptBody, scriptFoot;
	private ArrayList<String> selectEmails;
	private IUserService userService;

	private String voName;

	public VOTreeUtil(VWBContext context) {
		this.context = context;
		voName = context.getVO();
		userService = VWBContainerImpl.findContainer().getUserService();
		this.selectEmails = new ArrayList<String>();
	}

	private void generateTree(HashMap<String, String> checked) {
		String rootGroupName = context.getVOGroup();
		GroupNode root = userService.getTreeGroupNode(voName, rootGroupName,
				true);
		VOTreeJSDumper dumper = new VOTreeJSDumper(root);
		dumper.setChecked(checked);
		dumper.generateTree();
		scriptBody += dumper.toString();
	}

	private String getScripts() {
		return scriptHead + scriptBody + scriptFoot;
	}

	private void initScript() {
		scriptHead = "<script language='JavaScript'>"
				+ " var parent = null ;"
				+ " var child = null ;"
				+ " var grandparent = null ; var tree = null; var sub=null; var a=new Array(100); var layer=0;\n";

		scriptBody = "";

		scriptFoot = "document.write(tree);" + "</script>";
	}

	public void compareTree(HashMap<String, String> checked) {
		Iterator<String> iter = checked.keySet().iterator();
		VONode treeNode = null;
		while (iter.hasNext()) {
			String id = iter.next();
			String type = id.substring(0, id.indexOf("/"));
			String name = id.substring(id.indexOf("/") + 1);
			if ("group".equals(type)) {
				treeNode = (GroupNode) userService.getTreeGroupNode(voName,
						name, false);
				if (treeNode != null && treeNode.hasChild()) {
					List<VONode> nodes = treeNode.getChildren();
					for (VONode childnode : nodes) {
						if (childnode.getType()
								.equals(AbstractVONode.NODE_USER)) {
							selectEmails.add(childnode.getName());
						}
					}
				}
			} else if ("role".equals(type)) {
				String groupName = name.substring(0, name.indexOf("."));
				String positionName = name.substring(name.indexOf(".") + 1);
				treeNode = (PositionNode) userService.getTreePositionNode(
						voName, groupName, positionName);
				if (treeNode != null && treeNode.hasChild()) {
					List<VONode> nodes = treeNode.getChildren();
					for (VONode childnode : nodes) {
						if (childnode.getType()
								.equals(AbstractVONode.NODE_USER)) {
							selectEmails.add(childnode.getName());
						}
					}
				}
			} else if ("user".equals(type)) {
				selectEmails.add(name);
			}
		}
	}

	public String generateScripts(HashMap<String, String> checked) {
		initScript();
		generateTree(checked);
		return getScripts();
	}

	public ArrayList<String> getSelectedEmail(String checked) {
		HashMap<String, String> check = new HashMap<String, String>();
		if (checked != null) {
			String[] values = checked.split(",");
			for (String temp : values) {
				if ((temp != null) && !(temp.trim().equals("")))
					check.put(temp, "1");
			}
		}

		compareTree(check);

		@SuppressWarnings("unchecked")
		ArrayList<String> tmpUsers = (ArrayList<String>) this.selectEmails
				.clone();
		this.selectEmails.clear();
		for (String tmp : tmpUsers) {
			boolean find = false;
			for (String tmp1 : this.selectEmails) {
				if (tmp.equals(tmp1)) {
					find = true;
					break;
				}
			}
			if (!find)
				this.selectEmails.add(tmp);
		}

		return this.selectEmails;

	}

}
