<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="cn.vlabs.duckling.vwb.*" %>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="templates.default"/>
<%
   VWBContext context = VWBContext.getContext(request);
   String uctConfig = context.getProperty("duckling.uct.server"); 
   if (uctConfig!=null && uctConfig.trim().length()==0)
   		uctConfig = null;
   pageContext.setAttribute("contextPath", request.getContextPath());
 %>

<input type="hidden" id="info-username" name="info-username" value="">
<div id="info-commpopup" class="DCT_hideoutmenu" >
	<table cellspacing="0">
	<tr> <td onmouseover="javascript:samewithspan()" onmouseout="javascript:hidePopup()"><a id="homepage" href=""><fmt:message key='comm.info.homepage' /></a></td></tr>
	<%if (uctConfig != null) {%>
	<tr> <td onmouseover="javascript:samewithspan()" onmouseout="javascript:hidePopup()">
		<a id="sendinfo" href=""><fmt:message key='comm.info.sms' /></a><br />
	</td></tr>	
	<%} %>
	<tr> <td onmouseover="javascript:samewithspan()" onmouseout="javascript:hidePopup()"><a id="mailto" href=""><fmt:message key='comm.info.mailto' /></a>
	</td></tr>
	</table>
</div>

<div id="window-sendinfo" style="display: none; border: 1px solid rgb(0, 0, 0); padding: 5px; background-color: rgb(255, 255, 255); position: absolute; z-index: 11001; width: 350px; height: 230px; font-size: 14px; left: 300px; top: 300px;">
	<div id="TitleArea">
	<table width="100%"><tr><td align="left"><strong><fmt:message key='comm.info.sms' /></strong></td><td style='width:1px;'> <input type='button' onclick='closewindow();' title="<fmt:message key='comm.sms.close.hint' />" class='close' value="<fmt:message key='comm.sms.close' />" /></td></tr></table>
	</div>
	<table class="DCT_wikitable">
	<tr><td>
	<span><input type="checkbox" id="info-signature" name="info-signature" value="on"/><fmt:message key='comm.sms.signature' /></span>
	<!--
		<input id="info-signature" type="text" name="info-signature" value="" onpaste="return onCharsChange(this);" onKeyUp="return onCharsChange(this);"/>
	-->
	</td></tr>
	<tr><td>
	<fmt:message key='comm.sms.enter'>
		<fmt:param><font color="red"><span id="charsmonitor">200</span></font></fmt:param>
	</fmt:message>
	</td></tr>
	<tr><td>
		<textarea name="info-message" id="info-message" rows="4" cols="40" style="font-size:10pt" onpaste="return onCharsChange(this);" onKeyUp="return onCharsChange(this);"></textarea>
	</td></tr>
	<tr><td>
		<div align="center">
		<input type="button" id="info-send" name="info-send" value="<fmt:message key='comm.sms.send' />" onclick="javascript:sendSMSAjax();"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" id="info-cancel" name="info-cancel" value="<fmt:message key='comm.sms.cancel' />" onclick="javascript:closewindow();"/>
		</div>
	</td></tr>
	</table>
</div>

<div id="window-overwrite" style="display: none; border: 0pt none ; margin: 0pt; padding: 0pt; background-color: #FFFFFF; background-image: none; position: absolute; z-index: 11000; top: 0px; left: 0px; filter：alpha(opacity=20); width: 0px; height: 0px;"></div>

 <div onmouseover='Move_obj("dynamic_tip_check_pagename")' onmouseout="setto()" onmousemove="clearTimeoutO()" id="dynamic_tip_check_pagename" style="display:none;position:absolute;left:0px;top:0px;cursor:move;z-index:11999;border:1px solid #fb7;">
	   <div style=" height:20px; font-family:Verdana, Arial, Helvetica, sans-serif; font-size:12px; padding:3 5 0 5; color:red"><img src="${contextPath}/images/j_ico.gif"/>&nbsp;&nbsp;<fmt:message key="Error.tip" /></div>
   	   <div id="page_error_info"> </div>
</div>