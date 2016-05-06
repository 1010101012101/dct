<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://portals.apache.org/pluto" prefix="pluto"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<pluto:isMaximized var="isMax" />

<!-- Left column -->
<c:choose>
	<c:when test="${isMax}">
		<div class="DragContainer" id="DragContainer"
			overclass="OverDragContainer">
			<div class=DragBox id=Item3 overclass="OverDragBox"
				dragclass="DragDragBox">
				<c:forEach var="portlet" varStatus="status"
					items="${currentPage.portletIds}">
					<c:set var="portlet" value="${portlet}" scope="request" />
					<jsp:include page="portlet-skin.jsp" />
				</c:forEach>
			</div>
		</div>
	</c:when>

	<c:otherwise>
		<div class="DCT_leftof2" id="c_left" overclass="OverDragContainer">
			<c:forEach var="portlet" varStatus="status"
				items="${currentPage.portletIds}" step="2">
				<div class="DragBox" id="Item1" overclass="OverDragBox"
					dragclass="DragDragBox">
					<c:set var="portlet" value="${portlet}" scope="request" />
					<jsp:include page="portlet-skin.jsp" />
				</div>
			</c:forEach>
		</div>

		<!-- Right column -->
		<div class="DCT_rightof2" id="c_right" overclass="OverDragContainer">
			<c:forEach var="portlet" varStatus="status"
				items="${currentPage.portletIds}" begin="1" step="2">
				<div class="DragBox" id="Item2" overclass="OverDragBox"
					dragclass="DragDragBox">
					<c:set var="portlet" value="${portlet}" scope="request" />
					<jsp:include page="portlet-skin.jsp" />
				</div>
			</c:forEach>
		</div>

	</c:otherwise>
</c:choose>
