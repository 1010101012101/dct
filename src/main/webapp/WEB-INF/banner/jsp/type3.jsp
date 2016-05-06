<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" media="screen, projection, print" type="text/css"
	href="${realBanner.cssUrl}" />
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>


<div id="header" style="line-height: 0; position:relative; margin-top:0; min-height:90px;" class="customBanner">
	<div class="imageDiv1">
		<div  class="imageDiv2">
		  	<img id="type3_background_img_id"  src="${realBanner.leftPictureUrl}"> 
		</div>
	</div>

	<div class="clear"></div>   
	<div style="width:900px; position:absolute; text-align:left; bottom:20%; left:50px; margin:0 auto;">
		<c:if test="${not empty realBanner.firstTitle}">
			<div class="innerBanner_title"  style="position:relative;">
				<a class="BannerFont" href="/dct/page/1">${realBanner.firstTitle}</a>
			</div>
		</c:if>
		<c:if test="${not empty realBanner.secondTitle}">
			<div class="innerBanner_date" style="position:relative; top:20px;">
				<a href="/dct/page/1"><span class="banner_date">${realBanner.secondTitle}</span></a>
			</div>
		</c:if>
		<c:if test="${not empty realBanner.thirdTitle}">
			<div class="innerBanner_location" style="position:relative; top:20px;">
				<a href="/dct/page/1"><span class="banner_location">${realBanner.thirdTitle}</span></a>
			</div>
		</c:if>
	</div>
</div>