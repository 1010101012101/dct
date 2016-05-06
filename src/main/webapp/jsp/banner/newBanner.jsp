<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="templates.default"/>
<% 
	pageContext.setAttribute("locale",request.getLocale());
%>
<fmt:setBundle basename="templates.default"/>
<logic:notEmpty name="errorInfo">
   <font color="red" size="3">
     <bean:write name="errorInfo"/>
   </font>
</logic:notEmpty>
<form action="5008" id="bannerForm" method="post" enctype="multipart/form-data">
 <input name="method" value="saveBanner" type="hidden"/>
 <table>
    <tr>
       <td> 
            <table>
                <tr> <td><fmt:message key="banner.manager.bannerName" />:</td> <td>  <input name="bannerName" type="text" value="${newbanner.name}"/> </td>  </tr>
                 <tr> <td>第一标题:</td> <td>  <input name="firstTitle" type="text" value="${newbanner.firstTitle}"/> </td>  </tr>
                  <tr> <td>第二标题:</td> <td>  <input name="secondTitle" type="text" value="${newbanner.secondTitle}"/> </td>  </tr>
                   <tr> <td>第三标题:</td> <td>  <input name="thirdTitle" type="text" value="${newbanner.thirdTitle}"/> </td>  </tr>
                <tr><td> <fmt:message key="banner.manager.bannerType3.logopic" /> ：</td><td>  <input name="leftFile" type="file"/> </td></tr>
                <tr><td align="left"><fmt:message key="banner.manager.edit.public" />：</td>
                     <td> 
                      <c:if test="${publicBanner!=null}">
                        <input name="publicBanner" type="checkbox" checked="checked"/> 
                      </c:if>
                       <c:if test="${publicBanner==null}">
                        <input name="publicBanner" type="checkbox"/> 
                      </c:if>
                     </td>
                </tr>
            </table>
       </td>
   </tr>
 </table>
 <logic:notEmpty name="newbanner">
    
     <input name="bannerDir" value="${newbanner.dirName}" type="hidden"/>
	 <table width="100%">
	     <tr>
	      <td colspan="3">
	      <c:choose>
				<c:when test="${newbanner.type==3}">
						<div id="headerview">
	                      <table cellpadding="0" cellspacing="0">
	                          <tr>
	                             <td>
					            <img src="${siteBannerRootDir}/images/<c:out value='${newbanner.tempLeftName}'/>">
	                            </td>
	                          </tr>
	                      </table>
					   </div>
				</c:when>
	      </c:choose>
	         
		   </td>
	    </tr>
	 </table>
 </logic:notEmpty>
 <table>
    <tr> <td colspan="3" align="center"><br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <input type="submit" value="<fmt:message key="fact.submitter" />">
         &nbsp;&nbsp;&nbsp;&nbsp; <input type="button" value="<fmt:message key="preview.tab" />" onclick="preview()">   &nbsp;&nbsp;&nbsp;&nbsp; <input type='button' value="<fmt:message key='version.return' />" onclick="window.location.href='5008?method=showBanners';"/>
         </td> 
    </tr>
 </table>
</form>

 <script language="javascript" type="text/javascript">
    function preview()
    {
      var form = document.getElementById("bannerForm");
      form.action = "5008?method=preview";
      form.submit();
    
    }  
</script>
 <div class="clear"></div>