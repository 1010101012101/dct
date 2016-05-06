<%@ taglib uri="/WEB-INF/tld/vwb.tld" prefix="vwb" %>
<%@ page import="cn.vlabs.duckling.vwb.*"%>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fmt:setBundle basename="templates.default"/>

<% 
  VWBContext c = VWBContext.getContext(request);  
  List<?> history = VWBContext.getContainer().getDpageService().getDpageVersionsByResourceId(c.getSite().getId(),c.getResource().getResourceId());
  pageContext.setAttribute( "history", history );
 %>

<vwb:PageExists>
<form action="<vwb:LinkTo format='url'/>?a=diff" method="post" accept-charset="UTF-8">
<div class="DCT_collapsebox" id="diffcontent">
  <h4>
       <input type="hidden" name="page" value="<vwb:Variable key='pagename' />" />
       <fmt:message key="diff.difference">
         <fmt:param>
           <select id="r1" name="r1" onchange="this.form.submit();" >
           <c:forEach items="${history}" var="i">
             <option value="<c:out value='${i.version}'/>" <c:if test="${i.version == version}">selected="selected"</c:if> ><c:out value="${i.version}"/></option>
           </c:forEach>
           </select>
         </fmt:param>
         <fmt:param>
           <select id="r2" name="r2" onchange="this.form.submit();" >
           <c:forEach items="${history}" var="i">
             <option value="<c:out value='${i.version}'/>" <c:if test="${i.version == compareTo}">selected="selected"</c:if> ><c:out value="${i.version}"/></option>
           </c:forEach>
           </select>
         </fmt:param>
       </fmt:message>
  </h4>
  
  <div class="diffbody">
    <vwb:InsertDiff ><i><fmt:message key="diff.nodiff"/></i></vwb:InsertDiff> 
  </div>
</div>
</form>
</vwb:PageExists>