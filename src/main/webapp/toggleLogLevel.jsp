<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="org.apache.log4j.*" %>
<%
	if ("127.0.0.1".equals(request.getRemoteAddr())){
		Logger rootLogger = Logger.getRootLogger();
		if (rootLogger.isDebugEnabled()){
			rootLogger.setLevel(Level.INFO);
		}else{
			rootLogger.setLevel(Level.DEBUG);
		}
	}
%>