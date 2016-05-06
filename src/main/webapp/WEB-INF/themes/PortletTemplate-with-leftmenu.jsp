<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://portals.apache.org/pluto" prefix="pluto"%>
<div id="pagecontent">

	<pluto:isMaximized var="isMax" />

	<!-- Left column -->
	<c:choose>
		<c:when test="${isMax}">
			<c:forEach var="portlet" varStatus="status"
				items="${currentPage.portletIds}">
				<c:set var="portlet" value="${portlet}" scope="request" />
				<jsp:include page="portlet-skin.jsp" />
			</c:forEach>
		</c:when>

		<c:otherwise>
			<div id="portlets-left-column">
				<c:forEach var="portlet" varStatus="status"
					items="${currentPage.portletIds}" step="2">
					<c:set var="portlet" value="${portlet}" scope="request" />
					<jsp:include page="portlet-skin.jsp" />
				</c:forEach>
			</div>

			<!-- Right column -->
			<div id="portlets-right-column">
				<c:forEach var="portlet" varStatus="status"
					items="${currentPage.portletIds}" begin="1" step="2">
					<c:set var="portlet" value="${portlet}" scope="request" />
					<jsp:include page="portlet-skin.jsp" />
				</c:forEach>
			</div>

		</c:otherwise>
	</c:choose>
	<div class="DCT_clear">
	</div>
</div>