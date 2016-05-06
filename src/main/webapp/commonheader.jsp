<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%
pageContext.setAttribute("contextPath", request.getContextPath());
%>
<fmt:setBundle basename="templates.default" />

<%-- CSS stylesheet --%>
<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
            
<link rel="stylesheet" media="screen, projection, print" type="text/css"
     href="${contextPath}/css.css"/>
<link rel="stylesheet" type="text/css" media="print" href="${contextPath}/css.css" />
<vwb:IncludeResources type="stylesheet"/>
<vwb:IncludeResources type="inlinecss" />
<%-- JAVASCRIPT --%>

<script type="text/javascript" src="${contextPath}/scripts/page/pageName.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/ajax/ajax.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/ajax/comm-manager.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/js.js"></script>
     <script type="text/javascript"  src="${contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="${contextPath}/scripts/ajax/site.js"></script>

<script type="text/javascript">
	site.init('<vwb:BaseURL />');
//<![CDATA[

/* Localized javascript strings: LocalizedStrings[] */
<vwb:IncludeResources type="jslocalizedstrings"/>

var Wiki = {
	'BaseUrl': '<vwb:BaseURL />',
	'PageUrl': '<vwb:Link page="%23%24%25" format="url"/>',
	'TemplateDir': '${contextPath}/',
	'PageName': '<vwb:Variable key="pagename" />',
	'UserName': '<vwb:UserName />', 
	'JsonUrl' : '<vwb:Link jsp="JSON-RPC" format="url" context="plain"/>'
	};
	
var contextPath='${contextPath}';
	
<vwb:IncludeResources type="jsfunction"/>
//]]>
</script>
<script type="text/javascript" src="${contextPath}/scripts/DucklingCommon.js?ver=5.3.9"></script>
<vwb:RobotHeader/>

<vwb:Variable var="favicon" key="favicon" default="${contextPath}/images/favicon.ico"/>
<c:if test="${not empty favicon }">
	<link rel="shortcut icon" type="image/x-icon" href="${favicon}" />
</c:if>

<%-- SKINS : extra stylesheets, extra javascript --%>
<vwb:Variable var="skin" key="skin"/>

<c:if test='${skin!= null}'>
<link rel="stylesheet" type="text/css" media="screen, projection, print"
     href="${skin.webPath}/skin.css" />
<script type="text/javascript" src="${skin.webPath}/skin.js"></script>
</c:if>