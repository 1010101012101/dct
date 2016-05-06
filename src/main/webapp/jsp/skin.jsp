<%@ page language="java" pageEncoding="utf-8"%>
<%@ page import="cn.vlabs.duckling.vwb.VWBContext"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<%
	//Preferences.setupPreferences(pageContext);
	pageContext.setAttribute("basepath", request.getContextPath());
	
	VWBContext context = VWBContext.getContext(request);
	pageContext.setAttribute("currentSkin", context.getContainer().getSkinService().getCurrentSkin(context.getSiteId()));
%>
<script type="text/javascript">
<!--
	function applySkin(skin, shared,available){
		if(!available){
			alert('<fmt:message key="skin.template.notAvailable" />');
			return;
		}
		document.skinform.skinname.value=skin;
		document.skinform.shared.value=shared;
		document.skinform.submit();
	}
	var message='${message}';
	var errors=new Array();
	errors['empty']='<fmt:message key="skin.skinname.empty"/>';
	errors['invalidchar']='<fmt:message key="skin.skinname.invalidchar"/>';
	errors['toolong']='<fmt:message key="skin.skinname.toolong"/>';
	errors['exist']='<fmt:message key="skin.skinname.exist"/>';
	errors['unreachable']='<fmt:message key="skin.service.unreachable"/>';
	errors['info']='<fmt:message key="skin.errormessage.info"/>';
	errors['needzip']='<fmt:message key="skin.errormessage.needzip"/>';
	errors['override']='<fmt:message key="skin.errormessage.override"/>';
	errors['inuse']='<fmt:message key="skin.errormessage.inuse"/>';
	errors['confirmdelete']='<fmt:message key="skin.errormessage.remove"/>';
	function checkSkinname(input){
		if (document.uploadform.elements[0].value=="upload"){
		Ext.Ajax.request({
			url:'<vwb:Link page="5002" format="url"/>',
			params:{method:'check', skinname:input.value},
			success: function(resp,opts){
				var respText = resp.responseText;
				if (respText!='OK'){
					if (respText=='exist'){
						exist=true;
					}else{
						ok=false;
					}
					Ext.get("skinerror").dom.innerHTML=errors[respText];
				}else{
					exist=false;
					ok=true;
					Ext.get("skinerror").dom.innerHTML="";
				}
			},
			failure:function(resp, opts){
				Ext.Msg.alert(errors['info'], errors['unreachable']);
			}
		});
		}
	}
	function checkFileType(value){
		return (/.*\.[zZ][Ii][Pp]$/.test(value));
	}
	var ok=true;
	var exist=false;
	function doSubmit(){
		if (!ok){
			Ext.Msg.alert(errors['info'], Ext.get('skinerror').dom.innerHTML);
			return;
		}
		if ( document.uploadform.skinname.value==null ||  document.uploadform.skinname.value==""){
			Ext.Msg.alert(errors['info'], errors['empty']);
			return;
		}
		if (!checkFileType(document.uploadform.file.value)){
			Ext.Msg.alert(errors['info'], errors['needzip']);
			return;
		}
		if(adminSite){
			document.uploadform.shared.disabled=false;
			document.uploadform.template.disabled=false;
		}
		if (exist){
			Ext.Msg.confirm(errors['info'], errors['override'], function(button, text){
				if (button=="yes"){
					document.uploadform.elements[0].value='update';
					document.uploadform.submit();
				};
			} );
			return;
		}else
			document.uploadform.submit();
	}
	
	
//-->
</script>
<link rel="stylesheet" type="text/css"
	href="${basepath}/scripts/extjs/resources/css/ext-all.css" />
<script type="text/javascript"
	src="${basepath}/scripts/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript"
	src="${basepath}/scripts/extjs/ext-all.js"></script>
<script type="text/javascript"
	src="${basepath}/scripts/ajax/skintable.js"></script>
