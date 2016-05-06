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

import java.util.HashMap;
import java.util.List;

import cn.cnic.esac.clb.util.XMLCharFilter;
import cn.vlabs.duckling.api.vmt.entity.tree.AbstractVONode;
import cn.vlabs.duckling.api.vmt.entity.tree.GroupNode;
import cn.vlabs.duckling.api.vmt.entity.tree.PositionNode;
import cn.vlabs.duckling.api.vmt.entity.tree.UserNode;
import cn.vlabs.duckling.api.vmt.entity.tree.VONode;
import cn.vlabs.duckling.api.vmt.entity.tree.VONodeVisitor;

/**
 * Introduction Here.
 * 
 * @date Mar 2, 2010
 * @author wkm(wkm@cnic.cn)
 */
public class VOTreeJSDumper implements VONodeVisitor {
	private HashMap<String, String> check;
	private GroupNode node;

	public VOTreeJSDumper(GroupNode node) {
		check = new HashMap<String, String>();
		json = new StringBuffer();
		this.node = node;
		json.append(" tree = new WebFXTree('" + node.getDisplayName()
				+ "') ; tree.setBehavior('classic'); tree._sID= 'group/"
				+ node.getName() + "'; a[layer]=tree;\n");
	}

	public void setChecked(HashMap<String, String> checked) {
		check = checked;
		if (check == null)
			check = new HashMap<String, String>();

		if ((check.get("group/" + node.getName()) != null)
				&& ((String) checked.get("group/" + node.getName()))
						.equals("1"))
			json.append("tree._checked = true;");

	}

	private void visitChildren(VONode node) {
		// boolean isVO = isVONode(node);
		if (node != null && node.hasChild()) {
			List<VONode> nodes = node.getChildren();
			dumpType(nodes, AbstractVONode.NODE_GROUP);
			dumpType(nodes, AbstractVONode.NODE_POSITION);
			if (node.getType().equals(AbstractVONode.NODE_GROUP)) {
				json.append("sub = new WebFXTreeItem('" + "成员" + "', '"
						+ XMLCharFilter.filter(node.getName())
						+ ".');a[layer].add(sub);\n");
			}
			dumpType(nodes, AbstractVONode.NODE_USER);
		}
	}

	public void generateTree() {
		this.visitChildren(node);
	}

	private void dumpType(List<VONode> nodes, String type) {
		for (VONode childnode : nodes) {
			if (childnode.getType().equals(type)) {
				childnode.accept(this);
			}
		}
	}

	//
	// private boolean isVONode(Node node)
	// {
	// return "VO".equals(node.getName());
	// }

	public void visit(GroupNode node) {
		String name = node.getDisplayName();
		String id = "group/" + node.getName();
		json.append("sub = new WebFXCheckBoxTreeItem('" + name + "','group','"
				+ id + "');");
		if ((check.get(id) != null) && ((String) check.get(id)).equals("1"))
			json.append("sub._checked = true;");
		json.append("a[layer].add(sub); layer++; a[layer] = sub;\n");

		if (accessChildren()) {
			visitChildren(node);
		}

		json.append("layer--;");

	}

	public void visit(PositionNode node) {
		String name = node.getDisplayName();
		String parentname = "role/" + node.getPosition().getGroupName() + ".";
		json.append("sub = new WebFXCheckBoxTreeItem('" + name + "','role','"
				+ node.getName() + "','" + parentname
				+ "'); a[layer].add(sub);\n");

		if ((check.get(parentname + name) != null)
				&& ((String) check.get(parentname + name)).equals("1"))
			json.append("sub._checked = true;");
		if (accessChildren()) {
			visitChildren(node);
		}

	}

	protected boolean accessChildren() {
		return true;
	}

	public void visit(UserNode node) {
		if (!hideUser) {
			String id = "user/" + node.getName();
			json.append("child = new WebFXCheckBoxTreeItem('"
					+ node.getDisplayName() + "','user','" + id + "');");

			if ((check.get(id) != null) && ((String) check.get(id)).equals("1"))
				json.append("child._checked = true;");
			json.append("sub.add(child);\n");
		}
	}

	public String toString() {
		return json.toString();
		// return
		// "[{id: '系统环境部',text: '系统环境部',leaf:true},{id:'2',text:'A folder Node',children:[{id:'3',text: 'A child Node',leaf: true},]}]";
	}

	public void setHideUser(boolean visitUser) {
		this.hideUser = visitUser;
	}

	private boolean hideUser = false;

	private StringBuffer json;

}
