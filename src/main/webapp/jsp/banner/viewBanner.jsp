<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ page import="cn.vlabs.duckling.vwb.service.banner.Banner"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="templates.default"/>
<div>
<logic:notEmpty name="errorInfo">
   <font color="red" size="3">
     <bean:write name="errorInfo"/>
   </font>
</logic:notEmpty>
 <logic:notEmpty name="viewBanner">
	 <table width="100%">
	     <tr>
	      <td colspan="3">
	      <%Banner viewBanner = (Banner)request.getAttribute("viewBanner"); %>
	      
            <%
              request.setAttribute("realBannerTitleContent",request.getAttribute("bannerTitleContent"));
              request.setAttribute("realBanner",viewBanner);
              request.setAttribute("preview","preview");
              request.setAttribute("realBannerRoot", request.getAttribute("tempRealBannerRoot"));
            %>
             <jsp:include page="/WEB-INF/banner/jsp/type${viewBanner.type}.jsp"/>
		   </td>
	    </tr>
	 </table>
 </logic:notEmpty>
 <table>
    <tr> <td colspan="3" align="center"><br/>
         <input type='button' value="<fmt:message key='version.return' />" onclick="window.location.href='5008?method=showBanners';"/>
         </td> 
    </tr>
 </table>

</div>