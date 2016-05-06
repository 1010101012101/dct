<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="cn.vlabs.duckling.vwb.VWBContext"%>
<%@taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<html>
<head>
<title>DDL Link</title>
<html>
<head>
<vwb:Variable key="ddl.service.base" var="ddlbase" />
<link href="${ddlbase}/scripts/csp/css/csp.ddl.css" rel="stylesheet"
	type="text/css" />
</head>
<body>
	<div id="resourceContainer"></div>

	<script type="text/javascript"
		src="${ddlbase}/scripts/jquery.tmpl.min.js"></script>
	<script type="text/javascript"
		src="${ddlbase}/scripts/csp/csp.ddl.resource.js"></script>

	<vwb:Variable key="ddlteam" var="ddlteam" />
	<vwb:Variable key="ddltoken" var="ddltoken" />
	<script type="text/javascript">
		var ddlList = {
			init:function(){
				this.list=ResourceList.create("resourceContainer", "${ddlteam}", "${ddltoken}", "${ddlbase}");
				this.list.load();
			},
			share:function(callback){
				this.list.sharing(callback);
			},
			isChecked:function(){
				return this.list.hasChecked();
			},
			getFileName:function(){
				var path=$("div#resourceContainer input:checked").val();
				var segments = path.split("/");
				return segments[segments.length-1];
			}
		};
		
		$(function(){
			ddlList.init();
		});
	</script>
</body>
</html>