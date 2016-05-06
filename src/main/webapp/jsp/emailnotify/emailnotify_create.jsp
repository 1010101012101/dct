<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="WEB-INF/tld/vwb.tld" prefix="vwb"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="templates.default" />
<%
pageContext.setAttribute("locale", request.getLocale()); //page.resourceId
%>
<font color="red"> <html:errors locale="${locale}" /> </font>
<form action="<vwb:LinkTo format="url"/>?a=subscribe"
	name="pageInfoForm" method="post" class="esubManage">
	<input type="hidden" name="act" value="save" />
	<input type="hidden" name="returnUrl" />
	<input type="hidden" name="page" value="${page.title}" />

	<input type="hidden" name="deleteESubId" id="deleteESubid" value="" />

	<table class='wikitable'>
		<tr>
			<td>
				<fmt:message key="emailnotifier.page" />
			</td>
			<td>
				<select name="pageName" id="banner_selectTypeId">
					<option value="<c:out value='${page.resourceId}'/>"
						<c:if test="${pageName!='*' }">selected</c:if>>
						<c:out value='${page.title}' />
					</option>
					<option value="*" <c:if test="${pageName=='*' }">selected</c:if>>
						<fmt:message key="emailnotifier.allpages" />
					</option>
				</select>
			</td>
			<td>
				<fmt:message key="emailnotifier.hour" />
			</td>
			<td>
				<html:text property="hour" value="${hour}" />
				&nbsp;&nbsp;(
				<font color="blue"><fmt:message key="emailnotifier.timetip" />
				</font>)
			</td>

		</tr>
		<tr>
			<td>
				<fmt:message key="emailnotifier.email" />
			</td>
			<td colspan="3">
				<html:text property="emails" value="${emails}" style="width:390px"
					size="60" />
				&nbsp;&nbsp;(
				<font color="blue"><fmt:message key="emailnotifier.emailtip" />
				</font>)
			</td>

		</tr>


	</table>

	<table>
		<tr>
			<td>
				<html:submit>
					<fmt:message key="emailnotifier.submit" />
				</html:submit>
				&nbsp;&nbsp;&nbsp;
				<input type='button'
					value="<fmt:message key='emailnotifier.cancel' /> "
					onclick="window.location='<vwb:Link format='url'/>'" />
			</td>
		</tr>
	</table>
	<hr>
	<table class="DCT_wikitable">
		<logic:notEmpty name="createdESub">
			<tr>
				<th colspan='4' align='center'>
					<font color='red'> <fmt:message
							key="emailnotifier.createdList" /> </font>
				</th>
			</tr>
			<tr>
				<th>
					<fmt:message key="emailnotifier.page" />
				</th>
				<th>
					<fmt:message key="emailnotifier.email" />
				</th>
				<th>
					<fmt:message key="emailnotifier.hour" />
				</th>
			</tr>
			<c:forEach var="item" items='${requestScope.createdESub}'
				varStatus="status">
				<tr>

					<td>
						<c:choose>
							<c:when test="${ '*' == item.resourceId }">
								<fmt:message key="emailnotifier.allpages" />
							</c:when>
							<c:otherwise>
								<c:out value='${item.pageTitle}' />
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:out value='${item.receiver}' />
					</td>
					<td>
						<c:out value='${item.rec_time}' />
					</td>
				</tr>
			</c:forEach>
		</logic:notEmpty>
		<logic:notEmpty name="failedESub">
			<tr>
				<th colspan='4' align='center'>
					<font color='red'> <fmt:message
							key="emailnotifier.createFailedList" /> </font>
				</th>
			</tr>
			<tr>


				<th>
					<fmt:message key="emailnotifier.page" />
				</th>
				<th>
					<fmt:message key="emailnotifier.email" />
				</th>
				<th>
					<fmt:message key="emailnotifier.hour" />
				</th>
			</tr>
			<c:forEach var="item" items='${requestScope.failedESub}'
				varStatus="status">
				<tr>

					<td>
						<c:choose>
							<c:when test="${ '*' == item.resourceId }">
								<fmt:message key="emailnotifier.allpages" />
							</c:when>
							<c:otherwise>
								<c:out value='${item.pageTitle}' />
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:out value='${item.receiver}' />
					</td>
					<td>
						<c:out value='${item.rec_time}' />
					</td>
				</tr>
			</c:forEach>
		</logic:notEmpty>
		<logic:notEmpty name="existedESub">
			<tr>
				<th colspan='4' align='center'>
					<font color='red'> <fmt:message
							key="emailnotifier.existedList" /> </font>
				</th>
			</tr>
			<tr>

				<th>
					<fmt:message key="emailnotifier.creator" />
				</th>
				<th>
					<fmt:message key="emailnotifier.page" />
				</th>
				<th>
					<fmt:message key="emailnotifier.email" />
				</th>
				<th>
					<fmt:message key="emailnotifier.hour" />
				</th>
			</tr>
			<c:forEach var="item" items='${requestScope.existedESub}'
				varStatus="status">
				<tr>
					<td>
						<c:out value='${item.notify_creator}' />
					</td>
					<td>
						<c:choose>
							<c:when test="${ '*' == item.resourceId }">
								<fmt:message key="emailnotifier.allpages" />
							</c:when>
							<c:otherwise>
								<c:out value='${item.pageTitle}' />
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:out value='${item.receiver}' />
					</td>
					<td>
						<c:out value='${item.rec_time}' />
					</td>
				</tr>
			</c:forEach>
		</logic:notEmpty>
	</table>


	<h3>
		<fmt:message key="emailnotifier.emailList" />
	</h3>


	<table class="DCT_wikitable">
		<tr>

			<th>
				<fmt:message key="emailnotifier.email" />
			</th>
			<th>
				<fmt:message key="emailnotifier.page" />
			</th>

			<th>
				<fmt:message key="emailnotifier.hour" />
			</th>
			<th>
				<fmt:message key="emailnorifier.unsubscriber" />
			</th>
		</tr>

		<c:forEach var="item" items='${requestScope.eMailSubscribers}'
			varStatus="status">
			<tr>

				<td>

					<c:out value='${item.receiver}' />
				</td>
				<td>
					<c:choose>
						<c:when test="${ '*' == item.resourceId }">
							<fmt:message key="emailnotifier.allpages" />
						</c:when>
						<c:otherwise>
							<c:out value='${item.pageTitle}' />
						</c:otherwise>
					</c:choose>

				</td>

				<td>
					<c:out value='${item.rec_time}' />
				</td>
				<td>
					<BUTTON onclick="delsubmit('${item.id}');">
						<img
							src='<%=request.getContextPath()%>/images/common/unsubscribe.png' />
					</button>
				</td>
			</tr>
		</c:forEach>
	</table>
</form>
<script type="text/javascript" type="text/javascript">
<!--
function delsubmit(itemId)
{
	document.pageInfoForm.act.value="delete";
	document.pageInfoForm.deleteESubId.value=itemId;
	document.pageInfoForm.submit();
}
//-->
</script>

