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
<html>
	<head>
		<title><fmt:message key="error.page.title.500" /></title>
		<style>
			body.un404 {background:#eef;}
			.content {width:900px; margin:10px auto; padding:20px;}
			.csp_panel {height:300px;}
			.csp_panel .left {background:url(../images/500_girl.png) 0 0 no-repeat;margin-left:200px; height:200px; width:200px; float:left}
			.csp_panel .right {width:500px; float:left}
			h1 {color:#c00; text-shadow:2px 2px 2px #fff; font-size:48px; font-family:Arial "微软雅黑";}
			h3 {color:#333; font-family:Arial "微软雅黑"; margin:1em auto 3em;}
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
				<div class="left"></div>
				<div class="right">
					<h1><fmt:message key="error.code.500" /></h1>
					<h3><fmt:message key="error.code.500.describe" /></h3>
					<p class="errorHint"><a href="<%=url%>"><fmt:message key="error.page.goback" /></a></p>
				</div>
			</div>
		</div>
	</body>
</html>
