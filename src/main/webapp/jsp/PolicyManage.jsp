<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<fmt:setBundle basename="templates.default" />

<%
	pageContext.setAttribute("basepath", request.getContextPath());
%>

<link type="text/css"
	href="${basepath}/scripts/jquery/cupertino/jquery-ui-1.7.2.custom.css"
	rel="stylesheet" />
<!--link type="text/css"
	href="${basepath}/scripts/DUI/autosearch/jquery.autocomplete.css"
	rel="stylesheet" /-->
<style>
#policyclue {
	background-color: #E4F1FB;
	border: 1px solid #00457b;
	padding: 8px;
	font-size: .85em;
}
.policyBtn {
	border: 0px; 
	color: white; 
	font: bold; 
	cursor: hand; 
	width: 60px; 
	height: 30px; 
}
</style>

<!--  script type="text/javascript"
	src="${basepath}/scripts/jquery/jquery-1.3.2.min.js"></script>-->
<script type="text/javascript"
	src="${basepath}/scripts/jquery/jquery-ui-1.7.2.custom.min.js"></script>
<script type="text/javascript" src="${basepath}/scripts/DUI/autosearch/jquery.autocomplete.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$('#adddlg').dialog({
					autoOpen: false,
					modal:true,
					width: 600
		});
		
		$('#previewdlg').dialog({
					autoOpen: false
					,modal:true
					,width: 800
					,height:400
		});
		
		$('#template').change(function(){
			var template = document.getElementById("template");
			var index = template.selectedIndex;
			if(index==1){
				templatesubmit(1);
			}
			if(index==2){
				templatesubmit(2);
			}
		});
		
		$('#addPermission').change(function(){
			var addPermission = document.getElementById("addPermission");
			var index = addPermission.selectedIndex;
			if(index==0){
				showAddResource();
				showPageActions();
			}
			if(index==1){
				hideAddResource();
				showVWBActions();
			}
		});
		
		$("#addResourceContentAjax").autocomplete("<vwb:Link format='url' jsp='allresource.do' context='plain'/>",
		{
			parse: function(data) {
				return $.map(data, function(row) { 
					return {data: row, value: row.title, m_value :row.id}
				}); 
			}, 
			formatItem: function(row, i, max) {
				return "<td>" + row.title+ "(" + row.id +")</td>";
			},
			extraParams: {
				query:function(){
					return $('#addResourceContentAjax').val();
				}, 
				datatype:"json"
			},
			dataType: "json",
			autoFill: true,
			scroll:true,
			scrollHeight:100,
			delay:10,  
			max:100,
			onSelected:ajaxSelect
			//onEsc:otheresc//esc后事件无参数
		});
		
		var showPreview = <%=request.getAttribute("showPreview") %>;
		if(showPreview=="show"){
			showPreviewDialog();
		}
	});
	
	function ajaxSelect(row) {
		$("#addResourceContent").val(row.m_value);
	}

	function addsubmit()
	{
		$('#adddlg').dialog('open');
		return false;
	}
	function closeAddPolicyDialog(){
		$("#adddlg").dialog('close');
	}
	function showPreviewDialog()
	{
		$('#previewdlg').dialog('open');
		return false;
	}
	function closePreviewDialog(){
		$("#previewdlg").dialog('close');
	}
	
	function templatesubmit(itemId)
	{
		var delid = document.getElementById("templateId");
	 	delid.value=itemId;
	 	var form = document.getElementById("templatePolicyManage");
		
	 	form.submit();
	 	
	}
	
	function savesubmit()
	{
	 	var form = document.getElementById("savePolicyManage");
	 	form.submit();
	 	
	}
	
	function delsubmit(itemId)
	{
		var delid = document.getElementById("delPolicyId");
	 	delid.value=itemId;
	 	var form = document.getElementById("delPolicyManage");
		
	 	form.submit();
	 	
	}
	
	function showPageActions(){
		document.getElementById("addOperation4VWB").style.display="none";
		document.getElementById("addOperation4Page").style.display="";
	}
	function showVWBActions(){
		document.getElementById("addOperation4Page").style.display="none";
		document.getElementById("addOperation4VWB").style.display="";
	}
	function showAddResource(){
		document.getElementById("addResource4Page").style.display="";
	}
	function hideAddResource(){
		document.getElementById("addResource4Page").style.display="none";
	}
	
	function enableResourceContent() {
		document.getElementById("addResourceContentAjax").disabled = false;
	}
	function disableResourceContent() {
		document.getElementById("addResourceContentAjax").disabled = true;
	}
	
	function policyNotify(content){
		document.getElementById("policyNotify").innerHTML = content;
	}
	
</script>

<h3>
	<fmt:message key="policy.manage.title" />
