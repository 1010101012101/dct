<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed  under the  License is distributed on an "AS IS" BASIS,
WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
implied.

See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://portals.apache.org/pluto" prefix="pluto"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://portals.apache.org/pluto/portlet-el"	prefix="portlet-el"%>
<fmt:setBundle basename="portlet/AdminPortlet" />
<vwb:Permission permission="!allPermission">
	<fmt:message key="permission.deny"/>
</vwb:Permission>
<vwb:Permission permission="allPermission">
<% pageContext.setAttribute("basepath", request.getContextPath());%>
<portlet:defineObjects />
<style>
	a.ui-portal-remove{
		display:none;
		margin:0;
		cursor:pointer;
	}
</style>
<portlet:actionURL var="formActionUrl" />
<!--  script type="text/javascript"
	src="${basepath}/scripts/jquery/jquery-1.3.2.min.js"></script>-->
<script type="text/javascript"
	src="${basepath}/scripts/jquery/selectcontrol.js"></script>
<script type="text/javascript"
	src="${basepath}/scripts/jquery/jquery-ui-1.7.2.custom.min.js"></script>
<link
	href="${basepath}/scripts/jquery/cupertino/jquery-ui-1.7.2.custom.css"
	rel="stylesheet" type="text/css">
	<script language="javascript">
	function showbutton(owrapper, buttonid){
		owrapper.style.cursor='pointer';
		var button =document.getElementById(buttonid);
		button.style.display="block";
	}
	function hidebutton(owrapper, buttonid){
		owrapper.style.cursor='pointer';
		var button =document.getElementById(buttonid);
		button.style.display="none";
	}
