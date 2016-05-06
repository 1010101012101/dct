<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<link rel="stylesheet" media="screen, projection, print" type="text/css"
	href="${realBanner.cssUrl}" />
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb" %>

<div  id="header<c:out value="${preview}"/>" class="DCT_banner">
	<div class="DCT_banner_right">
	    <div id="banner_third_title<c:out value="${preview}"/>" style="position: absolute;">
		     <!--  table  class="DCT_banner_font_setting">
			  <tr>
			    <td>
			     <a href="<vwb:Link page='1' format='url'/>"  class="BannerFont"> <c:out value="${realBannerTitleContent}" escapeXml="false"></c:out> </a>
			    </td>
			 </tr>
			</table>-->
		</div>
		<div class="DCT_banner_font">
	    </div>
	</div>	
</div>
<script type="text/javascript">
function addthirdtile<c:out value="${preview}"/>() {
	var box = document.getElementById("banner_third_title<c:out value="${preview}"/>");
	var header = 'header<c:out value="${preview}"/>';
	var pos = getElementPos(header);
	var boxleft=pos.x;
	box.style.left = boxleft+"px";
	box.style.top=pos.y+"px";
}
if("addthirdtile<c:out value="${preview}"/>()".length>14)
{
  setTimeout("addthirdtile<c:out value="${preview}"/>()",1);
}else
{
   addthirdtile<c:out value="${preview}"/>()
}

 </script>