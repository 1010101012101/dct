<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="cn.vlabs.duckling.vwb.*"%>
<%@ page import="cn.vlabs.duckling.vwb.service.dpage.DPage"%>
<fmt:setBundle basename="templates.default" />

<vwb:PageExists>
	<%
		VWBContext c = VWBContext.getContext(request);
			DPage p = (DPage) c.getResource();
			int resourceid = p.getResourceId();
	%>

	<%-- If the page is an older version, then offer a note and a possibility
     to restore this version as the latest one. --%>
	<vwb:CheckVersion mode="notlatest">
		<form action="<vwb:LinkTo format='url'/>" method="get"
			accept-charset='UTF-8'>
			<div class="warning">
				<fmt:message key="view.oldversion">
					<fmt:param>
						<%--<wiki:PageVersion/>--%>
						<select id="version" name="version" onchange="this.form.submit();">
							<%
								int latestVersion = c
														.getContainer()
														.getDpageService()
														.getDpageVersionContent(
																c.getSite().getId(), resourceid,
																VWBContext.LATEST_VERSION)
														.getVersion();
												int thisVersion = p.getVersion();

												if (thisVersion == VWBContext.LATEST_VERSION)
													thisVersion = latestVersion; //should not happen
												for (int i = 1; i <= latestVersion; i++) {
							%>
							<option value="<%=i%>"
								<%=((i == thisVersion) ? "selected='selected'"
										: "")%>><%=i%></option>
							<%
								}
							%>
						</select>
					</fmt:param>
				</fmt:message>
				<br />
				<vwb:LinkTo>
					<fmt:message key="view.backtocurrent" />
				</vwb:LinkTo>
				&nbsp;&nbsp;
				<vwb:EditLink version="this">
					<fmt:message key="view.restore" />
				</vwb:EditLink>
			</div>

		</form>
	</vwb:CheckVersion>

	<%-- Inserts no text if there is no page. --%>
	<vwb:render content="${RenderContext.content}" />
</vwb:PageExists>
<vwb:NoSuchPage>
	<%-- FIXME: Should also note when a wrong version has been fetched. --%>
	<div class="information">
		<fmt:message key="common.nopage">
			<fmt:param>
				<vwb:EditLink>
					<fmt:message key="common.createit" />
				</vwb:EditLink>
			</fmt:param>
		</fmt:message>
	</div>
</vwb:NoSuchPage>