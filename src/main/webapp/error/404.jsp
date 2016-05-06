<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page import="cn.vlabs.duckling.vwb.*" %>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%
VWBContext context = VWBContext.getContext(request);
String url ;
if (context!=null){
	url=context.getFrontPage();
}else{
	url=request.getContextPath(); 
}
%>
		<fmt:setBundle scope="page" basename="CoreResources" />
		<meta http-equiv="refresh" content="3;url=<%=url%>"/>
		<link rel="stylesheet" href="${contextPath}/error/error.css"/>
		
<html>
	<head>
		<title><fmt:message key="error.page.title.404" /></title>
		<style>
			body.un404 {background:#eef;}
			.content {width:900px; margin:10px auto; padding:20px;}
			.csp_panel {background:url(../images/404_bg.png) center center no-repeat; height:310px;}
			h1 {color:#f90; font-size:48px; text-align:center;font-family:Arial "微软雅黑"; padding-top:1em}
			h3 {text-align:center; color:#333; font-family:Arial "微软雅黑"; margin:1em auto 3em;}
			p.errorHint {text-align:center;}
			p.errorHint > a {
				background:#eef; border:1px solid #ccc; padding:8px 15px; text-decoration:none; color:#08c;
				border-radius:3px; font-size:14px; font-weight:bold;
			}
			p.errorHint > a:hover {background:#fff;}
		</style>
	</head>
	<body class="un404">
		<div class="content">
			<div class="csp_panel">
				<h1><fmt:message key="error.code.404" /></h1>
				<h3><fmt:message key="error.code.404.describe" /></h3>
				<p class="errorHint"><a href="<%=url%>"><fmt:message key="error.page.goback" /></a></p>
			</div>
		</div>
	</body>
</html>		
		

<%
	if (context==null){
		HttpSession newSession = request.getSession(false);
		if (newSession!=null){
			//Remove session created by EL tag.
			VWBFilter.removeGlobalCookie(request, response, newSession);
		}
	}
%>
