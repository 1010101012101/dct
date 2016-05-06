<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="cn.vlabs.duckling.vwb.VWBContext" %>
<%
	pageContext.setAttribute("info", VWBContext.getContainer().getLoginSessionService().getCurrentLoginContext(session.getId()));
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>LocalLogin</title>
	<meta http-equiv="pragma" content="no-cache"/>
	<meta http-equiv="cache-control" content="no-cache"/>
	<meta http-equiv="expires" content="0"/>
  </head>
  <body>
  	<form action="${info.ssoUrl}" method="post" name="umtform">
  		<div>
  		<input type="hidden" name="act" value="Validate"/>
  		<input type="hidden" name="loginByDeputy" value="true"/>
  		<input type="hidden" name="username" value="${info.userName}"/>
  		<input type="hidden" name="password" value="${info.password}"/>
  		</div>
  	</form>
	<script type="text/javascript">
		document.umtform.submit();
	</script>
  </body>
</html>