<div class="dct_border">
	<div class="dct_content">
		<div class="dct_title">
			<table width="100%">
				<tr>
					<td align="left">
						<fmt:message key="skin.title" />
					</td>
					<td align="right">
						<a href="<%=request.getContextPath()%>/skin.zip"> <fmt:message key="skin.download" />
						</a>
						<span class="dct_vline">&nbsp;</span>
						<input type="button" id="uploadbutton"
							value="<fmt:message key='skin.upload'/>" />
					</td>
				</tr>
			</table>
		</div>
		<form action="<vwb:Link page="5002" format="url"/>" method="post" name="skinform">
			<input type="hidden" name="method" value="apply" />
			<input type="hidden" name="skinname" />
			<input type="hidden" name="shared"/>
			<table id="skinbox" width="100%">
				<c:set var="count" value="0" />
				<c:forEach varStatus="status" items="${skins}" step="2">
					<tr>
						<c:forEach var="skin" items="${skins}" varStatus="innerStatus"
							begin="${status.index}" end="${status.index+1}">
							<c:if test="${skin.name==currentSkin.name && skin.shared==currentSkin.shared}">
								<c:set var="selectclass" value="_dct_skin_selected" />
							</c:if>
							<c:if test="${skin.shared}">
								<td class="${selectclass} shared" onclick="applySkin('${skin.name}', ${skin.shared},${skin.available })"
									id="gskin${innerStatus.index}" shared="true" skinname="${skin.name}" template='${skin.template }' skinzip="<vwb:Link format="url" context='attach' docId='${skin.clbId}'/>"
									ext:qtip="<fmt:message key='skin.update.tooltip'/>"
									align="center">
									<table>
										<tr>
											<td>
												<img src="${skin.thumb}" width="200px"
													height="150px" />
											</td>
										</tr>
										<tr>
											<td valign="top" align="center">
												${skin.name}
											</td>
										</tr>
									</table>
								</td>
							</c:if>
							<c:if test="${!skin.shared}">
								<td class="${selectclass}" onclick="applySkin('${skin.name}', ${skin.shared},${skin.available })"
									id="skin${innerStatus.index}" shared="false" skinname="${skin.name}" template='${skin.template }'  
									skinzip="<vwb:Link format='url' context='attach' docId='${skin.clbId}'/>"  ext:qtip="<fmt:message key='skin.update.tooltip'/>"
									align="center">
									<table>
										<tr>
											<td>
												<img src="${skin.thumb}" width="200px"
													height="150px" />
											</td>
										</tr>
										<tr>
											<td valign="top" align="center">
												${skin.name}
											</td>
										</tr>
									</table>
								</td>
							</c:if>
							<c:set var="selectclass" value="" />
							<c:if
								test="${status.last && innerStatus.last && (innerStatus.index+1) mod 2 ==1}">
								<td>
									&nbsp;
								</td>
							</c:if>
							<c:if test="${status.last && innerStatus.last}">
								<c:set var="count" value="${innerStatus.index+1}" />
							</c:if>
						</c:forEach>
					</tr>
				</c:forEach>
			</table>
		</form>
		<div style="display:none" id="upload">
			<form name="uploadform" action="<vwb:Link page="5002" format="url"/>" method="post"
				enctype="multipart/form-data">
				<input type="hidden" name="method" value="upload" />
				<table class="_d_table">
					<tr>
						<td align="right" valign="bottom">
							<fmt:message key="skin.skinname" />
						</td>
						<td align="left" valign="bottom">
							<input type="text" name="skinname" style="width:200px;"
								onchange="checkSkinname(this)" />
							<c:if test="${isAdminSite}">
								<input type="checkbox" name="shared" value="true" onClick="clickSharedBtn(this)"/><fmt:message key="skin.shared" />
							</c:if>
						</td>
					</tr>
				<c:if test="${isAdminSite}">
					<tr id="templateTr" style="display:none">
						<td align="right" valign="bottom">
							<fmt:message key="skin.templatename" />
						</td>
						<td align="left" valign="bottom">
								<select id="template" name="template">
							 	 <c:forEach var="item" items='${requestScope.templates}' varStatus="status">
							       <option value="${item}">
								       <c:out value="${item}"/>
								   </option>
							  </c:forEach>
							</select>
						</td>
					</tr>
				</c:if>
					<tr>
						<td align="right" valign="middle">
							<fmt:message key="skin.file" />
						</td>
						<td valign="middle" align="left">
							<input type="file" name="file" size="20" style="width:300px;" />
						</td>
					</tr>
					<tr>
						<td colspan="2" id="skinerror" style="color:red;text-align:left;"></td>
					</tr>
					<tr>
						<td colspan="2" align="right">
							<input name="submitbutton" type="button"
								value="<fmt:message key='skin.submit'/>" onclick="doSubmit()" />
							<input type="button" value="<fmt:message key='skin.cancel'/>"
								onclick="upload.hide()" />
						</td>
					</tr>
				</table>
			</form>
		</div>
		<form name="removeform" action="<vwb:Link page="5002" format="url"/>" method="post">
			<input type="hidden" name="method" value="remove" />
			<input type="hidden" name="shared" value="false"/>
			<input type="hidden" name="skinname" />
		</form>
		<script type="text/javascript">
	function clearAll(){
		document.uploadform.reset();
		Ext.get("skinerror").dom.innerHTML="";
	}
	function showUpdate(e){
		clearAll();
		document.uploadform.elements[0].value='update';
		document.uploadform.skinname.value=skinname;
		if(adminSite){
			if ("true"==shared){
				document.uploadform.shared.checked=true;
			}else{
				document.uploadform.shared.checked=false;
			}
			document.uploadform.template.value=template;
			clickSharedBtn(document.uploadform.shared);
			document.uploadform.shared.disabled=true;
			document.uploadform.template.disabled=true;
		}
		document.uploadform.skinname.readOnly=true;
		Ext.get("upload").show();
		upload.show();
	}
	function removeSkin(){
		Ext.Msg.confirm(errors['info'], errors['confirmdelete'].replace("%1",skinname), function(button, text){
			if (button=="yes"){
				document.removeform.skinname.value=skinname;
				document.removeform.shared.value=shared;
				document.removeform.submit();
			};
		} );
	}
	function downloadSkin(){
		window.location.href= skinwebpath;
		
	}
	function showskinmenu(event, el,  skinname){
		event.cancelBubble = true;
		skinmenu.showAt(el, "tr-br");
		return false;
	}
	var box = new SkinBox("skinbox");
	var upload = new Ext.Window({
			contentEl:"upload",
			//width:400,
			//height:247,
			title:'<fmt:message key="skin.uploadtitle" />',
			modal:true,
			closeAction:'hide',
			animateTarget:'uploadbutton',
			resizable:true
		});
	var adminSite =${isAdminSite};
	var skinmenu;
	var skinname;
	var shared;
	var template;
	var skinwebpath;
	function clickSharedBtn(checkbox){
		if(checkbox.checked){
			document.getElementById('templateTr').setAttribute("style", "display:table-row");
		}else{
			document.getElementById('templateTr').setAttribute("style", "display:none");
		}
	}
	
	Ext.onReady(function(){
		skinmenu = new Ext.menu.Menu({
		items:[
			{icon:"${basepath}/images/s.gif",text:"<fmt:message key='skin.updatetitle'/>", handler:showUpdate},
			{icon:"${basepath}/images/s.gif",text:"<fmt:message key='skin.remove'/>", handler:removeSkin},
			{icon:"${basepath}/images/s.gif",text:"<fmt:message key='skin.downloadskin'/>", handler:downloadSkin}
		]});
		
		sharedSkinMenu = new Ext.menu.Menu({
		items:[
			{icon:"${basepath}/images/s.gif",text:"<fmt:message key='skin.downloadskin'/>", handler:downloadSkin}
		]});
		
		Ext.QuickTips.init();
		Ext.get("uploadbutton").on("click",function(){
			Ext.get("upload").show();
			clearAll();
			upload.show();
			document.uploadform.elements[0].value='upload';
			document.uploadform.skinname.readOnly=false;
			if(adminSite){
				document.uploadform.shared.disabled=false;
				document.uploadform.template.disabled=false;
				document.getElementById('templateTr').setAttribute("style", "display:none");
			}
			document.uploadform.skinname.focus();
		});
		
		var count = ${count};
		for (var i=0;i<count;i++){
			var skin = Ext.get("skin"+i);
			if (skin){
				skin.on("contextmenu", function(e){
					skinname=this.dom.attributes['skinname'].value;
					shared = this.dom.attributes['shared'].value;
					skinwebpath=this.dom.attributes['skinzip'].value;
					template=this.dom.attributes['template'].value;
					e.preventDefault();
					skinmenu.showAt(e.getPoint()); 
				});
			};
		}
		
		for (var i=0;i<count;i++){
			var skin = Ext.get("gskin"+i);
			if (skin){
				skin.on("contextmenu", function(e){
					skinname=this.dom.attributes['skinname'].value;
					shared = this.dom.attributes['shared'].value;
					skinwebpath=this.dom.attributes['skinzip'].value;
					template=this.dom.attributes['template'].value;
					e.preventDefault();
					if (!adminSite){
						sharedSkinMenu.showAt(e.getPoint());
					}else{
						skinmenu.showAt(e.getPoint());
					}
				});
			};
		}
		
		if (errors[message]!=null){
			Ext.Msg.alert(errors['info'], errors[message]);
		};
	});
</script>
	</div>
</div>
