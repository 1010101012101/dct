<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ page
	import="cn.vlabs.duckling.vwb.*,cn.vlabs.duckling.vwb.service.dpage.*,cn.vlabs.duckling.util.*"%>
<%@ page
	import="javax.servlet.jsp.jstl.fmt.*,cn.vlabs.duckling.vwb.service.viewport.ViewPort"%>
<%@ page import="java.util.Date"%>
<%@ page import="cn.vlabs.duckling.vwb.ui.UserNameUtil"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<script type="text/javascript">
	function submitRename(pagename) {
		var renameto = document.getElementById("renameto").value;
		if ((renameto == null) || (renameto.trim() == "")) {
			alert("info.renameto".localize());
		} else {
			if (pagename.toLowerCase().trim() == renameto.toLowerCase().trim()) {
				alert("info.samename".localize());

			} else {
				var pageNameRule = /^([a-zA-Z0-9]|[-_]|[^\x00-\xff]){1,255}$/;
				if (!pageNameRule.exec(renameto)) {
					alert("pagename.rules.tip".localize());
				} else {
					document.renameform.submit();
				}
			}
		}
	}
</script>
<%
	VWBContext c = VWBContext.getContext(request);
	ViewPort vp = VWBContext.getContainer().getViewPortService().getViewPort(c.getSite().getId(),
			c.getResource().getResourceId());
	DPage dPage = (DPage) c.getResource();
	//int attCount = c.getEngine().getAttachmentManager().listAttachments( c.getPage() ).size();
	int attCount = 0;
	String attTitle = LocaleSupport.getLocalizedMessage(pageContext,
			"attach.tab");
	if (attCount != 0)
		attTitle += " (" + attCount + ")";

	String creationAuthor = "";

	DPage firstPage = c.getPage();
	if (vp != null) {
		creationAuthor = UserNameUtil.getAuthorTip(c,
				vp.getCreator());
	}

	int itemcount = 0; //number of page versions
	try {
		itemcount = VWBContext
				.getContainer()
				.getDpageService()
				.getDpageVersionsByResourceId(c.getSite().getId(),
						dPage.getResourceId()).size(); /* highest version */
	} catch (Exception e) { /* dont care */
	}

	int pagesize = 20;
	int startitem = itemcount;
	String parm_start = (String) request.getParameter("start");
	if (parm_start != null)
		startitem = Integer.parseInt(parm_start);
	/*round to start of a pagination block */
	if (startitem > -1)
		startitem = ((startitem / pagesize) * pagesize) + 1;
	if (startitem > itemcount)
		startitem = ((startitem / pagesize - 1) * pagesize) + 1;
	pageContext.setAttribute("firstPage", firstPage);
	boolean bExists = c.resourceExists(dPage.getResourceId())
			&& dPage.getResourceId() < 65536;
	Date createTime = vp.getCreateTime();
