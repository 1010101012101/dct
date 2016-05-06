<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<fmt:setBundle basename="templates.default" />
<jsp:include flush="true" page="popupmenu.jsp"></jsp:include>

<c:if test="${RenderContext.banner!=null}">
	<script type="text/javascript"
		src="${contextPath}/scripts/ajax/langmenu.js"></script>
	<c:if test="${RenderContext.showUserbox==true}">
		<jsp:include page="UserBox.jsp" />
	</c:if>

	<vwb:render content="${RenderContext.banner}" />
</c:if>
<c:if test="${RenderContext.showUserbox==true}">
	<script type="text/javascript">
		$(document).ready(function(){
			modifyUserbox();
		});
	</script>
</c:if>