</script>
	<fieldset style="width:95%;margin-left:auto;margin-right:auto;">
		<legend>
			<fmt:message key="title.applications"></fmt:message>
		</legend>
		<div id="<portlet:namespace/>tabs">
			<ul>
					<c:set value="0" var="selectindex"></c:set>
					<c:forEach items="${availablePages}" var="page"  varStatus ="status">
						<c:if test="${page.id==selectpage}">
							<c:set value="${status.index}" var="selectindex"></c:set>
						</c:if>
						<li onMouseOver="showbutton(this, '<portlet:namespace/>p${page.id}_button')"
							onMouseOut="hidebutton(this, '<portlet:namespace/>p${page.id}_button')"
							style="cursor:pointer">
							<a href="#<portlet:namespace/>p${page.id}"><c:out value="${page.name}" />
							</a>
							<a id="<portlet:namespace/>p${page.id}_button" href="#" onClick="<portlet:namespace/>showdialog('${page.name}', '${page.id}')"
								style="margin:0;padding-left:0px;padding-right:0px;cursor:pointer;display:none;">
								<img src="${basepath}/images/del.png" /> </a>
						</li>
					</c:forEach>
				<li>
					<a href="#<portlet:namespace/>AddPage">+</a>
				</li>
			</ul>
			<c:forEach items="${availablePages}" var="page">
				<div id="<portlet:namespace/>p${page.id}">
					<form action="<c:out value="${formActionUrl}"/>" method="POST" name="<portlet:namespace/>p${page.id}_form">
						<input type="hidden" name="commandval" value="changeportlet" />
						<input type="hidden" name="page" value="${page.id}"/>
						<table width="100%">
							<tr>
								<td width="43%" valign="top">
									<div style="margin-left:auto; width:100%;height:100%;padding-bottom:10px;">
										<fmt:message key="title.exists"></fmt:message>
										<select multiple='true' style="height:325px; width:100%;"
											id="<portlet:namespace/>p${page.id}_pagePortlets" name="placedPortlets">
											<c:forEach items="${page.portlets}" var="portlet"
												varStatus="loopStatus">
												<option value="${portlet.id}">
													${portlet.portletName}
												</option>
											</c:forEach>
										</select>
									</div>
								</td>
								<td width="14%">
									<table style="margin-left:auto;margin-right:auto;height:300px;padding-bottom:10px;">
										<tr>
											<td>
												<div class="ui-state-default ui-corner-all"
													style="width:60px;height:60px;cursor:pointer;"
													onClick="<portlet:namespace/>p${page.id}_addAll()">
													<img style="margin:16px;" src="${basepath}/images/removeArrow.png" title="<fmt:message key='tooltip.add'/>" />
												</div>
											</td>
										</tr>
										<tr>
											<td>
												<div class="ui-state-default ui-corner-all"
													style="width:60px;height:60px;cursor:pointer;"
													onClick="<portlet:namespace/>p${page.id}_removeAll()">
													<img style="margin:16px"
														src="${basepath}/images/add.png" title="<fmt:message key='tooltip.remove'/>" />
												</div>
											</td>
										</tr>
									</table>
								</td>
								<td width="43%" valign="top">
									<div style="width:100%;margin-right:auto;">
										<fmt:message key="title.candidates"/>
										<select style="width:100%;"
											onChange="<portlet:namespace/>p${page.id}_changeAvailables(this)"
											name="applications">
											<option value='-'>
												<fmt:message key="button.selectapp" />
											</option>
											<c:forEach
												items="${portletContainer.optionalContainerServices.portletContextService.portletContexts}"
												var="app">
												<option value="<c:out value="${app.applicationName}"/>">
													<c:out value="${app.applicationName}" />
												</option>
											</c:forEach>
										</select>
										<select multiple style="height:300px;width:100%"
											id="<portlet:namespace/>p${page.id}_avaibles">
										</select>
									</div>
								</td>
							</tr>
						</table>
						<hr>
						<table width="100%">
							<tr>
								<td style="color:#A0A0A4;">
									<fmt:message key="title.address"></fmt:message>
									<c:set var="pageurl"><vwb:Link page="${page.id}" absolute="true" format="url"/></c:set>
									
									<a href="${pageurl}">${pageurl}</a>
								</td>
								<td align="right">
									<input type="button" onclick="<portlet:namespace/>p${page.id}_submit()" value="<fmt:message key='button.submit'/>" />
									<input type="button" onclick="<portlet:namespace/>p${page.id}_reset()" value="<fmt:message key='button.reset'/>"/>
								</td>
							</tr>
						</table>
					</form>
					<script language="javascript">
					var <portlet:namespace/>p${page.id}_candidate=new ControlSelect('<portlet:namespace/>p${page.id}_avaibles');
					var <portlet:namespace/>p${page.id}_pagePortlets = new ControlSelect('<portlet:namespace/>p${page.id}_pagePortlets');
					function <portlet:namespace/>p${page.id}_reset(){
						document.<portlet:namespace/>p${page.id}_form.reset();
						<portlet:namespace/>p${page.id}_candidate.reset();
						<portlet:namespace/>p${page.id}_pagePortlets.reset();
					}
					
					function <portlet:namespace/>p${page.id}_submit(){
						<portlet:namespace/>p${page.id}_pagePortlets.selectAll();
						document.<portlet:namespace/>p${page.id}_form.submit();
					}
					function <portlet:namespace/>p${page.id}_addAll(){
						<portlet:namespace/>p${page.id}_candidate.each(function(text, value){
							<portlet:namespace/>p${page.id}_pagePortlets.append(text, value);
						});
					}
					function <portlet:namespace/>p${page.id}_removeAll(){
						<c:choose>
							<c:when test="${page.name=='admin'}">
								if (<portlet:namespace/>p${page.id}_pagePortlets.removeSelect("PlutoPageAdmin")){
									alert('<fmt:message key="portlet.reserve"/>');
								}
							</c:when>
							<c:otherwise>
								<portlet:namespace/>p${page.id}_pagePortlets.removeSelect();
							</c:otherwise>
						</c:choose>
					}
					function <portlet:namespace/>p${page.id}_changeAvailables(oList){
						<portlet:namespace/>p${page.id}_candidate.removeAll();
						var portlets = apps[oList.value];
						if (portlets!=null){
							for (var i=0;i<portlets.length;i++){
								<portlet:namespace/>p${page.id}_candidate.append(portlets[i].text, portlets[i].value);
							}
						}
					}	
			</script>
				</div>
			</c:forEach>
			<div id="<portlet:namespace/>AddPage" style="height:345px ">
				<form name="<portlet:namespace/>addpageform" action="<c:out value="${formActionUrl}"/>" method="POST" onsubmit='return <portlet:namespace/>doAddpage()'>
					<input type="hidden" name="commandval" value="addpage" />
					<fmt:message key="title.pagename"/>
					<input type="text" name="newPage">
					<select name="pagetypes">
						<option value="one_line">
							<fmt:message key="title.theme.onecolumn"/>
						</option>
						<option value="left_menu">
							<fmt:message key="title.theme.leftmenu"/>
						</option>
						<option value="two_line">
							<fmt:message key="title.theme.twocolumn"/>
						</option>
					</select>
					<input type="submit" value="<fmt:message key='button.addpage'/>" />
				</form>
			</div>
		</div>
	</fieldset>
	<div id="<portlet:namespace/>dialog">
		<p id="<portlet:namespace/>message">
		</p>
	</div>
	<script type="text/javascript">
		var apps=new Array();
        <c:forEach items="${portletContainer.optionalContainerServices.portletContextService.portletContexts}" var="app">
            apps['<c:out value="${app.applicationName}"/>'] = new Array();
          <c:forEach items="${app.portletApplicationDefinition.portlets}" var="portlet" varStatus="loopStatus">
            apps['<c:out value="${app.applicationName}"/>'][<c:out value="${loopStatus.index}"/>] = {text:'<c:out value="${portlet.portletName}"/>', value:'${app.applicationName}.${portlet.portletName}'};
          </c:forEach>
        </c:forEach>
        function <portlet:namespace/>doAddpage(){
        	var pagename=document.<portlet:namespace/>addpageform.newPage.value;
        	if (pagename==null||pagename==""){
        		alert("<fmt:message key='tooltip.pagename.empty'/>");
        		return false;
        	}
        	var regx=/^[a-zA-Z0-9_]+$/;
        	if (!regx.test(pagename)){
        		alert("<fmt:message key='tooltip.pagename.check'/>");
        		return false;
        	}
        	
       		var test="typeof("+pagename+"_pagePortlets)!= 'undefined'";
       		if (eval(test)){
       			alert("<fmt:message key='tooltip.pagename.exist'/>");
       			return false;
  			}
  			return true;
        }
		function <portlet:namespace/>showdialog(title, value){
			var message="<fmt:message key='title.question'/>";
			$('#<portlet:namespace/>message').html(message.replace("%1", title));
			document.<portlet:namespace/>removeform.page.value=value;
			$("#<portlet:namespace/>dialog").dialog("open");
		}
		$(document).ready(function(){
			$("#<portlet:namespace/>tabs").tabs({selected:${selectindex}});
			$("#<portlet:namespace/>dialog").dialog({
				autoOpen:false,
				modal:true,
				width:400,
				postion:'center',
				height:247,
				title:"<fmt:message key='button.removepage'/>",
				buttons:{
					"<fmt:message key='button.cancel'/>":function(){
						$(this).dialog("close");
					},
					"<fmt:message key='button.confirm'/>":function(){
						$(this).dialog("close");
						document.<portlet:namespace/>removeform.submit();
					}					
				}
			});
		});
	</script>
<form action="<c:out value="${formActionUrl}"/>" method="POST"
	name="<portlet:namespace/>removeform">
	<input type="hidden" name="commandval" value="removepage" />
	<input type="hidden" name="page" />
</form>
<%-- Properties for link to app server deployer and help mode file --%>
<fmt:message key="appserver.deployer.url" var="deployerURL" />
<fmt:message key="appserver.deployer.help.page" var="deployerHelp" />

<portlet:renderURL portletMode="help" var="deployerhelpURL">
	<%-- needed el taglib to be able to use fmt:message value above --%>
	<portlet-el:param name="helpPage" value="${deployerHelp}" />
</portlet:renderURL>


</vwb:Permission>