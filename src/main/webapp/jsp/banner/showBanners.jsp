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
<logic:notEmpty name="errorInfo">
   <font color="red" size="3">
     <bean:write name="errorInfo"/>
   </font>
</logic:notEmpty>
<logic:empty name="errorInfo">
<form action="5008" id="bannerManager">
<input name="method" type="hidden" value="newBanner" />
<table class="banner_manager_table">
  <tr>
    <td><font size="5" color="#2292dd"><fmt:message key="banner.manager.scenario" /></font></td>
  </tr>
</table>
<table class="banner_manager_table" border="1" cellpadding="0" cellspacing="0" >

     <tr class="banner_manager_table_tr_1"><td ><fmt:message key="banner.manager.bannerName" /> </td> <td><fmt:message key="banner.manager.bannerCreator" /></td><td><fmt:message key="banner.manager.delete" /></td><td> <fmt:message key="banner.update.tip" /></td> </tr>
     
     <c:forEach var="item" items='${requestScope.showbanners}' varStatus="status">
		 <tr class="banner_manager_table_tr_2">
		       <td><a href="5008?method=viewBanner&bannerId=<c:out value='${item.id}'/>">  <c:out value='${item.name}'/>  </a> </td>
		       <td><c:out value='${item.creator}'/></td>
		       <td>
		        <c:choose>
		           <c:when test="${item.status==2 or item.system}">
		             <input type="button" disabled="disabled" value="<fmt:message key="banner.manager.delete" />"> 
		           </c:when>
		           <c:when test="${item.status==1}">
		           <input type="button"  onclick="if(confirm('<fmt:message key='banner.manager.delete' /> <c:out value='${item.name}'/>?'))window.location='5008?method=deleteBanner&bannerId=<c:out value='${item.id}'/>';" value="<fmt:message key="banner.manager.delete" />"/>
		           </c:when>
		        </c:choose>
		        <td> <a href="5008?method=openUpload&bannerId=<c:out value='${item.id}'/>"><fmt:message key="banner.update.tip" /></a>  </td>
		 </tr>
	</c:forEach>
</table>
<p>&nbsp;</p>

<table class="banner_manager_table"  cellpadding="0" cellspacing="0" >
     <tr>
        <td align="center"><html:submit><fmt:message key="banner.manager.new" /></html:submit>  </td>
     </tr>

</table>

</form>
</logic:empty>
	 <div class="clear"></div>