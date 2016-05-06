<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="cn.vlabs.duckling.vwb.*" %>
<%@ page import="cn.vlabs.duckling.vwb.ui.command.*" %>
<html>
	<head>
		<title>Upload Document</title>
		
		<script src="common/fck_dialog_common.js?version=dct4.5" type="text/javascript"></script>
		<script src="fck_select/fck_select.js" type="text/javascript"></script>
        <script type="text/javascript" src="swfobject/swfobject.js?version=dct4.5"></script>
	</head>
	<body class="InnerBody" >
	<%
	VWBContext vwbcontext = VWBContext.createContext(request,VWBCommand.ATTACH, null);	
	String basePath = vwbcontext.getBaseURL()+"/clbUploadsAction.do";
	%>
	<script type="text/javascript"> 
		var flashvars = {
		  jsessionid: "<%=session.getId()%>",
		  contextpath: "<%=vwbcontext.getBaseURL()%>/",
		  actionUrl:"<%=basePath%>",
		  lastpath: Y_GetCookie('lastPath'),
		  localeChain: "<%=request.getLocale()%>",
		  pageName: oEditor.parent.Wiki.PageName
		};
		var params ={
			menu: "true"
		};
		var attributes = {
		  id: "myDynamicContent",
		  name: "myDynamicContent"
		};
		
		swfobject.embedSWF("dctclbupload.swf", "myContent", "450", "150", "9.0.0.0","swfobject/expressInstall.swf", flashvars, params, attributes);
		function isFireFox(){
			return navigator.userAgent.indexOf("Firefox")!=-1;
		}
		function sayString(str){
 			alert(str);
		}
</script> 
<div id="myContent"  style="z-index:3" >
	<table width="100%" align="center"><tr><td height="400px" valign="middle">
	<table  style="background-color: #e8eefa; border: #C3D9FF 1px solid" id="links" cellpadding="0" align="center">
		<tr><td><h2><img src="images/alert.gif" alt="Alert" width="16" height="16">这个页面需要使用Flash插件才能正常查看。</h2></td></tr>
		<tr><td><a href="http://get.adobe.com/flashplayer/" target="_top">请从这里下载安装。</a></td></tr>
		<tr><td><a href="http://get.adobe.com/flashplayer/" class="noHover" target="_top"><img src="images/get_flash_player.gif" alt="下载Flash插件" border="0"></a></td></tr>
	</table>
	</td></tr></table>
	</div>
	</body>

</html>
