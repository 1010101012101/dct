<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<vwb:Variable var="skin" key="skin" />

<div id="header" class="banner">
	<div class="innerBanner" style="position: inherit;">
		<c:if test="${not empty realBanner.firstTitle}">

			<div class="innerBanner_title">
				<a class="BannerFont" href="/dct/page/1">${realBanner.firstTitle}</a>
			</div>
		</c:if>
		<c:if test="${not empty realBanner.secondTitle}">
			<div class="innerBanner_date">
				<a href="/dct/page/1"><span class="banner_date">
						${realBanner.secondTitle}</span></a>

			</div>
		</c:if>
		<c:if test="${not empty realBanner.thirdTitle}">

			<div class="innerBanner_location">
				<a href="/dct/page/1"><span class="banner_location">${realBanner.thirdTitle}</span></a>
			</div>
		</c:if>
	</div>
</div>