</h3>
<div>
	<table width="98%">
	<tr><td></td><td colspan=2 align=right>(<a href="5025?method=preview"><fmt:message key="policy.manage.link.preview" /></a>)</td></tr>
		<tr>
			<td>
				<div id="policyNotify" style="color:${sessionScope.PolicyNotifyColor} ; text-align: left">
					${sessionScope.PolicyNotify}
				</div>
			</td>
			<td width="10%">
				<input type="button" value="<fmt:message key="policy.manage.add" />" class="policyBtn"
					style="background-image: url('${basepath}/scripts/uploadify/blank.png')"
					onClick="addsubmit();" />
			</td>
			<td width="20%" align="right">
				<table>
					<tr>
						<td>
							<select id="template" name="template">
								<option value="">
								<fmt:message key="policy.manage.template.option1" />
								</option>
								<option value="1">
								<fmt:message key="policy.manage.template.option2" />
								</option>
								<option value="2">
								<fmt:message key="policy.manage.template.option3" />
								</option>
							</select>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td colspan="3">
				<div id="policycontent1">
					<table class="DCT_wikitable">
						<tr>
							<th>
								<fmt:message key="policy.manage.principal" />
							</th>
							<th>
								<fmt:message key="policy.manage.permission" />
							</th>
							<th>
								<fmt:message key="policy.manage.actions" />
							</th>
							<th>
								<fmt:message key="policy.manage.resource" />
							</th>
							<th>
								<fmt:message key="policy.manage.operation" />
							</th>
						</tr>
						<c:forEach var="item" items='${sessionScope.PData}'
							varStatus="status">
							<tr>
								<td>
									<c:out value='${item.principal}' />
								</td>
								<td>
									<c:out value='${item.permission}' />
								</td>
								<td>
									<c:out value='${item.operation}' />
								</td>
								<td>
									<c:out value='${item.resource}' />
								</td>
								<td>
									<button onclick="delsubmit('${item.id}');">
										<img src='${basepath}/images/common/unsubscribe.png' />
									</button>
									<fmt:message key="policy.manage.cancel" />
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td>
				<input type="button" name="saveSubmit" value="<fmt:message key="policy.manage.submit" />" onclick="savesubmit();">
				<input type="button" name="resetSubmit" value="<fmt:message key="policy.manage.reset" />" onclick="templatesubmit(0);">
			</td>
		</tr>
	</table>
</div>

<form id="templatePolicyManage" action="5025" method="post"
	name="policyManageForm">
	<input type="hidden" name="method" id="method" value="template" />
	<input type="hidden" name="templateId" id="templateId" value="" />
</form>

<form id="delPolicyManage" action="5025" method="post"
	name="policyManageForm">
	<input type="hidden" name="method" id="method" value="delete" />
	<input type="hidden" name="delPolicyId" id="delPolicyId" value="" />
</form>

<form id="savePolicyManage" action="5025" method="post"
	name="policyManageForm">
	<input type="hidden" name="method" id="method" value="save" />
</form>


<div id="adddlg" title="<fmt:message key="policy.manage.dlalog.add" />">
<form id="addPolicyManage" action="5025" method="post"
	name="policyManageForm">
	<input type="hidden" name="method" id="method" value="add" />
	<table>
		<tr>
			<td>
				<fmt:message key="policy.manage.dlalog.add.principal" />
			</td>
			<td colspan="2">
				<select id="addPrincipal" name="addPrincipal">
					<option value="All">
						<fmt:message key="policy.manage.dlalog.add.principal.option1" />
					</option>
					<option value="VO">
						<fmt:message key="policy.manage.dlalog.add.principal.option2" />
					</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<fmt:message key="policy.manage.dlalog.add.permission" />
			</td>
			<td>
				<select id="addPermission" name="addPermission">
					<option value="Page">
						<fmt:message key="policy.manage.dlalog.add.permission.option1" />
					</option>
					<option value="VWB">
						<fmt:message key="policy.manage.dlalog.add.permission.option2" />
					</option>
				</select>
			</td>
			<td>
				<div id="addResource4Page" style="display: ">
					<input id="addResource" name="addResource" type="radio" value="All" onclick="disableResourceContent();" checked><fmt:message key="policy.manage.dlalog.add.resource.option1" />
					<input id="addResource" name="addResource" type="radio" value="Custom" onclick="enableResourceContent();"><fmt:message key="policy.manage.dlalog.add.resource.option2" />
					
					<input id="addResourceContent" name="addResourceContent" type="hidden"/>
					<input id="addResourceContentAjax" name="addResourceContentAjax" type="text" value="" size="20" maxlength="45" disabled/>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<fmt:message key="policy.manage.dlalog.add.actions" />
			</td>
			<td colspan="2">
				<div id="addOperation4Page" style="display: ">
					<input id="addOperation1" name="addOperation1" type="radio" value="view" checked>
					<fmt:message key="policy.manage.dlalog.add.actions.option1" />
					<input id="addOperation1" name="addOperation1" type="radio" value="edit">
					<fmt:message key="policy.manage.dlalog.add.actions.option2" />
				</div>
				<div id="addOperation4VWB" style="display: none ">
					<input id="defaultAction2" name="defaultAction2" type="radio" value="login" checked><fmt:message key="policy.manage.dlalog.add.actions.option3" />
					<input id="addOperation2" name="addOperation2" type="checkbox" value="upload"><fmt:message key="policy.manage.dlalog.add.actions.option4" />
					<input id="addOperation2" name="addOperation2" type="checkbox" value="portlet"><fmt:message key="policy.manage.dlalog.add.actions.option5" />
					<input id="addOperation2" name="addOperation2" type="checkbox" value="search"><fmt:message key="policy.manage.dlalog.add.actions.option6" />
					<input id="addOperation2" name="addOperation2" type="checkbox" value="editProfile"><fmt:message key="policy.manage.dlalog.add.actions.option7" />
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="3">
				<input id="addsubmit" name="addsubmit" type="submit" value="<fmt:message key="policy.manage.dlalog.add.add" />" />
				<input id="cancelbtn" name="cancelbtn" type="button" value="<fmt:message key="policy.manage.dlalog.add.cancel" />" onclick="closeAddPolicyDialog();"/>
			</td>
		</tr>
	</table>
</form>
</div>

<div id="previewdlg" title="<fmt:message key="policy.manage.dlalog.preview" />" style="overflow:hidden;">
	<div style="width:100%; height:100%; overflow:scroll; border:1px solid #CCCCCC;">
		<pre><code><c:out value='${PolicyStr}'/></code></pre>
	</div>
</div>
