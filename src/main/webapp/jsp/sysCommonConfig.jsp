<%@ page language="java" pageEncoding="utf-8"%>
<%@ page errorPage="/Error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>

<fmt:setBundle basename="templates.default" />

<form action="5006" method="post" name="sysCommonConfigActionForm">

	<input type="hidden" name="act" value="set" />
	<c:if test="${error!=null}">
		<div>
			<font color="red"><fmt:message key='${error}' /> </font>
		</div>
	</c:if>
	<table class="sitemodify" border="1" cellspacing="0" cellpadding="0"
		bordercolor="#c7c7c7">
		<tr class="tr_1">
			<td><fmt:message
					key='SysCommonConfigAction.applicationNameTitle' /></td>
		</tr>
		<tr class="tr_2">
			<td height="79"><fmt:message
					key='SysCommonConfigAction.applicationName' /> <label> <input
					type="text" size="40" name="applicationName"
					value="${SysCommonConfigForm.applicationName}"> <input
					type="submit" class="DuclingButton"
					value="<fmt:message key='updateFckAttach.ok'/>" />
			</label></td>
		</tr>
	</table>

	<table class="sitemodify" style="margin-Top: 30px;" border="1"
		cellspacing="0" cellpadding="0" bordercolor="#c7c7c7">
		<tr class="tr_1">
			<td><fmt:message key='SysCommonConfigAction.Managementsetpage' /></td>
		</tr>
		<tr class="tr_2">
			<td height="79"><label> <vwb:Link page='101'
						target='_toppageset'>
						<vwb:Param name="a">setting</vwb:Param>
						<vwb:Param name="method">show</vwb:Param>
						<fmt:message key='SysCommonConfigAction.Toppageset' />
					</vwb:Link> <br /> <vwb:Link target="_subscriptionpageset" page="16">
						<vwb:Param name="a">edit</vwb:Param>
						<fmt:message
							key='SysCommonConfigAction.Customsubscriptionenotificationmailtemplate' />
					</vwb:Link> <br /> <vwb:Link target="_emailtextpageset" page="18">
						<vwb:Param name="a">edit</vwb:Param>
						<fmt:message
							key='SysCommonConfigAction.Custompagesharingnotificationtemplate' />
					</vwb:Link>
			</label></td>
		</tr>

	</table>

</form>
<div class="clear"></div>