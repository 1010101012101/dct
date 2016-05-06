<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ page import="cn.vlabs.duckling.vwb.*"%>
<%@ page import="cn.vlabs.duckling.vwb.ui.command.*"%>
<%
	VWBContext context = VWBContext.createContext(request,
			VWBCommand.ATTACH, null);
	String actionPath = "clbUploadsAction.do";
	String basePath = context.getBaseURL() + "/";
	String userVo = cn.vlabs.duckling.util.Utility
			.getVoDisplayName(context);
%>
<html>
<head>
<title>DucklingLink</title>
<vwb:Variable key="ddl.service.base" var="ddlbase" />
<script src="common/fck_dialog_common.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css"	href='../../../../scripts/DUI/autosearch/jquery.autocomplete.css' />
<script type="text/javascript"	src="${ddlbase}/scripts/jquery/jquery-1.8.3.min.js"></script>
<script type="text/javascript"	src='../../../../scripts/DUI/autosearch/jquery.autocomplete.js'></script>
<script src="fck_link/fck_Ducklinglink.js" type="text/javascript"></script>
<script type="text/javascript" src="swfobject/swfobject.js"></script>
<style>
.DisableDucklingLnkUrlInner {
	background-color: #dcdcdc;
	border: #c0c0c0 1px solid;
	color: #000000;
	cursor: default;
}
</style>
</head>
<body class="InnerBody">
<!-- 	<script type="text/javascript">
 	var baseurl='<%=basePath%>';
	function Flxonlad(){
		var flashvars = {
		  jsessionid: "<%=session.getId()%>",
		  contextpath: "<vwb:Link format='url' context='plain' jsp=''/>",
		  actionUrl:"<%=actionPath%>",
		  userVo: "<%=userVo%>",
		  lastpath: Y_GetCookie('lastPath'),
		  localeChain: "<%=request.getLocale()%>",
				pageName : oEditor.parent.Wiki.PageName,
				seleTxt : fckseleTxt
			};
			var params = {
				menu : "true"
			};
			var attributes = {
				id : "myDynamicContent",
				name : "myDynamicContent"
			};

			swfobject.embedSWF("dctclbclient.swf", "myContent", "100%", "100%",
					"9.0.0.0", "swfobject/expressInstall.swf", flashvars,
					params, attributes);

		}
		function isFireFox() {
			return navigator.userAgent.indexOf("Firefox") != -1;
		}
		function sayString(str) {
			alert(str);
		}
	</script> -->

	<div id="DucklingLnkPage" width="500px" style="DISPLAY: none">
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td class="DE_dialogboderstyle"><span
					fcklang="DMLPluginProValue" class="DE_dialogfontstyle"></span>
					<table cellspacing="0" cellpadding="0" width="100%" border="0"
						dir="ltr">
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td width="25%" nowrap="nowrap"><span fckLang="LinkPageName">Page
									Name</span><input type="hidden" class="inputText" id="ResourceId"
								value="" /></td>
							<td nowrap="nowrap" width="100%"><input type="text"
								class="inputText" name="parentPage" style="width: 100%;"
								id="txtDucklingLnkPage" value="" /></td>
						</tr>

					</table></td>
			</tr>
		</table>
		<fieldset class="DE_dialogboderstyle" style="text-align: center;">
			<legend class="DE_legendfontstyle" fcklang="LinkInformation"></legend>
			<table cellspacing="0" cellpadding="0" width="100%" border="0"
				dir="ltr">
				<tr>
					<td><br /></td>
					<td><br /></td>
				</tr>
				<tr>
					<td width="25%"><span fckLang="LinkText">Text</span></td>
					<td width="100%"><input id="DucklingLnkPageInner"
						style="WIDTH: 100%" type="text" /></td>
				</tr>
				<tr>
					<td><br /></td>
					<td><br /></td>
				</tr>
				<tr>
					<td><span fckLang="LinkTooltips">Tooltips</span></td>
					<td><input id="DucklingLnkPageTooltips" style="WIDTH: 100%"
						type="hidden" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><br /></td>
					<td><input id="DucklingLnkPageBlock" type="checkbox" /><span
						fckLang="LinkBlank">Open link in new window</span></td>
				</tr>
			</table>
		</fieldset>
	</div>
	<div id="DucklingLnkClb"
		style="DISPLAY: none; width: 450px; height: 350px;">
		<div id="myContent" style="z-index: 3">
			<jsp:include page="fck_ddlLink.jsp"/>
		</div>
	</div>
	<div id="DucklingLnkUrl" align="center" width="100%"
		style="DISPLAY: none">
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td class="DE_dialogboderstyle"><span
					fcklang="DMLPluginProValue" class="DE_dialogfontstyle"></span>
					<table cellspacing="0" cellpadding="0" width="100%" border="0"
						dir="ltr">
						<tr>
							<td width="25%" nowrap="nowrap"><span fckLang="DlgLnkProto">Protocol</span><br />
								<select id="cmbLinkProtocol" style="width: 80px">
									<option value="http://" selected="selected">http://</option>
									<option value="https://">https://</option>
									<option value="ftp://">ftp://</option>
									<option value="baseurl://">baseurl://</option>
									<!--option value="" fckLang="DlgLnkProtoOther">&lt;other&gt;</option>  -->
							</select></td>
							<td nowrap="nowrap" width="100%"><span fckLang="DlgLnkURL">URL</span><br />
								<input id="txtDucklingLnkUrl" style="WIDTH: 100%" type="text" />
							</td>
						</tr>
					</table></td>
			</tr>
		</table>
		<fieldset class="DE_dialogboderstyle" style="text-align: center;">
			<legend class="DE_legendfontstyle" fcklang="LinkInformation"></legend>
			<table cellspacing="0" cellpadding="0" width="100%" border="0"
				dir="ltr">
				<tr>
					<td><br /></td>
					<td><br /></td>
				</tr>
				<tr>
					<td width="25%"><span fckLang="LinkText">Text</span></td>
					<td width="100%"><input id="DucklingLnkUrlInner"
						style="WIDTH: 100%" type="text" /></td>
				</tr>
				<tr>
					<td><br /></td>
					<td><br /></td>
				</tr>
				<tr>
					<td><span fckLang="LinkTooltips">Tooltips</span></td>
					<td width="100%"><input id="DucklingLnkUrlTooltips"
						style="WIDTH: 100%" type="text" /></td>
				</tr>
				<tr>
					<td><br /></td>
					<td><br /></td>
				</tr>
				<tr>
					<td><br /></td>
					<td width="100%"><input id="DucklingLnkUrlBlock"
						type="checkbox" /><span fckLang="LinkBlank">Open link in
							new window</span></td>
				</tr>
			</table>
		</fieldset>
	</div>
	<div id="DucklingLnkEmail" style="DISPLAY: none">
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td class="DE_dialogboderstyle"><span
					fcklang="DMLPluginProValue" class="DE_dialogfontstyle"></span>
					<table cellspacing="0" cellpadding="0" width="100%" border="0"
						dir="ltr">
						<tr>
							<td><br /></td>
							<td><br /></td>
						</tr>
						<tr>
							<td width="25%" nowrap="nowrap"><span fckLang="LinkEMail">E-Mail</span>
							</td>
							<td nowrap="nowrap" width="100%"><input
								id="txtDucklingLnkEmail" style="WIDTH: 100%" type="text" /></td>
						</tr>
					</table></td>
			</tr>
		</table>
		<fieldset class="DE_dialogboderstyle" style="text-align: center;">
			<legend class="DE_legendfontstyle" fcklang="LinkInformation"></legend>
			<table cellspacing="0" cellpadding="0" width="104%" border="0"
				dir="ltr">
				<tr>
					<td><br /></td>
					<td><br /></td>
				</tr>
				<tr>
					<td width="25%"><span fckLang="LinkText">Text</span></td>
					<td width="100%"><input id="DucklingLnkEmailInner"
						style="WIDTH: 100%" type="text" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><span fckLang="LinkTooltips">Tooltips</span></td>
					<td width="100%"><input id="DucklingLnkEmailTooltips"
						style="WIDTH: 100%" type="text" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>