%>
<c:set var="startitem" scope="request" value="<%=startitem%>" />
<c:set var="pagesize" scope="request" value="<%=pagesize%>" />
<c:set var="pageexist" scope="request" value="<%=bExists%>" />
<c:set var="createTime" scope="request" value="<%=createTime%>" />
<%-- PageExists --%>
<%-- part 1 : normal pages --%>
<vwb:PageExists>
	<p>
		<fmt:message key='info.lastmodified'>
			<fmt:param>
				<vwb:PageVersion>1</vwb:PageVersion>
			</fmt:param>
			<fmt:param>
				<a
					href="<vwb:DiffLink format='url' version='latest' newVersion='previous' />"
					title="<fmt:message key='info.pagediff.title' />"> <vwb:PageDate />
				</a>
			</fmt:param>
			<fmt:param>
				<vwb:Author />
			</fmt:param>
		</fmt:message>
	</p>

	<vwb:CheckVersion mode="notfirst">
		<p>
			<fmt:message key='info.createdon'>
				<fmt:param>
					<vwb:Link version="1">
						<vwb:VWBDate date="${createTime}" />
					</vwb:Link>
				</fmt:param>
				<fmt:param>
					<%=creationAuthor%>
				</fmt:param>
			</fmt:message>
		</p>
	</vwb:CheckVersion>
	<c:choose>

		<c:when test="${pageexist}">
			<fmt:message key="current.page.name" />
			<vwb:Variable key='pagetitle' />(<fmt:message
				key="system.reservation.page" />)
		</c:when>
		<c:otherwise>
			<vwb:Permission permission="delete">
				<form
					action="<vwb:Link format='url' context="VWBContext.DELETE" />?a=del"
					class="viewPageForm" id="deleteForm" method="post"
					onsubmit="return( confirm('<fmt:message key="info.confirmdelete"/>') && Wiki.submitOnce(this) );">
					<p>
						<input type="submit" name="delete-all" id="delete-all"
							value="<fmt:message key='info.delete.submit'/>">
					</p>
				</form>
			</vwb:Permission>
		</c:otherwise>
	</c:choose>


	<div class="collapsebox-closed" id="incomingLinks">
		<h4>
			<fmt:message key="info.tab.incoming" />
		</h4>
		<vwb:LinkTo>
			<vwb:PageTitle />
		</vwb:LinkTo>
		<%--FIXME   <vwb:Plugin plugin="ReferringPagesPlugin" args="before='*' after='\n' " /> --%>
	</div>

	<div class="collapsebox-closed" id="outgoingLinks">
		<h4>
			<fmt:message key="info.tab.outgoing" />
		</h4>
		<%--FIXME    <vwb:Plugin plugin="ReferredPagesPlugin" args="depth='1' type='local'" /> --%>
	</div>

	<div class="DCT_clearbox"></div>

	<%-- DIFF section --%>
	<vwb:CheckRequestContext context='diff'>
		<vwb:Include page="DiffTab.jsp" />
	</vwb:CheckRequestContext>
	<%-- DIFF section --%>


	<vwb:CheckVersion mode="first">
		<fmt:message key="info.noversions" />
	</vwb:CheckVersion>
	<vwb:CheckVersion mode="notfirst">
		<%-- if( itemcount > 1 ) { --%>

		<vwb:SetPagination start="<%=startitem%>" total="<%=itemcount%>"
			pagesize="<%=pagesize%>" maxlinks="9" fmtkey="info.pagination"
			href='<%=c.getBaseURL()%>' />

		<div class="zebra-table sortable table-filter">
			<table class="DCT_wikitable" width="100%" style="table-layout: fixed">
				<tr>
					<th
						style="width: 35px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
						<fmt:message key="info.version" />
					</th>
					<th
						style="width: 90px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
						<fmt:message key="info.date" />
					</th>
					<th
						style="width: 32px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
						<fmt:message key="info.size" />
					</th>
					<th
						style="width: 60px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
						<fmt:message key="info.author" />
					</th>
					<th
						style="width: 150px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
						<fmt:message key="info.changes" />
					</th>
					<th
						style="width: 20px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
						<fmt:message key="info.changenote" />
					</th>
				</tr>

				<vwb:HistoryIterator id="currentPage" start="<%=startitem%>"
					pagesize="<%=pagesize%>">
					<c:if
						test="${(startitem == -1) || ((currentPage.version >= startitem) && (currentPage.version < startitem + pagesize))}">
						<tr>
							<td
								style="width: 40px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
								<vwb:LinkTo
									version="<%=Integer.toString(currentPage
										.getVersion())%>">
									${currentPage.version}
								</vwb:LinkTo>
							</td>

							<td
								style="width: 80px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
								<vwb:PageDate page='${currentPage}' />
								<%
									//
								%>
							</td>
							<td
								style="width: 40px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
								<%--<fmt:formatNumber value='<%=Double.toString(currentPage.getSize()/1000.0)%>' groupingUsed='false' maxFractionDigits='1' minFractionDigits='1'/>&nbsp;Kb--%>
								${currentPage.size}
							</td>
							<td
								style="width: 80px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
								${currentPage.author}</td>

							<td
								style="width: 140px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
								<vwb:CheckVersion mode="notfirst" page="${currentPage}">
									<vwb:DiffLink version="current" newVersion="previous">
										<fmt:message key="info.difftoprev" />
									</vwb:DiffLink>
									<vwb:CheckVersion mode="notlatest" page="${currentPage}"> | </vwb:CheckVersion>
								</vwb:CheckVersion> <vwb:CheckVersion mode="notlatest" page="${currentPage}">
									<vwb:DiffLink version="latest" newVersion="current">
										<fmt:message key="info.difftolast" />
									</vwb:DiffLink>
								</vwb:CheckVersion>
							</td>

							<td
								style="width: 50px; align: left; overflow: hidden; white-space: nowrap; word-break: keep-all; text-overflow: ellipsis">
							</td>

						</tr>
					</c:if>
				</vwb:HistoryIterator>

			</table>
		</div>
						     ${pagination}
						    <%-- } /* itemcount > 1 */ --%>
	</vwb:CheckVersion>
</vwb:PageExists>

<vwb:NoSuchPage>
	<fmt:message key="common.nopage">
		<fmt:param>
			<vwb:EditLink>
				<fmt:message key="common.createit" />
			</vwb:EditLink>
		</fmt:param>
	</fmt:message>
</vwb:NoSuchPage>
