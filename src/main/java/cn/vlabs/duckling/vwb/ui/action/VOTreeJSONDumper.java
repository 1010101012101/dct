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

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.vlabs.duckling.api.vmt.entity.tree.AbstractVONode;
import cn.vlabs.duckling.api.vmt.entity.tree.GroupNode;
import cn.vlabs.duckling.api.vmt.entity.tree.PositionNode;
import cn.vlabs.duckling.api.vmt.entity.tree.UserNode;
import cn.vlabs.duckling.api.vmt.entity.tree.VONode;
import cn.vlabs.duckling.api.vmt.entity.tree.VONodeVisitor;



/**
 * Introduction Here.
 * @date 2010-3-9
 * @author Fred Zhang (fred@cnic.cn)
 */
public class VOTreeJSONDumper implements VONodeVisitor {
	   // private String VO;
	   // private String VOPath;
	    //private int childNumber; 
	    
//	    public VOTreeJSONDumper()
//	    {
//	        json = new StringBuffer();
//	        json.append("[");
//	        //childNumber = 0;
//	    }
	    
	    public VOTreeJSONDumper() {
	    	jsonArray = new JSONArray();
	    }

	    public void visitChildren(VONode node)
	    {
	        //boolean isVO = isVONode(node);
	        if (node != null && node.hasChild())
	        {
	            List<VONode> nodes = node.getChildren();
	            dumpType(nodes, AbstractVONode.NODE_GROUP);
	            dumpType(nodes, AbstractVONode.NODE_POSITION);
	            dumpType(nodes, AbstractVONode.NODE_USER);
	        }
	    }

	    private void dumpType(List<VONode> nodes, String type)
	    {
	        for (VONode childnode : nodes)
	        {
	            if (childnode.getType().equals(type))
	            {
	                 childnode.accept(this);
	            }
	        }
	    }

//	    private boolean isVONode(Node node)
//	    {
//	        return "VO".equals(node.getName());
//	    }

	    public void visit(GroupNode node)
	    {
	    	JSONObject jsonObject=new JSONObject();
	        String name = node.getName();
	        jsonObject.put("name", name);
	        jsonObject.put("text", node.getName());
	        jsonObject.put("type", "group");
	        jsonObject.put("description", name);
	        jsonArray.put(jsonObject);
	    }

	    public void visit(PositionNode node)
	    {
	    	JSONObject jsonObject=new JSONObject();
	        String name = node.getPosition().getGroupName()+"."+node.getName();
	        
	        jsonObject.put("id", "position/"+name);
	        jsonObject.put("text", node.getPosition().getGroupName()+"."+node.getPosition().getName());
	        jsonObject.put("type", "role");
	        jsonArray.put(jsonObject);
	    }
//
//	    protected boolean accessChildren()
//	    {
//	        return false;
//	    }

	    public void visit(UserNode node)
	    {
	        if (!hideUser)
	        {
	        	JSONObject jsonObject=new JSONObject();
	        	 jsonObject.put("id", "user/"+node.getName());
	            String displayName = node.getUser().getDisplayName();
	            if ((displayName == null) ||(displayName.equals(""))){
	            	  jsonObject.put("text",node.getName());
	            }else{
	            	 jsonObject.put("text",displayName + "(" + node.getName() +")");
	            }
		        jsonObject.put("type", "user");
		        jsonObject.put("leaf", true);
		        jsonArray.put(jsonObject);
	        }
	    }

	    public String toString()
	    {
	        return jsonArray.toString();
	    }

	    public void setHideUser(boolean visitUser)
	    {
	        this.hideUser = visitUser;
	    }

	    private boolean hideUser = false;

	    /*private StringBuffer json;*/
	    
	    private JSONArray jsonArray;

}
