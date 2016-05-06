<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="cn.vlabs.duckling.vwb.*"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ page import="cn.vlabs.duckling.vwb.VWBContext"%>
<%@ page import="cn.vlabs.duckling.vwb.service.auth.permissions.*"%>



<%
	pageContext.setAttribute("basePath", request.getContextPath());
	VWBContext context = VWBContext.createContext(request,
			VWBContext.ERROR);
	if (context
			.checkPermission(PermissionFactory.getPagePermission(
					VWBContext.getContainer().getResourceService()
							.getResource(context.getSite().getId(), 1),
					"view"))) {
		pageContext.setAttribute("accessMain", "true");
	}
	String frontPage = context.getFrontPage();
%>
<fmt:setBundle basename="CoreResources" />
<c:if test="${accessMain=='true'}">
	<meta http-equiv="refresh" content="3;url=<%=frontPage%>" />
</c:if>
<link rel="stylesheet" href="${basePath}/error/error.css" />
<table width="100%" border="0" align="center" cellpadding="0"
	cellspacing="0">
	<tr>
		<td height="61" align="center" valign="middle"></td>
	</tr>
	<tr align="center" valign="middle">
		<td>
			<table width="740" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center" valign="middle" class="box"><fmt:message
							key="<%=RandomTitle.randTitle()%>" /></td>
				</tr>
				<tr>
					<td height="400" align="center" valign="middle" class="box2">
						<table width="700" border="0" cellpadding="0" cellspacing="0">
							<tr>
								<td align="center" valign="middle" class="text">
									<table width="500" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td height="350" align="center" valign="top" class="box3">
												<fmt:message key="security.error.noaccess.logged" /> <vwb:Link
													jsp="logout" context="plain">
													<fmt:message key="security.error.noaccess.logout" />
												</vwb:Link>&nbsp;&nbsp; <c:if test="${accessMain=='true'}">
													<a href="<%=frontPage%>"> <fmt:message
															key="security.error.noaccess.home" />
													</a>
												</c:if>
											</td>
										</tr>
									</table>
								</td>
								<td width="128" align="center" valign="middle"><img
									src="${basePath}/images/Delete[1].png" alt="" width="128"
									height="128" /></td>
							</tr>


						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
