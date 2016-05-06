<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" media="screen, projection, print" type="text/css"
	href="${realBanner.cssUrl}" />
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb" %>
<div id="header" class="type2_background">
	<div class="type2_logo"></div>
	<div class="type2_font">
		<a href="<vwb:Link page='1' format='url'/>"  class="BannerFont"><c:out value="${realBannerTitleContent}" escapeXml="false"></c:out></a>
	</div>
</div>